DESCRIPTION = "Reset root password and shutdown device"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://passwd-rst"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/passwd-rst ${D}${bindir}/passwd-rst
}

FILES:${PN} += "${bindir}/passwd-rst"

RM_WORK_EXCLUDE += "${PN}"