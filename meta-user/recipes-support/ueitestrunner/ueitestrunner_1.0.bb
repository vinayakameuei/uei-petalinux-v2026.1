SUMMARY = "UEI Test Runner"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "ueipallib"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_DEV = "svn://subversion2.ametek.com/Software"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}', '${SVN_DEV}', d)}"

SRC_URI = " \
    ${SVN_BASE}/Common;module=UeiTestRunner;protocol=svn \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/UeiTestRunner"

EXTRA_OEMAKE = "'CC=${CC}' 'CXX=${CXX}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

PARALLEL_MAKE = ""

do_compile:prepend() {
    cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
        install -d ${D}/usr/local/bin
        install -m 0755 ${S}/ueitestrunner ${D}/usr/local/bin/.
        install -d ${D}${includedir}
        install -m 644 ${S}/test.h ${D}${includedir}
        install -m 644 ${S}/log.h ${D}${includedir}
        install -m 644 ${S}/testRunner.h ${D}${includedir}
        install -m 644 ${S}/testSequence.h ${D}${includedir}
        install -m 644 ${S}/testSuite.h ${D}${includedir}
        install -m 644 ${S}/Timing.h ${D}${includedir}
        install -m 644 ${S}/Timing.h ${D}${includedir}
}

FILES:${PN} = "\
    /usr/local/bin \
    /usr/local/bin/ueitestrunner \
"
FILES:${PN}-dev = "\
    /usr/include/* \
"


ALLOW_EMPTY_${PN} = "1"

                    
do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
