DESCRIPTION = "UEI TSN IP Library"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "ueipallib"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_BASE = "svn://subversion2.ametek.com/Software/PowerDNA/Zynq/TSN/Software"

SVN_MOD_NAME = "UeiTsnLib"

SRC_URI = " \
    ${SVN_BASE};module=${SVN_MOD_NAME};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${SVN_MOD_NAME}"

MAJOR = "1"
MINOR = "0"

do_install() {
        install -d ${D}${includedir}
        install -m 644 ${S}/*.h ${D}${includedir}
        install -d ${D}${base_libdir}
        install ${S}/libUeiTsn.so.${MAJOR}.${MINOR} ${D}${base_libdir}
        cd ${D}${base_libdir}
        ln -sf libUeiTsn.so.${MAJOR}.${MINOR} libUeiTsn.so.${MAJOR}
        ln -sf libUeiTsn.so.${MAJOR} libUeiTsn.so
        install ${S}/libUeiTsn.a ${D}${base_libdir}        
}

addtask do_create_pkgupgrade
do_create_pkgupgrade() {
    if [ -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz ]; then
        rm -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz
    fi
    install -m 0766 -d ${WORKDIR}/pkgupgrade-${PN}-${PV}
    install -m 0766 ${WORKDIR}/INSTALL ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install -m 0775 ${WORKDIR}/pkgupgrade.sh ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}${MAJOR}-${PV}-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-dev-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

BBCLASSEXTEND = "native"

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
