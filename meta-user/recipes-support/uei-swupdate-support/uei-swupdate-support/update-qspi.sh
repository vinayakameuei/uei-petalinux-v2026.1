#!/bin/bash
set -euo pipefail

usage() {
    echo "Usage: $0 <boot-image> [mtd-device-or-name]" >&2
}

find_mtd_by_name() {
    local wanted="$1"
    local name
    local mtd

    while read -r mtd _ _ name; do
        name="${name%\"}"
        name="${name#\"}"
        if [ "${name}" = "${wanted}" ]; then
            echo "/dev/${mtd%:}"
            return 0
        fi
    done < /proc/mtd

    return 1
}

if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
    usage
    exit 2
fi

BOOT_IMAGE="$1"
MTD_TARGET="${2:-${SWU_QSPI_MTD:-}}"

if [ ! -f "${BOOT_IMAGE}" ]; then
    echo "Boot image not found: ${BOOT_IMAGE}" >&2
    exit 1
fi

if [ -z "${MTD_TARGET}" ]; then
    echo "Set SWU_QSPI_MTD or pass the target MTD device/name explicitly." >&2
    echo "Available MTD devices:" >&2
    cat /proc/mtd >&2
    exit 1
fi

if [[ "${MTD_TARGET}" != /dev/mtd* ]]; then
    MTD_TARGET="$(find_mtd_by_name "${MTD_TARGET}")"
fi

if [ ! -c "${MTD_TARGET}" ]; then
    echo "MTD target is not a character device: ${MTD_TARGET}" >&2
    exit 1
fi

echo "Writing ${BOOT_IMAGE} to ${MTD_TARGET}"
flashcp -v "${BOOT_IMAGE}" "${MTD_TARGET}"