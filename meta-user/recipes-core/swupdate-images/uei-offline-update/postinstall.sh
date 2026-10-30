#!/bin/sh
set -eu

RPM_DIR="/tmp/swupdate-rpms"
QSPI_BOOT_MTD="/dev/mtd0"

if [ -f "/tmp/qspi_boot_31_DNR_4ev.bin" ]; then
    /usr/bin/uei-swupdate/update-qspi.sh "/tmp/qspi_boot_31_DNR_4ev.bin" "${QSPI_BOOT_MTD}"
fi

# RPM payload installation is disabled during initial kernel/DTB/QSPI bring-up.
# Re-enable this block when rpms.tar.gz is added back to sw-description.
#if [ -f "/tmp/rpms.tar.gz" ]; then
#    rm -rf "${RPM_DIR}"
#    mkdir -p "${RPM_DIR}"
#    tar xzf "/tmp/rpms.tar.gz" -C "${RPM_DIR}"
#    /usr/bin/uei-swupdate/install-rpms.sh "${RPM_DIR}"
#fi

sync