SUMMARY = "UEIPAC Support Rootfs Files"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

RDEPENDS:${PN} = "bash"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_BASE = "svn://subversion2.ametek.com/Software/PowerDNA/Zynq/Yocto/files"

SRC_URI = " \
    ${SVN_BASE};module=${BPN};protocol=svn \
    file://0001-refactor-and-add-recovery-image.patch \
"

DEPENDS = "daqlib"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${BPN}"

ROOT_HOME = "/home/root"

inherit bin_package

do_install() {
    install -d -m 0755 -o root ${D}${bindir}
    install -m 0755 -o root ${S}${bindir}/devtbl ${D}${bindir}
    install -m 0755 -o root ${S}${bindir}/wpaconfig ${D}${bindir}
    install -m 0700 -o root ${S}${bindir}/setipeth* ${D}${bindir}
    install -m 0700 -o root ${S}${bindir}/ddimage.sh ${D}${bindir}
    install -d ${D}${sysconfdir}
    cp -a ${S}${sysconfdir}/* ${D}${sysconfdir}/.
    chmod 0755  ${D}${sysconfdir}/init.d/busybox-httpd
    chmod 0755  ${D}${sysconfdir}/init.d/busybox-inetd
    install -d ${D}${ROOT_HOME}/Desktop
    cp -a ${S}${ROOT_HOME}/Desktop/* ${D}${ROOT_HOME}/Desktop/.
    install -d ${D}${ROOT_HOME}/.local/share/applications/
    cp -a ${S}${ROOT_HOME}/.local/share/applications/* ${D}${ROOT_HOME}/.local/share/applications/.
    install -d ${D}${datadir}/applications
    install -m 0644 ${S}${datadir}/applications/* ${D}${datadir}/applications/.
    install -d ${D}/usr/local/html
    install -m 0644 ${S}/usr/local/html/solox-welcome.html ${D}/usr/local/html/.
    cd ${D}${bindir}/
    ln -s setipeth0 setip
    chown -R root:root ${D}/*
}

#modify installed chromium desktop homepage
do_install:append(){
    sed -i 's,file:///usr/local/html/solox-welcome.html,file:///www/index.html,g' ${D}${ROOT_HOME}/Desktop/chromium.desktop
    #mmcblk12 needs to be replaced with mmcblk012
    sed -i 's,12,012,g' ${D}${bindir}/ddimage.sh
}
do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
