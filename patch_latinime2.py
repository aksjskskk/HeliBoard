import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Make sure we got it
if "private android.widget.TextView mTranslationInputTextView;" not in content:
    print("WARNING: Could not insert UI vars.")
else:
    print("SUCCESS: Inserted UI vars.")

if "private void clearTranslationBuffer()" not in content:
    print("WARNING: Could not insert clear method.")
else:
    print("SUCCESS: Inserted clear method.")
