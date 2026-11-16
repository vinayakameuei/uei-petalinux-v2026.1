DESCRIPTION = "UEIDAQ Python Extension"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.0"

SRC_URI = " \
    ${UEI_FEED_URL}/ueidaqpython-${PV}.tar.gz \
    file://INSTALL \
    file://pkgupgrade.sh \
"
SRC_URI[sha256sum] = "a9600e2419983f112d6ea6a02e77dbdbf7a84a24a241fc5314e6f7f0cc399f8a"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

inherit setuptools3 

do_compile:prepend() {
    swig -python -threads -c++ -I../CPP/include -DUeiDaqAPI -v -Wall -o UeiDaqPy_wrap.cpp UeiDaqPy.i
}

do_install:append() {
    #include examples on to target
    install -d ${D}/usr/local/framework
    install -d ${D}/usr/local/framework/python
    install -m 0775 ${S}/Examples/*.py ${D}/usr/local/framework/python
}

addtask do_create_pkgupgrade
do_create_pkgupgrade() {
    if [ -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz ]; then
        rm -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz
    fi
    install -m 0755 -d ${WORKDIR}/pkgupgrade-${PN}-${PV}
    install -m 0766 ${WORKDIR}/INSTALL ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install -m 0775 ${WORKDIR}/pkgupgrade.sh ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-${PV}-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-dev-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

FILES:${PN} += " \
    /usr/local/framework \
    /usr/local/framework/python \
    /usr/local/framework/python/* \
    "

DEPENDS = "python3-numpy-native \
           python3-wheel-native \
           python3 \
           swig-native \
           libueidaq \
"

RDEPENDS:${PN} = "python3-numpy \
"

BBCLASSEXTEND = "native"
