SUMMARY = "UEI SWUpdate offline update support scripts"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = " \
    file://install-rpms.sh \
    file://update-qspi.sh \
"

RDEPENDS:${PN} = "bash mtd-utils"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}/uei-swupdate
    install -m 0755 ${WORKDIR}/install-rpms.sh ${D}${bindir}/uei-swupdate/install-rpms.sh
    install -m 0755 ${WORKDIR}/update-qspi.sh ${D}${bindir}/uei-swupdate/update-qspi.sh
}

FILES:${PN} += "${bindir}/uei-swupdate ${bindir}/uei-swupdate/*"

PACKAGE_ARCH = "${MACHINE_ARCH}"