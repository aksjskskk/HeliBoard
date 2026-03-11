import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# I see `CODE_DELETE` is an error.
# Let's check Constants.java to see what it's really called.
