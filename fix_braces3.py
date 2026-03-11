import re

latin_ime_file = "app/src/main/java/helium314/keyboard/latin/LatinIME.java"
with open(latin_ime_file, "r") as f:
    content = f.read()

# Remove the broken lines
broken_code = """    )
                    .addOnFailureListener(e -> Log.e(TAG, "Translation failed", e));
            })
            .addOnFailureListener(e -> Log.e(TAG, "Model download failed", e));
    }"""
content = content.replace(broken_code, "")

with open(latin_ime_file, "w") as f:
    f.write(content)
