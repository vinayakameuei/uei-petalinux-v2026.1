DESCRIPTION = "UEIDAQ Python Extension"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_DEV = "svn://subversion2.ametek.com/Software"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}', '${SVN_DEV}', d)}"

SVN_MOD_NAME = "Framework"

SRC_URI = " \
    ${SVN_BASE};module=${SVN_MOD_NAME};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${SVN_MOD_NAME}/Python"

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

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
