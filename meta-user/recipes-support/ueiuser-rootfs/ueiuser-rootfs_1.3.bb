SUMMARY = "Rootfs add-ons for user 'uei'"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.3"

SRC_URI = " \
    ${UEI_FEED_URL}/ueiuser-rootfs-${PV}.tar.gz \
"
SRC_URI[sha256sum] = "6a0599293acb9acfc553922f569eb329df35b8c8969f65b9165cafd5d498ebb7"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

inherit bin_package useradd

USERADD_PACKAGES = "${PN}"
PASSWD_UEI ?= "\$6\$uei\$Qqcyz9b1fqFAcGslFQxRT3agqC9l8gU51CFk7CE60I/.7xVzegV.zEDTPWnGeY4641FUxhUv5sbLusjt3J6Rl."
USERADD_PARAM:${PN} = "-u 1001 -g 1001 -m -r -s /bin/bash -p '${PASSWD_UEI}' uei"
GROUPADD_PARAM:${PN} = "-g 1001 uei"

do_install() {
	install -d ${D}/home/uei/Desktop
	cp -a ${S}/home/uei/Desktop/* ${D}/home/uei/Desktop/.
	chown -R uei:uei ${D}/home/uei
}
