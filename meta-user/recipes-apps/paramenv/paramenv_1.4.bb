SUMMARY = "UEIPAC UEI params utility"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

RM_WORK_EXCLUDE += "${PN}"

SVN_BASE = "svn://subversion2.ametek.com/Software/PowerDNA/Yocto/Utilities"
#SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SVN_MOD_NAME = "${PN}"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.4+svn${SRCPV}"

SRC_URI = " \
    ${SVN_BASE};module=${SVN_MOD_NAME};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
    file://0001-Init-line-and-len-to-NULL.patch \
    file://0001-Fix-MAC-address-for-end1-and-end2-based-on-serial-nu.patch \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/${PN}"
INSANE_SKIP:${PN} = "ldflags"
# EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 
# 		'CFLAGS=${CFLAGS}' ' LDFLAGS=${LDFLAGS}'"

EXTRA_OEMAKE = "'CC=${CC}'"

do_install() {
	install -m 0755 -d ${D}/usr/sbin
	install -m 0755 ${S}/paramenv ${D}/usr/sbin
	install -m 0755 ${S}/paramenvl ${D}/usr/sbin
}

addtask do_create_pkgupgrade
do_create_pkgupgrade() {
    if [ -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PACKAGE_PV}.tgz ]; then
        rm -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PACKAGE_PV}.tgz
    fi
    install -m 0755 -d ${WORKDIR}/pkgupgrade-${PN}-${PACKAGE_PV}
    install -m 0766 ${WORKDIR}/INSTALL ${WORKDIR}/pkgupgrade-${PN}-${PACKAGE_PV}/.
    install -m 0775 ${WORKDIR}/pkgupgrade.sh ${WORKDIR}/pkgupgrade-${PN}-${PACKAGE_PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-${PACKAGE_PV}-* ${WORKDIR}/pkgupgrade-${PN}-${PACKAGE_PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PACKAGE_PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PACKAGE_PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PACKAGE_PV}.tgz ${TMPDIR}/pkgupgrades
}

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
