import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# I see handleTranslationInput logic needs to make sure we use the codePoint correctly.
# Constants.CODE_DELETE is -5.
# Let's verify Constants import
if "helium314.keyboard.latin.common.Constants" not in content:
    print("Warning: Constants not imported directly")

# What about Constants.CODE_SPACE? In LatinIME space is handled usually via CODE_SPACE or simply checking codePoint == ' '
if "Constants.CODE_SPACE" not in content and "CODE_SPACE" not in content:
    # Actually LatinIME usually has Constants.CODE_SPACE
    print("Checking CODE_SPACE")
