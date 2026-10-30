SUMMARY = "Final merged IOMC/PDNA kernel module"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.1+svn${SRCPV}"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

inherit module

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SRC_URI = " \
    ${SVN_BASE};module=DAQ_IOMC;protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
    file://0001-Add-ccflags-to-remove-mgeneral-regs-only.patch \
    file://0002-add-define.patch \
    file://0003-Search-axi_epc-by-compatible-name.patch \
"

S = "${WORKDIR}/DAQ_IOMC"

EXTRA_OEMAKE:append = " KERNEL_SRC=${STAGING_KERNEL_DIR} PTP_NF=1 RT=1"

# Uncomment this line to build debug version
#EXTRA_OEMAKE:append = " 'DEBUG=1'"

# Disable autoloading as it fails occassionaly due to mysterious timing issue
# modprobe module in rc.local instead.
KERNEL_MODULE_AUTOLOAD = "pdnadev"

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.

do_compile:prepend() {
    cp ${S}/Makefile.zynq ${S}/Makefile
}

do_compile:prepend_ueipdnasx6-rt() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install:append() {
    install -m 0766 -d ${D}${includedir}
    install -m 0766 ${S}/common/UEIPAC/pdnadev_ioctl.h ${D}${includedir}
}

FILES:${PN}-dev = " \
    ${includedir}/${PN}/Module.symvers \
    ${includedir}/pdnadev_ioctl.h \
"
addtask do_create_pkgupgrade
do_create_pkgupgrade() {
    if [ -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz ]; then
        rm -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz
    fi
    install -m 0755 -d ${WORKDIR}/pkgupgrade-${PN}-${PV}
    install -m 0766 ${WORKDIR}/INSTALL ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install -m 0775 ${WORKDIR}/pkgupgrade.sh ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-${PV}-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/kernel-module-pdnabus* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/kernel-module-pdnadev* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.    
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
