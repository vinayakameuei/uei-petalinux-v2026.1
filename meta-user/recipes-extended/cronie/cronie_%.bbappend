# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://aide \
"
do_install:append() {
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}" ]; then
		rm -f ${D}${sysconfdir}/cron.deny
		chmod 0700 ${D}${sysconfdir}/cron.d
		chmod 0700 ${D}${sysconfdir}/cron.daily
		chmod 0700 ${D}${sysconfdir}/cron.hourly
		chmod 0700 ${D}${sysconfdir}/cron.monthly
		chmod 0700 ${D}${sysconfdir}/cron.weekly
		install -m 600 ${WORKDIR}/aide ${D}${sysconfdir}/cron.daily
	fi
}

#SYSTEMD_AUTO_ENABLE = "disable"

#RM_WORK_EXCLUDE += "${PN}"
