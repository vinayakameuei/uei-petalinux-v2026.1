SUMMARY = "UEI webconfig application"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
DEPENDS = "daqlib libpdnaconfig ueiexamples"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATH:prepend := "${THISDIR}/${PN}:"

PV = "1.0"

SRC_URI = " \
    http://10.221.7.142:8080/ueiwebconfig-${PV}.tar.gz \
    file://INSTALL \
    file://pkgupgrade.sh \
"
SRC_URI[sha256sum] = "a7b095f7178f2cc2136f423219837c4ac3c70033f59e78f7b7efe049c89b6ec2"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

do_compile:prepend() {
    cp -a ${WORKDIR}/recipe-sysroot/usr/lib/UeiPacUtils/ ${S}/.
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
    install -m 0755 -d ${D}/www/cgi-bin
    install -m 0755 ${S}/WebConfig ${D}/www/cgi-bin/webconfig.cgi
    install -m 0644 ${S}/config.html ${D}/www/.
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

FILES:${PN} = " \
    /www/config.html \
    /www/cgi-bin/webconfig.cgi \
"

PARALLEL_MAKE = ""
