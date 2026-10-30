DESCRIPTION = "UEI OPC Server"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.1+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SVN_MOD_NAME = "UeiOpcUa"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC;module=${SVN_MOD_NAME};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
    file://ueiopc.service \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"

S = "${WORKDIR}/${SVN_MOD_NAME}/UeiOpcServer"

inherit cmake

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=OFF"

PARALLEL_MAKE = "-j 1"

DEPENDS = " \
    openssl \
    dmapserver libpdnaconfig ueipallib \
    ueiopcuasdk \
"

do_configure:prepend() {
    export OPCUASDK_DIR="${WORKDIR}/${PN}-${PV}/opcua_sdk"
}

do_install:append() {
    install -d ${D}${sysconfdir}/opcua
    install -m 0664 ${WORKDIR}/${PN}-${PV}/settings.conf ${D}${sysconfdir}/opcua/.
    install -m 0664 ${WORKDIR}/${PN}-${PV}/ueiopc.xml ${D}${sysconfdir}/.
    install -d ${D}${sysconfdir}/systemd/system
    install -m 0755 ${WORKDIR}/ueiopc.service ${D}${sysconfdir}/systemd/system/.
    install -d ${D}/www/cgi-bin
    install -m 0755 ${WORKDIR}/${PN}-${PV}/UI/ueiopc_ui.cgi ${D}/www/cgi-bin
    install -m 0755 ${WORKDIR}/${PN}-${PV}/UI/ueiopc.html ${D}/www
}

do_compile:append() {
    cd ${WORKDIR}/${PN}-${PV}/UI
    oe_runmake all -f Makefile.arm
}

addtask do_create_pkgupgrade
do_create_pkgupgrade() {
    if [ -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz ]; then
        rm -f ${TMPDIR}/pkgupgrades/pkgupgrade-${PN}-${PV}.tgz
    fi
    install -m 0755 -d ${WORKDIR}/pkgupgrade-${PN}-${PV}
    install -m 0766 ${WORKDIR}/INSTALL ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install -m 0775 ${WORKDIR}/pkgupgrade.sh ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-${PV}-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

SYSTEMD_SERVICE:${PN} = "ueiopc.service"
SYSTEMD_AUTO_ENABLE = "disable"

BBCLASSEXTEND = "native"

FILES:${PN} += " \
    /www/ueiopc.html \
    /www/cgi-bin/ueiopc_ui.cgi \
" 
