SUMMARY = "UEI Vistas"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

DEPENDS = "daqlib libpdnaconfig ueipallib"

# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.0"

SRC_URI = " \
    http://10.221.7.142:8080/ueivistas-${PV}.tar.gz \
    file://INSTALL \
    file://pkgupgrade.sh \
"
SRC_URI[sha256sum] = "7b2ed3e9adadb4e27cfdaa76a2a867efca5da8933d61ee7c2b8697d77f26b5a9"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

do_configure:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install () {
    install -d ${D}${bindir}
    oe_runmake install UEIPACROOT=${D}
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

PARALLEL_MAKE = ""

FILES:${PN} = " \
    /usr/bin/UeiVistas \
"

RM_WORK_EXCLUDE += "${PN}"
