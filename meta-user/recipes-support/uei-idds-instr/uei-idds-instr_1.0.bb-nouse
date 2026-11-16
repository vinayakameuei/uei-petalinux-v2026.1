SUMMARY = "UEI iDDS"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "daqlib dmapserver expat libpdnaconfig ueimds-rootfs ueipallib"

inherit systemd

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC;module=UEIiDDS;protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
    file://ueiidds.service \
"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

#EXTRA_OEMAKE = "'CC=${CC}' 'CXX=${CXX}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS}
#  -DWITHOUT_XATTR' 'BUILDDIR=${S}'"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/UEIiDDS/${BPN}"

PARALLEL_MAKE = ""

do_compile:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install () {
    oe_runmake install UEIPACROOT=${D}
    install -d -m 0755 -o root ${D}${bindir}
    install -m 0755 -o root ${S}/../idds_shell ${D}${bindir}
    install -m 0755 -o root ${S}/../ueiiDDS ${D}${bindir}
    install -d ${D}${sysconfdir}/systemd/system
    install -m 0644 ${WORKDIR}/ueiidds.service ${D}${sysconfdir}/systemd/system/.
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

FILES:${PN} = "\
    /etc/systemd/system/ueiidds.service \
    /usr/bin/idds_shell \
    /usr/bin/ueiiDDS \
    /usr/local/UEIiDDS/idds-n \
"

SYSTEMD_SERVICE_${PN} = "ueiidds.service"
SYSTEMD_AUTO_ENABLE_${PN} = "disable"

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
