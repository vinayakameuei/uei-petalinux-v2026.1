SUMMARY = "UEI Misc Boot Configuration during boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = " \
	file://ueibootmisc.service \
	file://ueibootmisc.sh \
"
PV = "1.0"

inherit bin_package systemd

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install() {
	install -d -o root ${D}${sysconfdir}/systemd/system
	install -m 0644 ${WORKDIR}/ueibootmisc.service ${D}${sysconfdir}/systemd/system/.
	install -d -m 0755 -o root ${D}${bindir}
	install -m 0700 -o root ${WORKDIR}/ueibootmisc.sh ${D}${bindir}
}

RM_WORK_EXCLUDE += "${PN}"


SYSTEMD_SERVICE:${PN} = "ueibootmisc.service"
SYSTEMD_AUTO_ENABLE = "enable"

