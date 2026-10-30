#!/bin/bash

rpmlist="ueimodbus"

SVC="modbusslave"
SVCBIN=$SVC
RUNSVC=0

# Uninstall packages before updating to prevent against failed
# dependencies errors and allow flexibility in going up or down
# versions.
echo "Preparing installation..."

# Check if $SVC running and close
if pgrep -x "$SVCBIN" >/dev/null; then
    echo "$SVC is running and will be stopped"
    systemctl stop "$SVC";killall -q -9 "$SVCBIN"
    RUNSVC=1
fi

# Uninstall rpm package with matching string $rpmlist
rpm -qa | grep $rpmlist | xargs rpm -ev --nodeps

# Install rpms
echo "Installing rpm packages..."
rpm -ihv *.rpm

# Reload $SVC.service
echo "Reload $SVC.service configuration..."
systemctl -q daemon-reload

# Start $SVC if it was running prior to rpm installation
if [ "${RUNSVC}" = "1" ]; then
    echo "Start $SVC ..."
    systemctl start "$SVC"
fi
