#!/bin/bash

# Set "trust" attribute for desktop icons for root
su -l root root -s '/bin/bash' -c 'dbus-run-session -- \
    gio set /home/root/Desktop/chromium.desktop 'metadata::trust' "true"'
sleep 1
su -l root root -s '/bin/bash' -c 'dbus-run-session -- \
    gio set /home/root/Desktop/chromium.desktop 'metadata::trust' "true"'
sleep 1

# Set "trust" attribute for desktop icons for uei
su -l uei uei -s '/bin/bash' -c 'dbus-run-session -- \
    gio set /home/uei/Desktop/chromium.desktop 'metadata::trust' "true"'
sleep 1
su -l uei uei -s '/bin/bash' -c 'dbus-run-session -- \
    gio set /home/uei/Desktop/uei-pdnaexplorer.desktop 'metadata::trust' "true"'

# Delete me
rm $0
