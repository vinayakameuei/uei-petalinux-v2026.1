# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://openssl.cnf.sec \
"

PACKAGECONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' fips', '', d)}"
PACKAGECONFIG[fips] = "enable-fips"

PACKAGECONFIG:append:class-native = "${@bb.utils.contains('DISTRO_FEATURES', 'selinux', ' fips', '', d)}"

do_install:append() {
      if ${@bb.utils.contains('PACKAGECONFIG', 'fips', 'true', 'false', d)}; then
          install -d ${D}${libdir}/ossl-modules ${D}${sysconfdir}/ssl
          install -m 0755 ${B}/providers/fips.so \
              ${D}${libdir}/ossl-modules/fips.so
          install -m 0644 ${B}/providers/fipsmodule.cnf \
              ${D}${sysconfdir}/ssl/fipsmodule.cnf
      fi
}

do_install:append() {

	if [ "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}" ]; then
		install -m 0644 ${WORKDIR}/openssl.cnf.sec ${D}${sysconfdir}/ssl/openssl.cnf
	fi
}


FILES:${PN}:append = " ${libdir}/ossl-modules/fips.so"
FILES:openssl-conf:append = " ${sysconfdir}/ssl/fipsmodule.cnf"
RM_WORK_EXCLUDE += "${PN}"
