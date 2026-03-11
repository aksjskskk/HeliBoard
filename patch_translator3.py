import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Replace Constants references with fully qualified if necessary
# LatinIME already imports Constants usually
if "import helium314.keyboard.latin.common.Constants" in content:
    print("Constants already imported.")
else:
    content = content.replace("import helium314.keyboard.latin.common.LocaleUtils;", "import helium314.keyboard.latin.common.LocaleUtils;\nimport helium314.keyboard.latin.common.Constants;")

# We need to make sure handleTranslationInput runs on UI thread or handles composing text properly
# What if the user taps outside or closes? We need to finish composing.
# Also the user mentioned: "Only explicitly commitText when the user closes the translation mode, hits 'enter', or presses a specific confirm button."
# The UI has a "translate" button (btn_translation_translate). What should it do? Maybe commit text and clear.

# Update handleTranslationInput to clear after enter
update_method = """
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

content = re.sub(r"private void handleTranslationInput\(int codePoint\) \{[\s\S]*?performLiveTranslation\(\);\n    \}", update_method.strip(), content)

# Check translation perform Translation
# Replace any performTranslation if it existed
perform_translation_replace = """
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
            // Instead of closing, maybe just rely on the new one, or don't re-instantiate if same languages.
            // For simplicity in a live env, let's keep it simple. If we change languages, we should get a new client.
            // But getting a new client on every keystroke might be heavy. Let's optimize.
        }
"""

with open(latin_ime_file, "w") as f:
    f.write(content)
