import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Make sure clear method is there
if "clearTranslationBuffer()" in content and "private void clearTranslationBuffer()" not in content:
    clear_method = """
    private void clearTranslationBuffer() {
        if (mTranslationInputBuffer != null) {
            mTranslationInputBuffer.setLength(0);
        }
        if (mTranslationInputTextView != null) {
            mTranslationInputTextView.setText("");
            mTranslationInputTextView.setHint("Type here to translate...");
        }
        android.view.inputmethod.InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.finishComposingText();
        }
    }
"""
    content = content.replace("public void toggleTranslationMode() {", clear_method + "\n    public void toggleTranslationMode() {")

with open(latin_ime_file, "w") as f:
    f.write(content)

print("Updated LatinIME.java with clear method")
