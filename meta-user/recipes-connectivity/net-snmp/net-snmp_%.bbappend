SYSTEMD_AUTO_ENABLE:${PN}-server-snmpd = "disable"

pkg_postinst:${PN}-server-snmpd () {
	if [ -n "$D" ]; then
		OPTS="--root=$D"
	fi

	systemctl $OPTS mask snmpd.service
}
