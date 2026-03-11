import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Make sure Spinner selection changes trigger re-translation
spinner_listener = """
            android.widget.AdapterView.OnItemSelectedListener langChangeListener = new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                    performLiveTranslation();
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            };
            if (mSourceLangSpinner != null) mSourceLangSpinner.setOnItemSelectedListener(langChangeListener);
            if (mTargetLangSpinner != null) mTargetLangSpinner.setOnItemSelectedListener(langChangeListener);
"""
if "OnItemSelectedListener langChangeListener =" not in content:
    content = content.replace(
        "if (mTargetLangSpinner != null) mTargetLangSpinner.setAdapter(adapter);",
        "if (mTargetLangSpinner != null) mTargetLangSpinner.setAdapter(adapter);\n" + spinner_listener
    )

with open(latin_ime_file, "w") as f:
    f.write(content)
