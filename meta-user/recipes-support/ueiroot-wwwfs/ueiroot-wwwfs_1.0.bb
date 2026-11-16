SUMMARY = "UEIPAC Support Rootfs www Files"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
#FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

RDEPENDS:${PN} = "bash"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2"

SRC_URI = " \
    ${UEI_FEED_URL}/ueiroot-wwwfs-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "f4b3aef0bd17b5a86e976b3cdc75d5a6f80a9d61e9a06d491a789a4ae2416e05"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

inherit bin_package

do_install() {
    install -d ${D}/www
    cp -a ${S}/www/* ${D}/www/.
    chown -R root:root ${D}/*
}
