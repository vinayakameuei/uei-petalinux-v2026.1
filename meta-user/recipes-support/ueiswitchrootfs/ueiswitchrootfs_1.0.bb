SUMMARY = "Simple C application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ueiswitchrootfs.c"

S = "${WORKDIR}"

do_compile() {
    ${CC} ${CFLAGS} ${LDFLAGS} ueiswitchrootfs.c -o ueiswitchrootfs
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ueiswitchrootfs ${D}${bindir}/ueiswitchrootfs
}

FILES_${PN} += "${bindir}/ueiswitchrootfs"
