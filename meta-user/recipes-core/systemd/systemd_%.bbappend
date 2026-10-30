FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://20-wired.network \
    file://21-wired.network \
    file://22-wired.network \
"

PACKAGECONFIG:append = " \
    coredump \
"

PACKAGECONFIG:remove = " \
    hostnamed \
"

#ensure timesync is disabled by default
do_configure:append() {
    sed -i -e "s/enable systemd-timesyncd.service/disable systemd-timesyncd.service/g" ${S}/presets/90-systemd.preset
}

do_install:append() {
        if [ "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}" ]; then
                sed -i \
                        -e 's,size=50,exec\,size=25,g' \
                        ${D}${systemd_system_unitdir}/tmp.mount
                sed -i \
                        -e 's,#Storage=auto,Storage=persistent,g' \
                        -e 's,#Compress=yes,Compress=yes,g' \
                        -e 's,#ForwardToSyslog=no,ForwardToSyslog=yes,g' \
                        ${D}${sysconfdir}/systemd/journald.conf
		# STIG sysctl_net_ipv4_conf_default_rp_filter: vendor default is loose (2), not strict (1)
		sed -i \
			-e 's,^net.ipv4.conf.default.rp_filter = 2,net.ipv4.conf.default.rp_filter = 1,g' \
			-e 's,^net.ipv4.conf.*.rp_filter = 2,net.ipv4.conf.all.rp_filter = 1,g' \
			${D}/usr/lib/sysctl.d/50-default.conf
        fi
}

do_install:append() {
    rm -rf ${D}${sysconfdir}/systemd/system/sysinit.target.wants/systemd-timesyncd.service
    install -d ${D}${sysconfdir}/systemd/network
    install -m 644 ${WORKDIR}/20-wired.network ${D}${sysconfdir}/systemd/network
    install -m 644 ${WORKDIR}/21-wired.network ${D}${sysconfdir}/systemd/network
    install -m 644 ${WORKDIR}/22-wired.network ${D}${sysconfdir}/systemd/network

}
