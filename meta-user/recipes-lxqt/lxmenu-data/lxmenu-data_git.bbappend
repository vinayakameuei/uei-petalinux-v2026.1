FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
            file://lxde-uei.directory \
"

do_install:append() {
    install -m 0644 ${WORKDIR}/lxde-uei.directory ${D}${datadir}/desktop-directories/.
}
