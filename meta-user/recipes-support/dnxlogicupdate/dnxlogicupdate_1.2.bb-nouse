SUMMARY = "DNX Logic Update application"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "daqlib"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SRC_URI = " \
    svn://subversion2.ametek.com/Software/PowerDNA/PDNx%5C%20Logic%5C%20Loader;module=DNxLogicUpdate;protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/DNxLogicUpdate"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

do_compile:prepend() {
	cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
	install -m 0755 -d ${D}${bindir}
	install -m 0755 ${S}/DNxLogicUpdate ${D}${bindir}
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

PARALLEL_MAKE = ""

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
