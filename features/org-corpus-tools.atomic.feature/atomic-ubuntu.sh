#/bin/bash
echo "Setting the environment variable \"UBUNTU_MENUPROXY\" to \"0\".
This has the effect that the Unity global menu is not used (on, e.g., Ubuntu).
Instead, the menu is relocated to the application menu.
If this isn't set, keyboard shortcut texts aren't visible in the menus."
UBUNTU_MENUPROXY=0 ./atomic