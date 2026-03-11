import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Let's optimize translator creation and ensure we handle the 'enter'/'translate' button.
# Replace performLiveTranslation to be more efficient
optimal_live_translation = """
    private String mLastSourceLang = "";
    private String mLastTargetLang = "";

    private void performLiveTranslation() {
        if (mTranslationInputBuffer.length() == 0) {
            android.view.inputmethod.InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.commitText("", 1); // clear composing text
            }
            return;
        }

        String sourceLang = mSourceLangSpinner != null ? (String) mSourceLangSpinner.getSelectedItem() : "en";
        String targetLang = mTargetLangSpinner != null ? (String) mTargetLangSpinner.getSelectedItem() : "es";

        if (mTranslator == null || !sourceLang.equals(mLastSourceLang) || !targetLang.equals(mLastTargetLang)) {
            if (mTranslator != null) {
                mTranslator.close();
            }
            com.google.mlkit.nl.translate.TranslatorOptions options = new com.google.mlkit.nl.translate.TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(targetLang)
                    .build();
            mTranslator = com.google.mlkit.nl.translate.Translation.getClient(options);
            mLastSourceLang = sourceLang;
            mLastTargetLang = targetLang;
        }

        com.google.mlkit.common.model.DownloadConditions conditions = new com.google.mlkit.common.model.DownloadConditions.Builder()
                .build();

        mTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(v -> {
                    mTranslator.translate(mTranslationInputBuffer.toString())
                            .addOnSuccessListener(translatedText -> {
                                android.view.inputmethod.InputConnection ic = getCurrentInputConnection();
                                if (ic != null && mTranslationModeEnabled) {
                                    ic.setComposingText(translatedText, 1);
                                }
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("LatinIME", "Translation failed", e);
                            });
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("LatinIME", "Model download failed", e);
                });
    }

    private void handleTranslationInput(int codePoint) {
        if (codePoint == helium314.keyboard.latin.common.Constants.CODE_DELETE) {
            if (mTranslationInputBuffer.length() > 0) {
                // Remove last character (handling surrogate pairs correctly)
                int lastCharIndex = mTranslationInputBuffer.length() - 1;
                if (Character.isLowSurrogate(mTranslationInputBuffer.charAt(lastCharIndex)) && lastCharIndex > 0 && Character.isHighSurrogate(mTranslationInputBuffer.charAt(lastCharIndex - 1))) {
                    mTranslationInputBuffer.delete(lastCharIndex - 1, lastCharIndex + 1);
                } else {
                    mTranslationInputBuffer.deleteCharAt(lastCharIndex);
                }
            }
        } else if (codePoint == helium314.keyboard.latin.common.Constants.CODE_ENTER) {
            // Commit text
            android.view.inputmethod.InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
            toggleTranslationMode();
            return;
        } else if (codePoint >= 32) { // printable characters
            mTranslationInputBuffer.appendCodePoint(codePoint);
        }

        if (mTranslationInputTextView != null) {
            if (mTranslationInputBuffer.length() == 0) {
                mTranslationInputTextView.setText("");
                mTranslationInputTextView.setHint("Type here to translate...");
            } else {
                mTranslationInputTextView.setText(mTranslationInputBuffer.toString());
            }
        }

        // Trigger translation
        performLiveTranslation();
    }
"""

# Find the existing handleTranslationInput and performLiveTranslation
pattern = r"private void handleTranslationInput\(int codePoint\) \{[\s\S]*?performLiveTranslation\(\);\n    \}[\s\n]*private void performLiveTranslation\(\) \{[\s\S]*?addOnFailureListener\(e -> \{\n                    android\.util\.Log\.e\(\"LatinIME\", \"Model download failed\", e\);\n                \}\);\n    \}"

content = re.sub(pattern, optimal_live_translation.strip(), content)

# Check if performTranslation() method was existing from earlier and fix it to performLiveTranslation or commit
# I saw `translateBtn.setOnClickListener(v -> performTranslation());` earlier. Let's make it commit and close
content = content.replace("v -> performTranslation()", "v -> { android.view.inputmethod.InputConnection ic = getCurrentInputConnection(); if (ic != null) ic.finishComposingText(); toggleTranslationMode(); }")

# Let's remove any stale performTranslation()
content = re.sub(r"private void performTranslation\(\) \{[\s\S]*?\}", "", content)

with open(latin_ime_file, "w") as f:
    f.write(content)
