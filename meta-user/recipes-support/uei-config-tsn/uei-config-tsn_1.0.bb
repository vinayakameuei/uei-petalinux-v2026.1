DESCRIPTION = "TSN IP Demo Files"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

DEPENDS = "libueitsn"

RM_WORK_EXCLUDE += "${BPN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"
SVN_BASE = "svn://subversion2.ametek.com/Software/PowerDNA/Zynq/TSN/Software"
SVN_MOD_NAME = "ConfigTSN"

SRC_URI = " \
    ${SVN_BASE};module=${SVN_MOD_NAME};protocol=svn \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${SVN_MOD_NAME}"

INSANE_SKIP:${PN} += "ldflags"

do_compile(){
    cd ${S}
    oe_runmake
}

do_install(){
    oe_runmake prefix=${D} install
}
FILES:${PN} += "\
    /usr/sbin \
    /usr/sbin/ConfigTSN \
    /etc \
    /etc/PTP_Config.xml \
    "


do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
