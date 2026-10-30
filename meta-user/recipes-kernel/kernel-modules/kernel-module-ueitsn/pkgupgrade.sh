#!/bin/bash

rpmlist="kernel-module-ueitsn kernel-module-uei-tsn"

SVCBIN="modbusslave UeiOpcServer idds-n"
RUNMODBUS="0"
RUNOPC="0"
RUNIDDS="0"

# Uninstall packages before updating to prevent against failed
# dependencies errors and allow flexibility in going up or down
# versions.
echo "Preparing installation..."

# Check and close any UEI apps/services before unloading pdnadev
for s in ${SVCBIN}; do
    if pgrep -x "$s" >/dev/null; then
        echo "$s is running and will be stopped"
        killall -q -9 "$s"
        if [ "${s}" = "modbusslave" ]; then
            RUNMODBUS="1"
            systemctl stop modbusslave
        elif [ "${s}" = "UeiOpcServer" ]; then
            RUNOPC="1"
            systemctl stop ueiopc
        elif [ "${s}" = "idds-n" ]; then
            RUNIDDS="1"
            systemctl stop ueiidds
        fi
    fi
done

# Unload uei_tsn driver module
rmmod uei_tsn

# Iterate through list of rpm base names
for f in ${rpmlist}; do
    # Uninstall rpm package with matching package name
    rpm -qa | grep $f | xargs rpm -ev --nodeps
done

# Install rpms
echo "Installing rpm packages..."
rpm -ihv *.rpm

# Load upgraded pdnadev driver modules
modprobe uei_tsn

# Start any services running prior to rpm installation
if [ "${RUNMODBUS}" = "1" ]; then
    systemctl start modbusslave
fi
if [ "${RUNOPC}" = "1" ]; then
    systemctl start ueiopc
fi
if [ "${RUNIDDS}" = "1" ]; then
    systemctl start ueiidds
fi

echo "Installation complete..."
