SUMMARY = "UEIPAC Support Rootfs www Files"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
#FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

RDEPENDS:${PN} = "bash"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SVN_DEV = "svn://subversion2.ametek.com/Software/PowerDNA/3.3.x"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${SVN_DEV}', d)}"

SVN_MOD_NAME = "file_system"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC;module=${SVN_MOD_NAME};protocol=svn \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${SVN_MOD_NAME}"

inherit bin_package

do_install() {
    install -d ${D}/www
    cp -a ${S}/www/* ${D}/www/.
    chown -R root:root ${D}/*
}

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
