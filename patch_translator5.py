import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Let's ensure the user can select spaces
if "mTranslationInputBuffer.append(\" \");" not in content and "mTranslationInputBuffer.appendCodePoint(codePoint);" in content:
    # Actually wait, appendCodePoint(32) works.
    print("Space handled by codePoint >= 32")

# Check if translateBtn got changed properly
if "translateBtn.setOnClickListener(v -> {" in content:
    print("Translate button updated.")
else:
    print("Translate button update failed.")
