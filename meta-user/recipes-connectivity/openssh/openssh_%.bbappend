# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
	file://DoD-banner.sec \
"

do_install:append () {
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}" ]; then
        sed -i -e 's,#   Ciphers aes128,  Ciphers aes128,g' ${D}${sysconfdir}/ssh/ssh_config
        sed -i \
            -e 's,ClientAliveInterval 15,ClientAliveInterval 300,g' \ 
            -e 's,ClientAliveCountMax 4,ClientAliveCountMax 0,g' \ 
            -e 's,#PasswordAuthentication yes,PasswordAuthentication no,g' \ 
            -e 's,ChallengeResponseAuthentication no,ChallengeResponseAuthentication yes,g' \ 
            -e 's,# no default,# Default,g' \ 
            -e 's,#Banner none,Banner /etc/ssh/DoD-banner,g' ${D}${sysconfdir}/ssh/sshd_config
        chmod 0600 ${D}${sysconfdir}/ssh/sshd_config
        install -m 0600 ${WORKDIR}/DoD-banner.sec ${D}${sysconfdir}/ssh/DoD-banner
    fi
}

#RM_WORK_EXCLUDE += "${PN}"
