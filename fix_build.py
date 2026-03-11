import re

build_file = "app/build.gradle.kts"
with open(build_file, "r") as f:
    content = f.read()

# Add a wildcard for everything else that could be failing.
# And maybe "META-INF/DEPENDENCIES" is added twice, let's fix it.
content = content.replace('resources.excludes.add("META-INF/DEPENDENCIES")\n        resources.excludes.add("META-INF/INDEX.LIST")', 'resources.excludes.add("META-INF/INDEX.LIST")')

# We can also just ignore everything in META-INF that could cause conflicts except what's needed.
# Let's add more exclusions.
extra_excludes = """
        resources.excludes.add("META-INF/DEPENDENCIES.txt")
        resources.excludes.add("META-INF/dependencies.txt")
        resources.excludes.add("META-INF/LGPL2.1")
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/com.android.tools/**")
        resources.excludes.add("META-INF/gradle/**")
        resources.excludes.add("META-INF/versions/**")
"""

pattern = r"(resources\.excludes\.add\(\"META-INF/com.android.tools/proguard/coroutines\.pro\"\))"
content = re.sub(pattern, r"\1" + extra_excludes, content)

with open(build_file, "w") as f:
    f.write(content)
