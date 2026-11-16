DESCRIPTION = "UEI Unified Automation OPC UA Stack"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${PN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PV = "1.1"

SRC_URI = " \
    http://10.221.7.142:8080/ueiopcuasdk-${PV}.tar.gz \
    file://INSTALL \
    file://pkgupgrade.sh \
"
SRC_URI[sha256sum] = "8651e67cb036b52d50ecf3ba67f0bd939b3c559b83d660133e3fcf5269d104f8"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${PN}-${PV}/opcua_sdk"

inherit cmake

EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=OFF"

PARALLEL_MAKE = "-j 1"

DEPENDS = " \
    openssl \
"

FILES:${PN} = " \
    ${libdir}/libuastack.so \
"

FILES:${PN}-dev = " \
    ${includedir}/baselib \
    ${includedir}/clientlib \
    ${includedir}/datalogger \
    ${includedir}/models_di \
    ${includedir}/models_plcopen \
    ${includedir}/serverlib \
    ${includedir}/uastack \
"

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

BBCLASSEXTEND = "native"
