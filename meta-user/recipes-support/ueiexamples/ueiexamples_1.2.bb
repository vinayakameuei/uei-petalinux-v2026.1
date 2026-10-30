SUMMARY = "UEI examples for UEIPAC devices"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = " daqlib libshareddata gsl libusb1 mosquitto zeromq"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC;module=examples;protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/examples"

PARALLEL_MAKE = ""

do_compile:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install () {
    install -d ${D}/usr/local/examples
    oe_runmake install UEIPACROOT=${D}
    install -m 0766 -d ${D}/usr/lib/UeiPacUtils
    install -m 0766 ${S}/UeiPacUtils/UeiPacUtils.o \
            ${D}/usr/lib/UeiPacUtils
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
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"

FILES:${PN} = "\
    /usr/local/examples \
    /usr/local/examples/* \
"

FILES:${PN}-dev = "\
    /usr/lib/UeiPacUtils/UeiPacUtils.o \
"
#INSANE_SKIP_${PN} = "ldflags"
#TARGET_CC_ARCH += "${LDFLAGS}"
#BBCLASSEXTEND = "native"
