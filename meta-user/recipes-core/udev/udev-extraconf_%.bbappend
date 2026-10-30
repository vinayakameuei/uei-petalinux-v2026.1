# Freescale i.MX SOC extra configuration udev rules
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
	file://blacklist.conf \
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' \
		file://mount.ignorelist.sec \
		file://mount.sh.sec \
		file://bluetooth.conf \
		file://cramfs.conf \
		file://dccp.conf \
		file://freevxfs.conf \
		file://hfs.conf \
		file://hfsplus.conf \
		file://jffs2.conf \
		file://rds.conf \
		file://sctp.conf \
		file://squashfs.conf \
		file://tipc.conf \
		file://99-automount.rules \
		file://udf.conf', '', d)}"

do_install:append() {
	install -d ${D}${sysconfdir}/modprobe.d

    install -m 0644 ${WORKDIR}/dccp.conf \
        ${D}${sysconfdir}/modprobe.d/dccp.conf

    install -m 0644 ${WORKDIR}/sctp.conf \
        ${D}${sysconfdir}/modprobe.d/sctp.conf

	install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/99-automount.rules     ${D}${sysconfdir}/udev/rules.d/automount.rules
}

FILES:${PN} += " \
    ${sysconfdir}/modprobe.d/dccp.conf \
    ${sysconfdir}/modprobe.d/sctp.conf \
"

#RM_WORK_EXCLUDE += "${PN}"
