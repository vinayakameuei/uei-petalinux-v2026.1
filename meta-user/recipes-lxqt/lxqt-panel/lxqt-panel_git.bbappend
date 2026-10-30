FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG = "cpu_plugin dom mount_plugin mount_plugin networkmonitor_plugin sensor_plugin sysstat_plugin"

SRC_URI += " \
            file://panel.conf \
            file://lxqt-applications.menu \
"
do_install:append() {
    # install -d ${D}${sysconfdir}/xdg/lxqt
    install -m 0644 ${WORKDIR}/panel.conf ${D}${datadir}/lxqt/.
    install -m 0644 ${WORKDIR}/lxqt-applications.menu ${D}${sysconfdir}/xdg/menus/.
}
