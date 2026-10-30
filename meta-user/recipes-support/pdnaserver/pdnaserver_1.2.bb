SUMMARY = "PDNA Server application"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

inherit systemd

RM_WORK_EXCLUDE += "${PN}"
# Switchover to Revision 5.0.0 requires kernel-module-daqiomc
DEPENDS = "kernel-module-daqiomc openssl"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC/tools;module=${PN};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
    file://0001-Change-pdnaserver-listening-port-to-end0-for-v2025.1.patch \
    file://pdnaserver.service \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

do_compile:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
    install -m 0766 -d ${D}${bindir}
    install -m 0755 ${S}/pdnaserver ${D}${bindir}
}

do_install:append() {
    install -d ${D}${sysconfdir}/systemd/system
    install -m 0644 ${WORKDIR}/pdnaserver.service ${D}${sysconfdir}/systemd/system/.
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

SYSTEMD_SERVICE:${PN} = "pdnaserver.service"
SYSTEMD_AUTO_ENABLE = "enable"
