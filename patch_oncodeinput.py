import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Add logic to intercept keystrokes when translation mode is enabled
# Need to check where to hook into onCodeInput.
# Let's see the beginning of onCodeInput
pattern = r"(public void onCodeInput\(final int codePoint, final int x, final int y, final boolean isKeyRepeat\) \{)"
replacement = r"""\1
        if (mTranslationModeEnabled) {
            handleTranslationInput(codePoint);
            return;
        }"""
content = re.sub(pattern, replacement, content)

# Add handleTranslationInput method
translation_input_method = """
    private void handleTranslationInput(int codePoint) {
        if (codePoint == Constants.CODE_DELETE) {
            if (mTranslationInputBuffer.length() > 0) {
                // Remove last character (handling surrogate pairs correctly)
                int lastCharIndex = mTranslationInputBuffer.length() - 1;
                if (Character.isLowSurrogate(mTranslationInputBuffer.charAt(lastCharIndex)) && lastCharIndex > 0 && Character.isHighSurrogate(mTranslationInputBuffer.charAt(lastCharIndex - 1))) {
                    mTranslationInputBuffer.delete(lastCharIndex - 1, lastCharIndex + 1);
                } else {
                    mTranslationInputBuffer.deleteCharAt(lastCharIndex);
                }
            }
        } else if (codePoint == Constants.CODE_ENTER) {
            // Commit text
            android.view.inputmethod.InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
            toggleTranslationMode();
            return;
        } else if (codePoint >= 32) { // printable characters
            mTranslationInputBuffer.appendCodePoint(codePoint);
        } else if (codePoint == Constants.CODE_SPACE) {
            mTranslationInputBuffer.append(" ");
        }

        if (mTranslationInputTextView != null) {
            if (mTranslationInputBuffer.length() == 0) {
                mTranslationInputTextView.setText("");
            } else {
                mTranslationInputTextView.setText(mTranslationInputBuffer.toString());
            }
        }

        // Trigger translation
        performLiveTranslation();
    }

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

        com.google.mlkit.nl.translate.TranslatorOptions options = new com.google.mlkit.nl.translate.TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLang)
                .build();

        if (mTranslator != null) {
            mTranslator.close();
        }
        mTranslator = com.google.mlkit.nl.translate.Translation.getClient(options);

        com.google.mlkit.common.model.DownloadConditions conditions = new com.google.mlkit.common.model.DownloadConditions.Builder()
                .requireWifi()
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
"""

if "private void handleTranslationInput(int codePoint)" not in content:
    # Insert it before toggleTranslationMode
    content = content.replace("public void toggleTranslationMode()", translation_input_method + "\n    public void toggleTranslationMode()")

with open(latin_ime_file, "w") as f:
    f.write(content)

print("Updated LatinIME.java with handleTranslationInput and performLiveTranslation")
