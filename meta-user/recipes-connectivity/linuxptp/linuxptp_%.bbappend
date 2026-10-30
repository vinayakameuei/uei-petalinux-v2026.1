FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
#add direct control of PTP Hardware Clock
#do_install:append () {
#	install -p ${S}/phc_ctl  ${D}/${bindir}
#}