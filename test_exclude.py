import re

build_file = "app/build.gradle.kts"
with open(build_file, "r") as f:
    content = f.read()

# Add excludes that might be causing duplicate file exceptions during packaging.
# In a previous step I saw: "Added com.google.mlkit:translate:17.0.2 and kotlinx-coroutines-play-services:1.7.3 dependencies. Fixed resulting APK packaging crashes by excluding conflicting META-INF files in build.gradle.kts."
# But the CI still failed with `PackageAndroidArtifact$IncrementalSplitterRunnable`.
# Let's add more common excludes.

pattern = r"(resources\.excludes\.add\(\"META-INF/\*\.kotlin_module\"\))"
replacement = r"""\1
        resources.excludes.add("META-INF/DEPENDENCIES")
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/io.netty.versions.properties")
        resources.excludes.add("META-INF/kotlinx-coroutines-core.kotlin_module")
        resources.excludes.add("META-INF/kotlinx-coroutines-play-services.kotlin_module")
        resources.excludes.add("META-INF/com.google.dagger_dagger.version")
        resources.excludes.add("META-INF/com.android.tools/proguard/coroutines.pro")"""

if "INDEX.LIST" not in content:
    content = re.sub(pattern, replacement, content)
    with open(build_file, "w") as f:
        f.write(content)
    print("Added META-INF excludes to build.gradle.kts")
else:
    print("Excludes already exist.")
