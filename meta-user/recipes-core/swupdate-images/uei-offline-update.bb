SUMMARY = "UEI offline SWUpdate bundle"
DESCRIPTION = "Kernel, DTB, and QSPI boot image offline update bundle."
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

inherit swupdate

SRC_URI = " \
    file://sw-description \
    file://postinstall.sh \
"

SWUPDATE_IMAGES = ""
SWUPDATE_IMAGES_FSTYPES = ""

# Keep lab bring-up unsigned. For production, set SWUPDATE_SIGNING to RSA
# and provide SWUPDATE_PRIVATE_KEY plus the matching public key on target.
SWUPDATE_SIGNING = ""
#SWUPDATE_PRIVATE_KEY ?= "${TOPDIR}/keys/swupdate-priv.pem"

SRC_URI:append = " file://Image file://system-top-4ev.dtb file://qspi_boot_31_DNR_4ev.bin"

python do_swuimage:prepend() {
    import os
    os.chmod(d.expand('${WORKDIR}/postinstall.sh'), 0o755)
}