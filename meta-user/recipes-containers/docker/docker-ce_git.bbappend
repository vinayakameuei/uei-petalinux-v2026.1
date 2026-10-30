# look for files in the layer first
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_AUTO_ENABLE_${PN} = "disable"
