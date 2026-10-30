#!/bin/bash
set -euo pipefail

usage() {
    echo "Usage: $0 <rpm-dir> [rpm-name ...]" >&2
}

if [ "$#" -lt 1 ]; then
    usage
    exit 2
fi

RPM_DIR="$1"
shift

if [ ! -d "${RPM_DIR}" ]; then
    echo "RPM directory not found: ${RPM_DIR}" >&2
    exit 1
fi

if [ "$#" -gt 0 ]; then
    RPM_FILES=()
    for rpm_name in "$@"; do
        matches=("${RPM_DIR}"/${rpm_name}*.rpm)
        if [ ! -e "${matches[0]}" ]; then
            echo "No RPM matching ${rpm_name}*.rpm in ${RPM_DIR}" >&2
            exit 1
        fi
        RPM_FILES+=("${matches[@]}")
    done
else
    RPM_FILES=("${RPM_DIR}"/*.rpm)
    if [ ! -e "${RPM_FILES[0]}" ]; then
        echo "No RPM files found in ${RPM_DIR}" >&2
        exit 1
    fi
fi

echo "Installing RPM update payload from ${RPM_DIR}"
rpm -Uvh --replacepkgs "${RPM_FILES[@]}"