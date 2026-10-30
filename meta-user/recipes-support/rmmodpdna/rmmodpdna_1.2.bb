SUMMARY = "Rootfs add-ons for rmmodpdna.service"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

RM_WORK_EXCLUDE += "${PN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI = "file://rmmodpdna.service"

inherit bin_package systemd

do_install() {
    install -d ${D}${sysconfdir}/systemd/system
    install -m 0644 ${WORKDIR}/rmmodpdna.service ${D}${sysconfdir}/systemd/system/.
}

SYSTEMD_SERVICE:${PN} = "rmmodpdna.service"
SYSTEMD_AUTO_ENABLE = "enable"

