SUMMARY = "DMAP Server Library"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
DEPENDS = "daqlib libpdnaconfig libshareddata libwebsockets \
           mosquitto ueipallib zeromq zlib"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.0"

SRC_URI = " \
    http://10.221.7.142:8080/dmapserver-${PV}.tar.gz \
    file://INSTALL \
    file://pkgupgrade.sh \
"
SRC_URI[sha256sum] = "12c9895c701c5583f872a98681aad43a31e493fa024cdae0d186d1a100916251"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

do_compile:prepend() {
	cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
    install -d ${D}${includedir}
    install -m 644 ${S}/*.h ${D}${includedir}
    rm -rf ${D}${includedir}/pugxml.h
    install -d ${D}${base_libdir}
    if [ -e ${S}/libDmapServer.a ]; then
        install ${S}/libDmapServer.a ${D}${base_libdir}
    else
        install ${S}/libDmapServerD.a ${D}${base_libdir}
    fi
    install -d ${D}${sbindir}
    install -m 755 ${S}/testclient/testclient ${D}${sbindir}/test.libDmapServer
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
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-dev-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/${PN}-staticdev-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

PARALLEL_MAKE = ""

