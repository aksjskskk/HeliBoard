import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Replace CODE_DELETE and CODE_ENTER
content = content.replace("helium314.keyboard.latin.common.Constants.CODE_DELETE", "helium314.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode.DELETE")
content = content.replace("helium314.keyboard.latin.common.Constants.CODE_ENTER", "helium314.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode.ENTER")

with open(latin_ime_file, "w") as f:
    f.write(content)
