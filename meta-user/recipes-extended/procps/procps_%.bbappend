# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' \
		file://sysctl.conf.sec', '', d)}"

do_install:append() {
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}" ]; then
		install -m 0644 ${WORKDIR}/sysctl.conf.sec ${D}${sysconfdir}/sysctl.conf
	fi
}

#RM_WORK_EXCLUDE += "${PN}"
