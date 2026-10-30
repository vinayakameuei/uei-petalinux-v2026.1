SUMMARY = "QSPI Image for UEI Zynq"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

inherit deploy 
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"


SRC_URI = " \
    file://QSPI_BOOT.BIN \
"
S = "${WORKDIR}"

do_install(){
     install -d ${D}/boot
     install -m 0766 ${S}/QSPI_BOOT.BIN ${D}/boot/QSPI_BOOT.BIN
}
do_deploy(){
  install -d ${DEPLOYDIR}
  install -m 0766 ${S}/QSPI_BOOT.BIN ${DEPLOYDIR}/QSPI_BOOT.BIN
}
addtask deploy before do_build after do_install

FILES:${PN} += "\
  /boot \
  /boot/QSPI_BOOT.BIN \
"

do_deploy[depends] += "ueiboot-qspi-rfs:do_deploy"

PACKAGE_ARCH = "${MACHINE_ARCH}"

