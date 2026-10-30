FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
require recipes-bsp/u-boot/u-boot-zynq.inc

SOURCE_DATE_EPOCH = "${@int(time.time())}"

do_configure:prepend () {
        cp ${WORKDIR}/ueizynq_defconfig ${S}/configs/
}

do_configure:append () {
        if [ "${U_BOOT_AUTO_CONFIG}" = "1" ]; then
                install ${WORKDIR}/platform-auto.h ${S}/include/configs/
                install ${WORKDIR}/platform-top.h ${S}/include/configs/
        fi
}
