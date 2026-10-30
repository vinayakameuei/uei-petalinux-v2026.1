DESCRIPTION = "UEI PowerDNA Explorer"
PRIORITY = "optional"
SECTION = "java/application"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

DEPENDS = "ant-native openjdk-8-native"

# PACKAGE_ARCH definition must exist before "inherit" declaration
PACKAGE_ARCH = "${MACHINE_ARCH}"

#inherit java-library

SVN_DEV = "svn://subversion2.ametek.com/Software"

SVN_BASE_POWERDNA = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}', \
    '${SVN_DEV}/PowerDNA', d)}"

SVN_BASE_COMMON = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}', \
    '${SVN_DEV}', d)}"

SVN_URI_POWERDNA ?= "${SVN_BASE_POWERDNA}/Java/"
SVN_URI_COMMON ?= "${SVN_BASE_COMMON}/Common/Java"

SRCREV_FORMAT = "v5.0.0"
SRCREV_pdnaex = "${AUTOREV}"
SRCREV_javadaqlib = "${AUTOREV}"
SRCREV_ueiui = "${AUTOREV}"

PV = "1.2+uei_${SRCPV}"

#AJT removed path_spec
SRC_URI = " \
    ${SVN_URI_POWERDNA};module=PowerDNA_Explorer;protocol=svn;name=pdnaex \
    ${SVN_URI_POWERDNA};module=JavaDaqLib;protocol=svn;name=javadaqlib \
    ${SVN_URI_COMMON};module=UeiUi;protocol=svn;name=ueiui \
    file://PDNAExplorer \
    file://bigicon.png \
    file://smallicon.png \
    file://INSTALL \
    file://pkgupgrade.sh \
"

S = "${WORKDIR}/${PN}"

JAR = "${PN}-${PV}.jar"

do_removebinaries[noexec] = "1"

do_compile() {
    export J2REDIR=""
    export J2SDKDIR=""
    export JAVA_HOME="${WORKDIR}/recipe-sysroot-native${libdir}/jvm/openjdk-8-native"
    ant -f ${S}/../UeiUi/yocto_build.xml
    ant -f ${S}/../JavaDaqLib/yocto_build.xml
    ant -f ${S}/../PowerDNA_Explorer/yocto_build.xml
}

do_install() {
    cp ${S}/../PowerDNA_Explorer/PowerDNAExplorer.jar ${PN}-${PV}.jar
    install -m 0755 -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/PDNAExplorer ${D}${bindir}
    install -d ${D}${datadir}/pixmaps
    install -m 0644 ${WORKDIR}/*.png ${D}${datadir}/pixmaps/.
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
    install ${WORKDIR}/deploy-rpms/${RPM_DIR}/lib${PN}-* ${WORKDIR}/pkgupgrade-${PN}-${PV}/.
    tar czvf ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz -C ${WORKDIR}/ pkgupgrade-${PN}-${PV}/
    if [ ! -d ${TMPDIR}/pkgupgrades ]; then
        install -d ${TMPDIR}/pkgupgrades
    fi
    install ${WORKDIR}/pkgupgrade-${PN}-${PV}.tgz ${TMPDIR}/pkgupgrades
}

do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"

