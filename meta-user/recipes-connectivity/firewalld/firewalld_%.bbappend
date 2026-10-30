# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
	file://public.xml \
"

do_install:append() {
	install -d ${D}${sysconfdir}/firewalld/zones
	install -m 644 ${WORKDIR}/public.xml ${D}${sysconfdir}/firewalld/zones/.
}

FILES:${PN} += " \
	${sysconfdir}/firewalld/zones/public.xml \
"

SYSTEMD_AUTO_ENABLE = "enable"

RM_WORK_EXCLUDE += "${PN}"
