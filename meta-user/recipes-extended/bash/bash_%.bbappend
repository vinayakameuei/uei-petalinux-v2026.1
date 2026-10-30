# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' \
	file://enable_system_global_bash_bash_logout_in_config-top_h.patch \
	file://bash.bash_logout', '', d)}"

do_install:append () {
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'install -d ${D}${sysconfdir}', '', d)}
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'install -m 644 ${WORKDIR}/bash.bash_logout ${D}${sysconfdir}', '', d)}
}

FILES:${PN} += " \
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' \
	/etc/bash.bash_logout', '', d)}"

RM_WORK_EXCLUDE += "${PN}"
