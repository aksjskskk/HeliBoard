import re

strip_container_file = "app/src/main/res/layout/strip_container.xml"
with open(strip_container_file, "r") as f:
    content = f.read()

# Change height to wrap_content so it can expand
content = content.replace('android:layout_height="@dimen/config_suggestions_strip_height"', 'android:layout_height="wrap_content"')

with open(strip_container_file, "w") as f:
    f.write(content)

print("Updated strip_container.xml")
