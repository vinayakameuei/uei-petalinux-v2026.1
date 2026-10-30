SUMMARY = "Rootfs add-ons for user 'uei'"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_BASE = "svn://subversion2.ametek.com/Software/PowerDNA/Zynq/Yocto/files"

SRC_URI = " \
    ${SVN_BASE};module=${BPN};protocol=svn \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${BPN}"

inherit bin_package

do_install() {
    install -d ${D}/home/uei/Desktop
    cp -a ${S}/home/uei/Desktop/* ${D}/home/uei/Desktop/.
    chown -R root:root ${D}/home/uei
}

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"

