SUMMARY = "UEIModbus Modbusslave application"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "daqlib ueipallib libpdnaconfig"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.2+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC;module=UEIModbus;protocol=svn \
    file://modbusslave.service \
    file://INSTALL \
    file://pkgupgrade.sh \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/UEIModbus"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

do_compile:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
    install -d ${D}${bindir}
    install -m 755 ${S}/modbusslave ${D}${bindir}
    install -d ${D}${sysconfdir}
    install -m 644 ${S}/modbusslave.conf ${D}${sysconfdir}
    install -d ${D}/www
    install -m 0644 ${S}/UI/modbus.html ${D}/www
    install -d ${D}${sysconfdir}/systemd/system
    install -m 0755 ${WORKDIR}/modbusslave.service ${D}${sysconfdir}/systemd/system/.
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

FILES:${PN} += " \
    /www \
    /www/modbus.html \
"

SYSTEMD_SERVICE_${PN} = "${PN}.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
