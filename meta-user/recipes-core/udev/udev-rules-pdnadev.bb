DESCRIPTION = "udev rules for UEI PDNADEV"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

SRC_URI = " file://11-pdnadev.rules"

S = "${WORKDIR}"

do_install () {
	install -d ${D}${sysconfdir}/udev/rules.d
	install -m 0644 ${WORKDIR}/11-pdnadev.rules ${D}${sysconfdir}/udev/rules.d/
}
