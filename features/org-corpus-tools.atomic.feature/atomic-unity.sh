#  /bin/bash
#  What is 'atomic-unity.sh'?
#  ==========================
#  
#  'atomic-unity.sh' is a custom launcher script for Unity desktops [1]. 
#  If you use Unity, use this script to start Atomic.
#  
#  Why a custom launcher just for Unity?
#  -------------------------------------
#  
#  Unity has a feature called the Global Menu: Instead of placing application menus inside the application window, 
#  Unity places them in the top bar of the desktop window.
#  
#  Unfortunately, Eclipse (and thus Eclipse-based applications such as Atomic) do not fully support the
#  Global Menu. This results in a number of issues when using Eclipse or Eclipse-based applications under
#  Unity, e.g., some menus will not appear in full. In the case of Atomic, the main side-effect of using the
#  Global Menu is that the key combination for keyboard shortcuts is not displayed next to the respective menu item.
#  
#  Solution: 'atomic-unity.sh'
#  ---------------------------
#  
#  Thankfully, there is an environment variable addressing the Global Menu in Ubuntu: 'UBUNTU_MENUPROXY'. If set to '0',
#  the Global Menu isn't used. Instead, the application menus will be placed in the actual application window.
#  
#  The launcher script 'atomic-unity.sh' does just that. It simply calls 'UBUNTU_MENUPROXY=0' and then continues
#  to start Atomic via the default launcher 'atomic'. The script also accepts any arguments you may want to pass
#  in to the application. E.g., './atomic-unity.sh -consoleLog'.
#  
#  How to use 'atomic-unity.sh'
#  -----------------------------
#  
#  Simply navigate into the Atomic installation folder, and call the script. On the command line, you can pass in arguments as well:
#  $> ./atomic-unity.sh -consoleLog
#  OR
#  $> bash atomic-unity.sh -consoleLog 
#  
#  References
#  ----------
#  
#  [1] http://unity.ubuntu.com/
#  
#  Related bug reports, etc.
#  -------------------------
#  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=330563
#  - http://ubuntuforums.org/showthread.php?t=1935380

echo "Setting the environment variable \"UBUNTU_MENUPROXY\" to \"0\".
This has the effect that the Unity global menu is not used (on, e.g., Ubuntu).
Instead, the menu is relocated to the application menu.
If this isn't set, keyboard shortcut texts aren't visible in the menus."
UBUNTU_MENUPROXY=0 ./atomic "$@"
exit 0