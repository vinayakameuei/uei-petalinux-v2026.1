# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Not used but leave here
SRC_URI += " \
	file://login-banner \
"

do_install:append() {
	install -m 0644 ${WORKDIR}/login-banner ${D}${sysconfdir}/
	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}" ]; then
		sed -i \
			-e '/# This module parses env/i \
# Add a warning message to user after entering username. DISABLED by default \
#auth        optional  pam_echo.so file=/etc/login-banner \
' \
			${D}${sysconfdir}/pam.d/login

		sed -i \
			-e 's,UMASK		022,UMASK		077,g' \
			-e 's,PASS_MAX_DAYS	99999,PASS_MAX_DAYS	365,g' \
			-e 's,PASS_MIN_DAYS	0,PASS_MIN_DAYS	7,g' \
			${D}${sysconfdir}/login.defs

		sed -i \
			-e 's,INACTIVE=-1,INACTIVE=30,g' \
			${D}${sysconfdir}/default/useradd
    fi
}

#RM_WORK_EXCLUDE += "${PN}"
