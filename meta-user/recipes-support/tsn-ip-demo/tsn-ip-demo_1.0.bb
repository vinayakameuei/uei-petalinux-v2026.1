DESCRIPTION = "TSN IP Demo Files"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

DEPENDS = "libueitsn"
RDEPENDS:${PN} = "bash"

RM_WORK_EXCLUDE += "${PN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.0"

SRC_URI = " \
    http://10.221.7.142:8080/tsn-ip-demo-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "838737aa41ae1dbc411d1dbf283d9da2666c89d28ea1c3841475aabe04e7cff8"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

INSANE_SKIP:${PN} += "ldflags"

do_compile(){
    cd ${S}
    oe_runmake
}

do_install(){
    oe_runmake prefix=${D} install
    install -m 0755 ${S}/CreateVlans/setupVlans.py ${D}/usr/local/bin
}
FILES:${PN} += "\
    /usr/local/bin/* \
    /usr/local/tsndemo \
    /usr/local/tsndemo/* \
    "
