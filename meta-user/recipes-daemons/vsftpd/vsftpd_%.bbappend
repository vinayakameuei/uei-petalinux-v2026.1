SYSTEMD_AUTO_ENABLE = "disable"

PACKAGE_WRITE_DEPS:append = " ${@bb.utils.contains('DISTRO_FEATURES','systemd selinux','systemd-systemctl-native','',d)}"
pkg_postinst:${PN} () {
	# If selinux is enabled, mask the vsftpd init script
	if ${@bb.utils.contains('DISTRO_FEATURES','selinux','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask ${PN}.service
	fi
}
