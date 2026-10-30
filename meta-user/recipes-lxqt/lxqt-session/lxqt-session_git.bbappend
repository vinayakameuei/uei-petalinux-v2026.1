FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

#session.conf will default to openbox window manager

SRC_URI += " \
	file://lxqt.conf \
	file://session.conf \
"


do_install_append() {
	install -m 0644 ${WORKDIR}/lxqt.conf ${D}${datadir}/lxqt/.
	install -m 0644 ${WORKDIR}/session.conf ${D}${datadir}/lxqt/.
	rm -f ${D}${datadir}/applications/lxqt-lockscreen.desktop
}
