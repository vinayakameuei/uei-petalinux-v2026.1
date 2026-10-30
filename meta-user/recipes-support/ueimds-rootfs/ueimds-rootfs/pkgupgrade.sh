#!/bin/bash

rpmlist="ueimds-rootfs"

# Uninstall packages before updating to prevent against failed
# dependencies errors and allow flexibility in going up or down
# versions.
echo "Preparing installation..."

# Uninstall rpm package with matching string $rpmlist
rpm -qa | grep $rpmlist | xargs rpm -ev --nodeps

# Install rpms
echo "Installing rpm packages..."
rpm -ihv *.rpm
