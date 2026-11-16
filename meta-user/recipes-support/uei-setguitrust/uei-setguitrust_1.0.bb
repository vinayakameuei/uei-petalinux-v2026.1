SUMMARY = "UEI Set "trust" attributes on *.desktop files"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.0"

SRC_URI = " \
    http://10.221.7.142:8080/uei-setguitrust-${PV}.tar.gz \
    file://INSTALL \
    file://pkgupgrade.sh \
"
SRC_URI[sha256sum] = "ba8cab4bc15155cbf7afed81ac32e44b350d8f5bdcaff632a6f0ec2dc18d29da"

RDEPENDS:${PN} = "bash"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

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