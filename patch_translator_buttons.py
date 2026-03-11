import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Make sure buttons like "swap" triggers re-translation
swap_btn_pattern = r"(swapBtn.setOnClickListener\(v -> \{[\s\S]*?mTargetLangSpinner.setSelection\(srcPos\);\n\s+\}\);)"
swap_btn_replacement = r"""\1.replace("});", "    performLiveTranslation();\n            });")"""
# Wait, let's just do a simple replace
if "mTargetLangSpinner.setSelection(srcPos);" in content:
    content = content.replace(
        "mTargetLangSpinner.setSelection(srcPos);\n            });",
        "mTargetLangSpinner.setSelection(srcPos);\n                performLiveTranslation();\n            });"
    )

with open(latin_ime_file, "w") as f:
    f.write(content)

print("Updated LatinIME.java with UI interactions for Translation mode")
