# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
	file://public.xml \
	file://direct.xml \
"

do_install:append() {
	install -d ${D}${sysconfdir}/firewalld/zones
	install -m 644 ${WORKDIR}/public.xml ${D}${sysconfdir}/firewalld/zones/.
	install -m 644 ${WORKDIR}/direct.xml ${D}${sysconfdir}/firewalld/.
}

FILES:${PN} += " \
	${sysconfdir}/firewalld/zones/public.xml \
	${sysconfdir}/firewalld/direct.xml \
"

SYSTEMD_AUTO_ENABLE = "enable"

RM_WORK_EXCLUDE += "${PN}"
