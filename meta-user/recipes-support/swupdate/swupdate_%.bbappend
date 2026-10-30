FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://swupdate-zu4ev.service \
    file://swupdate-zu4eg.service \
    file://swupdate-zu9eg-tsn.service \
"

do_install:append() {
    install -d ${D}${libdir}/systemd/system

    install -m 0644 \
        ${WORKDIR}/swupdate-${MACHINE}.service \
        ${D}${libdir}/systemd/system/swupdate.service
}

PACKAGECONFIG:append = " mongoose"
