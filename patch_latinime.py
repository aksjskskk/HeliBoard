import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Add translation UI variables
ui_vars_pattern = r"(private boolean mTranslationModeEnabled = false;\n\s+private View mTranslationStrip;\n\s+private android\.widget\.Spinner mSourceLangSpinner;\n\s+private android\.widget\.Spinner mTargetLangSpinner;\n\s+private com\.google\.mlkit\.nl\.translate\.Translator mTranslator;)"
ui_vars_replacement = r"""\1
    private android.widget.TextView mTranslationInputTextView;
    private StringBuilder mTranslationInputBuffer = new StringBuilder();
"""
content = re.sub(ui_vars_pattern, ui_vars_replacement, content)

# Update UI initialization
ui_init_pattern = r"(mTranslationStrip = view.findViewById\(R.id.translation_strip\);\n\s+if \(mTranslationStrip != null\) \{)([\s\S]*?)(View closeBtn = mTranslationStrip.findViewById\(R.id.btn_translation_close\);)"
ui_init_replacement = r"""\1\2mTranslationInputTextView = mTranslationStrip.findViewById(R.id.translation_input_buffer);

            \3"""
content = re.sub(ui_init_pattern, ui_init_replacement, content)

# Toggle translation mode clears buffer
toggle_pattern = r"(public void toggleTranslationMode\(\) \{[\s\S]*?mTranslationModeEnabled = !mTranslationModeEnabled;)"
toggle_replacement = r"""\1
        if (!mTranslationModeEnabled) {
            clearTranslationBuffer();
            if (mTranslator != null) {
                mTranslator.close();
                mTranslator = null;
            }
        }"""
content = re.sub(toggle_pattern, toggle_replacement, content)

# Check clear translation buffer
if "clearTranslationBuffer()" not in content:
    clear_method = """
    private void clearTranslationBuffer() {
        mTranslationInputBuffer.setLength(0);
        if (mTranslationInputTextView != null) {
            mTranslationInputTextView.setText("");
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.finishComposingText();
        }
    }
"""
    content = content.replace("public void toggleTranslationMode() {", clear_method + "\n    public void toggleTranslationMode() {")

with open(latin_ime_file, "w") as f:
    f.write(content)

print("Updated LatinIME.java with translation UI vars and clear method")
