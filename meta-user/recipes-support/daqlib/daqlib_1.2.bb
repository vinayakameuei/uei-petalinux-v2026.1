SUMMARY = "UEI DaqLib Library"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "ueipallib"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SRC_URI = " \
    ${SVN_BASE};module=DAQLib;protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"
UEI_MAJ="5"
UEI_MIN="1"
UEI_MIC="1"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

EXTRA_OEMAKE = "'CC=${CC}' 'CXX=${CXX}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

EXTRA_OEMAKE:append = " MAJ=${UEI_MAJ} MIN=${UEI_MIN} MIC=${UEI_MIC}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/DAQLib"

PARALLEL_MAKE = ""

do_compile:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}


do_install() {
        install -d ${D}${includedir}
        install -m 644 ${S}/*.h ${D}${includedir}
        install -d ${D}${base_libdir}
        install ${S}/libpowerdna.so.${UEI_MAJ}.${UEI_MIN}.${UEI_MIC} ${D}${base_libdir}
        cd ${D}${base_libdir}
        ln -sf libpowerdna.so.${UEI_MAJ}.${UEI_MIN}.${UEI_MIC} libpowerdna.so.${UEI_MAJ}
        ln -sf libpowerdna.so.${UEI_MAJ} libpowerdna.so
        install ${S}/libpowerdna.a ${D}${base_libdir}        
}

UPG_PN = "libpowerdna"

addtask do_create_pkgupgrade
do_create_pkgupgrade() {
    if [ -f ${TMPDIR}/pkgupgrades/pkgupgrade-${UPG_PN}-${PV}.tgz ]; then
        rm -f ${TMPDIR}/pkgupgrades/pkgupgrade-${UPG_PN}-${PV}.tgz
    fi
    install -m 0755 -d ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}
    install -m 0766 ${WORKDIR}/INSTALL ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}/.
    install -m 0775 ${WORKDIR}/pkgupgrade.sh ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${UPG_PN}${UEI_MAJ}-${PV}-* \
        ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${UPG_PN}-dev-* ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${UPG_PN}-staticdev-* ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${UPG_PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${UPG_PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

PACKAGES="daqlib-src daqlib-dbg daqlib-staticdev daqlib daqlib-dev daqlib-doc daqlib-locale"


FILES:${PN}-dev = "\
    /usr/include/* \
"

FILES:${PN} = "\
   ${libdir}/libpowerdna.so \
   ${libdir}/libpowerdna.so.5 \
   ${libdir}/libpowerdna.so.5.1.1 \
"

INSANE_SKIP:${PN} += "dev-so"    

BBCLASSEXTEND = "native"
                    
do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
