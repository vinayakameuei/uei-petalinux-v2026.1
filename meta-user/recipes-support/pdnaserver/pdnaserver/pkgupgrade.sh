#!/bin/bash

rpmlist="pdnaserver"

# Uninstall packages before updating to prevent against failed
# dependencies errors and allow flexibility in going up or down
# versions.
echo "Preparing installation..."

# Close any pdnaserver processes running
systemctl stop inetd;killall -q -9 pdnaserver

# Uninstall rpm package with matching string $rpmlist
rpm -qa | grep $rpmlist | xargs rpm -ev --nodeps

# Install rpms
echo "Installing rpm packages..."
rpm -ihv *.rpm

# Restart inetd for pdnaserver
systemctl start inetd
