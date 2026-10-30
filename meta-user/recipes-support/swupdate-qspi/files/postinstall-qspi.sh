#!/bin/sh
set -eu

# postinstall script for qspi update
QSPI_BOOT_MTD="/dev/mtd0"

if [ -f "/tmp/QSPI_BOOT.BIN" ]; then
    /usr/bin/uei-swupdate/update-qspi.sh "/tmp/QSPI_BOOT.BIN" "${QSPI_BOOT_MTD}"
else
    echo "Missing /tmp/QSPI_BOOT.BIN" >&2
    exit 1    
fi

# postinstall script for rpm update
#RPM_DIR=/tmp/swupdate-rpms

#rm -rf "${RPM_DIR}"
#mkdir -p "${RPM_DIR}"

#tar xzf /tmp/rpms.tar.gz -C "${RPM_DIR}"

#dnf install -y ${RPM_DIR}/*.rpm

sync
