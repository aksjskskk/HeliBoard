import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Try to find where braces are unmatched
lines = content.splitlines()
balance = 0
for i, line in enumerate(lines):
    balance += line.count('{')
    balance -= line.count('}')
    if balance < 0:
        print(f"Error at line {i+1}: {line}")
        print(f"Previous 10 lines:\n" + "\n".join(lines[i-10:i]))
        break
