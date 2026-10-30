SUMMARY = "U-Boot bootloader fw_printenv/setenv utilities"
DEPENDS += "mtd-utils"

UBOOT_VERSION = "v2025.02"

UBRANCH ?= "xlnx_rebase_v2025.01"

SRCREV ?= "5e0d8abc7e092b62a5de17aa7aadfad7af5f0f59"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://README;beginline=1;endline=4;md5=744e7e3bb0c94b4b9f6b3db3bf893897"

# u-boot-xlnx has support for these
HAS_PLATFORM_INIT ?= " \
                xilinx_zynqmp_virt_config \
                "

#UBOOT_MACHINE = "xilinx_zynqmp_virt_config"

DEPENDS += "bc-native dtc-native bison-native"

#XILINX_RELEASE_VERSION ?= ""
UBOOT_VERSION_EXTENSION ?= "-xilinx"
PV = "${UBOOT_VERSION}${UBOOT_VERSION_EXTENSION}+git${SRCPV}"

UBOOTURI ?= "git://github.com/Xilinx/u-boot-xlnx.git;protocol=https"
UBRANCH ?= ""
UBRANCHARG = "${@['nobranch=1', 'branch=${UBRANCH}'][d.getVar('UBRANCH', True) != '']}"

SRC_URI = "${UBOOTURI};${UBRANCHARG}"

S = "${WORKDIR}/git"

INSANE_SKIP:${PN} = "already-stripped"
EXTRA_OEMAKE:class-target = 'CROSS_COMPILE=${TARGET_PREFIX} CC="${CC} ${CFLAGS} ${LDFLAGS}" HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}" V=1'
EXTRA_OEMAKE:class-cross = 'HOSTCC="${CC} ${CFLAGS} ${LDFLAGS}" V=1'

inherit uboot-config

do_compile () {
        oe_runmake ${UBOOT_MACHINE}
        oe_runmake envtools
}

do_install () {
        install -d ${D}${base_sbindir}
        install -d ${D}${sysconfdir}
        install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
        install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_setenv
        #install -m 0644 ${S}/tools/env/fw_env.config ${D}${sysconfdir}/fw_env.config
}

do_install:class-cross () {
        install -d ${D}${bindir_cross}
        install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_printenv
        install -m 755 ${S}/tools/env/fw_printenv ${D}${bindir_cross}/fw_setenv
}

SYSROOT_DIRS:append_class-cross = " ${bindir_cross}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
BBCLASSEXTEND = "cross"

