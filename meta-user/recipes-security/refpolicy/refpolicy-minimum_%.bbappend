# This tells bitbake where to find the files we're providing on the local filesystem
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://0001-ueiiDDS-should-not-be-confined-by-SELinux.patch \
	file://0002-Add-uei-extended-perms-for-repolicy-minimum.patch \
"

# Add lvm policy so that dmsetup and others work as expected
EXTRA_POLICY_MODULES += " \
	lvm \
	uei_lvm_xperm \
	uei_tpm2_tss \
"

do_install:append () {
	sed -i \
		-e 's,SELINUX=permissive,SELINUX=disabled,g' \
		-e '/#     standard/d' \
		-e '/#     mls/d' \
		-e '/#     targeted/d' \
		-e '/#     mcs/d' \
		${D}${sysconfdir}/selinux/config

	# While building policies on target, Makefile will be searched from SELINUX_DEVEL_PATH
	echo "SELINUX_DEVEL_PATH=${datadir}/selinux/${POLICY_NAME}" > ${D}${sysconfdir}/selinux/sepolgen.conf
}

RM_WORK_EXCLUDE += "${PN}"
