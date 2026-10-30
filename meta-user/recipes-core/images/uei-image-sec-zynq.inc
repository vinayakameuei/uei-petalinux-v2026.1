# Copyright (C) 2025 UEI Inc.

DESCRIPTION = "UEI SEC Linux Image Zynqmp"
LICENSE = "MIT"

inherit \
	${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'selinux-image', '', d)}

CORE_IMAGE_EXTRA_INSTALL += " \
	packagegroup-core-selinux \
	packagegroup-uei-base \
	packagegroup-uei-tsn \
"

IMAGE_INSTALL:append = " \
	aide \
	ddrecovery \
	dosfstools \
	bash \
	cronie \
	can-utils \
	firewalld \
	libtss2 \
	libtss2-mu \
	libtss2-tcti-device \
	libtss2-tcti-mssim \
	logrotate \
	ntp \
	openscap scap-security-guide \
	os-release \
	python3-tpm2-pytss \
	passwd-rst \
	refpolicy-minimum-dev \
	rsyslog \
	trousers \
	vlock \
	vsftpd \
	shadow \
	tpm2-abrmd \
	tpm2-openssl \
	tpm2-pkcs11 \
	tpm2-pkcs11-tools \
	tpm2-tools \
	tpm2-tss \
	tpm2-tss-engine \
	tpm2-tss-engine-dev \
	tpm2-tss-engine-engines \
"

IMAGE_INSTALL:append = " \
	ueiswitchrootfs \
"

# snmp support
CORE_IMAGE_EXTRA_INSTALL:append = " net-snmp net-snmp-server net-snmp-client net-snmp-mibs"

EXTRA_USERS_PARAMS = "\
    usermod -p '' root; \
    "

ROOTFS_POSTPROCESS_COMMAND += "uei_main_enable_root_ssh; "

uei_main_enable_root_ssh() {
    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config ]; then
        sed -i \
            -e 's/^#*PermitRootLogin .*/PermitRootLogin yes/' \
			-e 's/^#*PermitEmptyPasswords .*/PermitEmptyPasswords yes/' \
            -e 's/^#*PasswordAuthentication .*/PasswordAuthentication yes/' \
            -e 's/^#*ChallengeResponseAuthentication .*/ChallengeResponseAuthentication no/' \
            ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config
    fi
}

EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin ftp;"
EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin mosquitto;"
EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin rpcuser;"
EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin tss;"
EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin sshd;"
EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin messagebus;"
EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin rpc;"
EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin ntp;"

# Fixup permissions on directories for OpenSCAP compliance
chmod_rootfs() {
	chmod go-w ${IMAGE_ROOTFS}/bin
	chmod go-w ${IMAGE_ROOTFS}/usr/bin
	chmod go-w ${IMAGE_ROOTFS}/sbin
	chmod go-w ${IMAGE_ROOTFS}/usr/sbin
	chmod go-w ${IMAGE_ROOTFS}/usr/local/sbin
	chmod g-w ${IMAGE_ROOTFS}/home
	chmod o-rwx ${IMAGE_ROOTFS}/home
}

ROOTFS_POSTPROCESS_COMMAND += "fix_license_manifest_ownership;"

fix_license_manifest_ownership() {
	manifest="${IMAGE_ROOTFS}${datadir}/common-licenses/license.manifest"

	[ -f "$manifest" ] || bbfatal "License manifest is missing: $manifest"
	chown 0:0 "$manifest" || bbfatal "Failed to set root ownership on $manifest"
}

# Expire the provisioned password so the first root login must choose a new one.
ROOTFS_POSTPROCESS_COMMAND += "expire_root_password;"

expire_root_password() {
	shadow_file="${IMAGE_ROOTFS}${sysconfdir}/shadow"

	[ -f "$shadow_file" ] || bbfatal "Root password database is missing: $shadow_file"
	sed -i -E 's/^(root:[^:]*:)[^:]*/\10/' "$shadow_file"
}

ROOTFS_POSTPROCESS_COMMAND += "enforce_dnf_gpgcheck;"

# override upstream gpgcheck=TRUE to 1
enforce_dnf_gpgcheck() {
      conf="${IMAGE_ROOTFS}${sysconfdir}/dnf/dnf.conf"

      sed -i \
          's/^[[:space:]]*gpgcheck[[:space:]]*=.*/gpgcheck=1/' \
          "$conf"

      grep -Eq '^[[:space:]]*gpgcheck[[:space:]]*=[[:space:]]*1[[:space:]]*$' \
          "$conf" || bbfatal "gpgcheck=1 is missing from $conf"
}

RM_WORK_EXCLUDE += "${PN}"
