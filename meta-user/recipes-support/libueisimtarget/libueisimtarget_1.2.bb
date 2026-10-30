SUMMARY = "Libueisimtarget librrary for UEISim"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "daqlib ueipallib libpdnaconfig libshareddata"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC/UEISim;module=${BPN};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

MAJOR = "1"
MINOR = "0"
MICRO = "0"

EXTRA_OEMAKE:append = " MAJ=${MAJOR} MIN=${MINOR} MIC=${MICRO}"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

PKG_libueisimtarget = "${PN}"
PKG_libueisimtarget-dbg = "${PN}-dbg"
PKG_libueisimtarget-dev = "${PN}-dev"
PKG_libueisimtarget-staticdev = "${PN}-staticdev"
PKG_libueisimtarget-doc = "${PN}-doc"
PKG_libueisimtarget-locale = "${PN}-locale"
PKG_libueisimtarget-src = "${PN}-src"

do_compile:prepend() {
	cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
        install -d ${D}${includedir}
        install -m 644 ${S}/UeiSimTarget.h ${D}${includedir}
        install -d ${D}${base_libdir}
        install -m 644 ${S}/${PN}.a ${D}${base_libdir}
        install -m 644 ${S}/${PN}.so.${MAJOR}.${MINOR}.${MICRO} ${D}${base_libdir}
        cd ${D}${base_libdir}
        ln -sf ${PN}.so.${MAJOR}.${MINOR}.${MICRO} ${PN}.so.${MAJOR}
        ln -sf ${PN}.so.${MAJOR} ${PN}.so
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

PARALLEL_MAKE = ""
                    
do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
