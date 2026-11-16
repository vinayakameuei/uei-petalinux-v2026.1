DESCRIPTION = "TSN IP Demo Files"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

DEPENDS = "libueitsn"

RM_WORK_EXCLUDE += "${PN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.0"

SRC_URI = " \
    http://10.221.7.142:8080/uei-config-tsn-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "d5fe4d6fc77fc6e5f0c9746d4934f88ad6d011fd4e767e8bcfab54fea2023ee9"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

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
