#AT - just need inetd.conf but copied these files from petalinux recipes
#RM_WORK_EXCLUDE += "${BPN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

#PACKAGES =+ "${PN}-inetd"
#FILES_${PN}-inetd = "${sysconfdir}/init.d/inetd.busybox ${sysconfdir}/inetd.conf"

FILES_${PN}-inetd = "${sysconfdir}/init.d/${PN}-inetd ${sysconfdir}/inetd.conf"

INITSCRIPT_PACKAGES += "${PN}-inetd"
INITSCRIPT_NAME_${PN}-inetd = "${PN}-inetd"

SRC_URI += " \
                file://inetd.conf \
                file://ftp.cfg \
                file://ftpd.cfg \
                file://hexdump.cfg \
                file://httpd.cfg \
                file://inetd.cfg \
                file://nc.cfg \
                file://telnetd.cfg \
                file://tftpd.cfg \
                "
