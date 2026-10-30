SUMMARY = "Rootfs for UEI Zynq"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

inherit deploy
RDEPENDS:${PN} = "bash"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


SRC_URI = " \
    file://rc.local \ 
    file://GetCPUTemp \ 
    file://fw_env.config \
"
S = "${WORKDIR}"

do_install(){
    install -m 0766 -d ${D}${sysconfdir}
    install -m 0766 ${S}/rc.local ${D}${sysconfdir} 
    install -m 0766 ${S}/fw_env.config ${D}${sysconfdir}

    install -m 0766 -d ${D}${bindir}
    install -m 0766 ${S}/GetCPUTemp ${D}${bindir}
}

