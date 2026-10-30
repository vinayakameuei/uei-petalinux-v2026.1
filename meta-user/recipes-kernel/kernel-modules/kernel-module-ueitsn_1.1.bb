SUMMARY = "UEI TSN kernel module"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.1+svn${SRCPV}"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit module

SVN_BASE = "svn://subversion2.ametek.com/Software/PowerDNA/Zynq/TSN/Software"

SRC_URI = " \
    ${SVN_BASE};module=kernelDriver;protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
    file://0001-Fix-compilation-error.patch \
    file://0002-update-print-and-add-pl_bus-and-compatible-string.patch \
"

S = "${WORKDIR}/kernelDriver"

EXTRA_OEMAKE:append = " KERNEL_SRC=${STAGING_KERNEL_DIR}"

KERNEL_MODULE_AUTOLOAD = "uei_tsn"

do_install:append(){
    install -m 0766 -d ${D}${includedir}
}

FILES:${PN}-dev = " \
    ${includedir}/${BPN}/Module.symvers \
"

addtask do_create_pkgupgrade
do_create_pkgupgrade() {
    if [ -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz ]; then
        rm -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz
    fi
    install -m 0766 -d ${WORKDIR}/pkgupgrade-${PN}-${PV}
    install -m 0766 ${WORKDIR}/INSTALL ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install -m 0775 ${WORKDIR}/pkgupgrade.sh ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-${PV}* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/kernel-module-uei-tsn* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
