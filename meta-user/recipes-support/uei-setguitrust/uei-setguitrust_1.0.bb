SUMMARY = "UEI Set "trust" attributes on *.desktop files"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SRC_URI = " \
    svn://subversion2.ametek.com/Software/PowerDNA/Zynq/Yocto/files;module=${BPN};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

RDEPENDS:${PN} = "bash"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}"

inherit bin_package

do_install() {
    install -d ${D}${ROOT_HOME}/
    install -d -m 0755 ${D}/usr/local/uei
    install -m 0755 ${S}/usr/local/uei/setguitrust.sh ${D}/usr/local/uei/.
    cd ${D}${ROOT_HOME}/
    ln -s ../../usr/local/uei/setguitrust.sh ${D}${ROOT_HOME}/.
    chown -R root:root ${D}/*
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

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
