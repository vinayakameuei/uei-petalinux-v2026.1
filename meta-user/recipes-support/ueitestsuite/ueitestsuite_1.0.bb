SUMMARY = "UEI Test Suite"
SECTION = "base"
LICENSE = "Proprietary"
LIC_FILES_CHKSUM = "file://${THISDIR}/../../LICENSE;md5=f7265074b9dd09a8d5473cb359ec5cf8"

RM_WORK_EXCLUDE += "${BPN}"
DEPENDS = "ueipallib ueitestrunner daqlib"
# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "${@bb.utils.contains('UEI_BUILD_LOCAL', '1', '', '${AUTOREV}', d)}"
PV = "1.0+svn${SRCPV}"

SVN_BASE = "${@bb.utils.contains('UEI_BUILD_TYPE', 'release', '${UEI_SVN}/PowerDNA', '${UEI_SVN}', d)}"

MODULE = "Test%20Suite"
SRC_URI = " \
    ${SVN_BASE};module=${MODULE};protocol=svn \
"

PACKAGE_ARCH = "${MACHINE_ARCH}"
S = "${WORKDIR}/Test%20Suite"

#EXTRA_OEMAKE = "'CC=${CC}' 'CXX=${CXX}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'BUILDDIR=${S}'"

# Uncomment this line to build debug version
#DEBUG_BUILD="1"

#INSANE_SKIP_${PN} += "ldflags"
TARGET_CC_ARCH += "${LDFLAGS}"

PARALLEL_MAKE = ""

do_compile:prepend() {
	cp ${S}/Makefile.arm ${S}/Makefile
}

do_install() {
       install -d ${D}/usr/lib
       install -m 0644 ${S}/DNxTestSuite.so ${D}/usr/lib/.
}

FILES:${PN} = "\
    /usr/lib \
    /usr/lib/DNxTestSuite.so \
"


ALLOW_EMPTY_${PN} = "1"

                    
do_fetch[lockfiles] += "${TMPDIR}/ueisvnlock.lock"
