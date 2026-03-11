import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# I see compilation errors related to `toggleTranslationMode`.
# "e: file:///app/app/src/main/java/helium314/keyboard/keyboard/KeyboardActionListenerImpl.kt:108:26 Unresolved reference 'toggleTranslationMode'."
# In LatinIME, is `toggleTranslationMode` public? Yes I saw "public void toggleTranslationMode()".
# Did I break `LatinIME` class parsing entirely? Like missing a bracket?
# Check for unmatched braces.
open_braces = content.count("{")
close_braces = content.count("}")

print("Braces count:")
print("Open:", open_braces)
print("Close:", close_braces)

# If they don't match, we have a problem.
