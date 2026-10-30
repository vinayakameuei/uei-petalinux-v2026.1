DESCRIPTION = "dd script to flash recovery-image to its partition"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ddrecovery"

do_install() {
    install -d ${D}${bindir}

    sed 's/@MACHINE@/${MACHINE}/g' \
        ${WORKDIR}/ddrecovery \
        > ${D}${bindir}/ddrecovery

    chmod 0755 ${D}${bindir}/ddrecovery
}

FILES:${PN} += "${bindir}/ddrecovery"

RM_WORK_EXCLUDE += "${PN}"
