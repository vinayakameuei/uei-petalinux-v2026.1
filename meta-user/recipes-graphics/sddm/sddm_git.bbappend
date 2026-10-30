FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://00-default.conf \
"
do_install:append () {
	install -m 0644 ${WORKDIR}/00-default.conf ${D}${sysconfdir}/sddm.conf.d/.

	
}
