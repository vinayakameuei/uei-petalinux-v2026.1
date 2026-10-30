SUMMARY = "UEI PalLib Library"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SVN_DEV = "svn://subversion2.ametek.com/Software"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}', '${SVN_DEV}', d)}"

SRC_URI = " \
	${SVN_BASE}/Common;module=UeiPalLib;protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/UeiPalLib"

EXTRA_OEMAKE = "'CC=${CC}' 'CXX=${CXX}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

PARALLEL_MAKE = ""

do_compile:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
        install -d ${D}${includedir}
        install -m 644 ${S}/*.h ${D}${includedir}
        install -m 644 ${S}/sxmlc/*.h ${D}${includedir}
        install -d ${D}${libdir}
        if [ -e ${S}/libUeiPal.a ]; then
            install -m 644 ${S}/libUeiPal.a ${D}${libdir}
        else
            install -m 644 ${S}/libUeiPalD.a ${D}${libdir}
        fi
        install -d ${D}/usr/local/sbin
        install -m 0755 ${S}/UeiPalTest/UeiPalTest ${D}/usr/local/sbin
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
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-staticdev-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

ALLOW_EMPTY_${PN} = "1"

FILES:${PN} += "/usr/local/sbin/UeiPalTest"
FILES:${PN}-dev += "/usr/local/sbin/UeiPalTest"

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
