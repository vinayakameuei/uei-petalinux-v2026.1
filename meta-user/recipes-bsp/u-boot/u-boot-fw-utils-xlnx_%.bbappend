FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
require recipes-bsp/u-boot/u-boot-zynq.inc

RM_WORK_EXCLUDE += "${PN}"

SRC_URI:append = " file://fw_env.config"

do_configure:prepend () {
        cp ${S}/../ueizynq_defconfig ${S}/configs/
}

do_install:append:class-target () {
        install -m 0644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
}
