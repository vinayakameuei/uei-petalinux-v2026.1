#!/bin/bash

rpmlist="pdnaexplorer"

SVCBIN="pdnaexplorer-1.0.jar"
PID=""

# Uninstall packages before updating to prevent against failed
# dependencies errors and allow flexibility in going up or down
# versions.
echo "Preparing installation..."

# Close any PDNAExplorer processes running
PID=`pgrep -f "$SVCBIN"`
if [ ! -z "$PID" ]; then
    for i in ${PID}; do
        kill -9 $i
    done
fi

# Uninstall rpm package with matching string $rpmlist
rpm -qa | grep $rpmlist | xargs rpm -ev --nodeps

# Install rpms
echo "Installing rpm packages..."
rpm -ihv *.rpm
