SUMMARY = "MDS Software Rootfs Files for UEI iDDS"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

RDEPENDS:${PN} = "bash"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

SVN_MOD_${BPN} ?= "MDS"

SRC_URI = " \
    ${SVN_BASE}/UEIPAC/UEIiDDS;module=${SVN_MOD_${BPN}};protocol=svn \
    file://INSTALL \
    file://pkgupgrade.sh \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/${SVN_MOD_${BPN}}"

inherit bin_package

do_install() {
    install -d -m 0755 -o root ${D}${prefix}/local/UEIiDDS
    install -m 0744 -o root ${S}/ship/target/armhf/mds-license.lic \
        ${D}${prefix}/local/UEIiDDS
    install -m 0755 -o root ${S}/ship/target/armhf/jimsh \
        ${D}${prefix}/local/UEIiDDS
    install -m 0755 -o root ${S}/ship/target/armhf/qos-setup \
        ${D}${prefix}/local/UEIiDDS
    install -m 0644 -o root ${S}/ship/target/armhf/qos.xml \
        ${D}${prefix}/local/UEIiDDS
    install -d -m 0755 -o root ${D}${libdir}
    cp -a ${S}/ship/target/armhf/*.so ${D}${libdir}
    install -d -m 0755 -o root ${D}${includedir}
    cp -a ${S}/ship/target/armhf/*.h ${D}${includedir}
    cp -a ${S}/ship/include/*.h ${D}${includedir}
    chown -R root:root ${D}/*
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

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"

FILES:${PN} = "\
    /usr/lib/idds_shell.so \
    /usr/lib/libdds_cf.so \
    /usr/lib/libdds_qos_provider.so \
    /usr/lib/libdds.so \
    /usr/lib/libdds_xml.so \
    /usr/lib/libEUC.so \
    /usr/lib/libidds.so \
    /usr/lib/libmdsheap.so \
    /usr/local/UEIiDDS/jimsh \
    /usr/local/UEIiDDS/mds-license.lic \
    /usr/local/UEIiDDS/qos-setup \
    /usr/local/UEIiDDS/qos.xml \
"

FILES:${PN}-dev = "\
    /usr/include/idds_sdk_log.h \
    /usr/include/idds_sdk.h \
    /usr/include/idds_sdk_config_service.h \
    /usr/include/idds_sdk_net.h \
    /usr/include/idds_sdk_attributes.h \
    /usr/include/idds_sdk_app_config.h \
    /usr/include/idds_sdk_events.h \
    /usr/include/EUCL_lib.h \
    /usr/include/idds_sdk_parameters.h \
    /usr/include/idds_sdk_time.h \
    /usr/include/idds_sdk_node.h \
    /usr/include/idds_sdk_cmd_msg.h \
"

INSANE_SKIP_${PN} += "ldflags"
INSANE_SKIP_${PN} += "already-stripped"
