#!/bin/bash

# Usage: ./build_qspi.sh <machine_name> <variant>
# make sure you have source petalinux settings before running this script
# Example: ./build_qspi.sh 4ev 31-DNR
# Example: ./build_qspi.sh 4ev-tsn 31-DNR

MACHINE=$1
VARIANT=$2

FPGA=${PWD}/../fw/${MACHINE}/${VARIANT}/CM_ZINQ_wrapper.bit
PMUFW=${PWD}/../fw/${MACHINE}/pmufw.elf
FSBL=${PWD}/../fw/${MACHINE}/zynqmp_fsbl.elf
ATF=${PWD}/../../images/linux/bl31.elf
DTB=${PWD}/../../images/linux/system-top-${MACHINE}.dtb
UBOOT=${PWD}/../../images/linux/u-boot.elf

OUTPUT_NAME="qspi_boot_${VARIANT//-/_}_${MACHINE}.bin"

# Validate Input Arguments
if [[ -z "$MACHINE" || -z "$VARIANT" ]]; then
    echo "Usage: $0 <machine_name: 4ev|4eg|4ev-tsn> <variant: 30-DNA|31-DNR|35-MIL>"
    exit 1
fi

FILES_TO_CHECK=("$FSBL" "$PMUFW" "$DTB" "$ATF" "$FPGA")
for file in "${FILES_TO_CHECK[@]}"; do
    if [[ ! -f "$file" ]]; then
        echo "Error: Required file missing -> $file"
        exit 1
    fi
done

echo "Building QSPI image for $MACHINE ($VARIANT)..."

petalinux-package --force --boot \
    --u-boot "$UBOOT"\
    --fsbl "$FSBL" \
    --pmufw "$PMUFW" \
    --dtb "$DTB" \
    --fpga "$FPGA" \
    --atf "$ATF" \
    --output "$OUTPUT_NAME"