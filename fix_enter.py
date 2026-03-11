import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Replace ENTER back to Constants.CODE_ENTER
content = content.replace("helium314.keyboard.keyboard.internal.keyboard_parser.floris.KeyCode.ENTER", "helium314.keyboard.latin.common.Constants.CODE_ENTER")

with open(latin_ime_file, "w") as f:
    f.write(content)
