import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Let's see lines around 904 to 914
lines = content.splitlines()
start = 890
end = 920
print("\n".join(lines[start:end]))
