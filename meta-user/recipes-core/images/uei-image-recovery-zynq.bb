DESCRIPTION = "UEI minimal recovery image for SWUpdate"
LICENSE = "MIT"

inherit core-image
inherit plnx-image
inherit image_types_plnx
inherit extrausers

IMAGE_FEATURES = "ssh-server-openssh allow-root-login"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    passwd-rst \
    openssh \
    swupdate \
    swupdate-www \
    mtd-utils \
    e2fsprogs \
    libgcc \
    util-linux \
    u-boot-fw-utils-xlnx \
    uei-swupdate-support \
    ueiswitchrootfs \
    "

IMAGE_FSTYPES += "ext4"

IMAGE_LINGUAS = " "
IMAGE_NAME_SUFFIX ?= ""
IMAGE_BASENAME = "${PN}-${MACHINE}"
IMAGE_LINK_NAME = "${IMAGE_BASENAME}"

EXTRA_USERS_PARAMS = "\
    usermod -p '' root; \
    "

ROOTFS_POSTPROCESS_COMMAND += "uei_recovery_enable_root_ssh; "

uei_recovery_enable_root_ssh() {
    if [ -f ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config ]; then
        sed -i \
            -e 's/^#*PermitRootLogin .*/PermitRootLogin yes/' \
            -e 's/^#*PermitEmptyPasswords .*/PermitEmptyPasswords yes/' \
            -e 's/^#*PasswordAuthentication .*/PasswordAuthentication yes/' \
            -e 's/^#*ChallengeResponseAuthentication .*/ChallengeResponseAuthentication no/' \
            ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config
    fi
}

# Expire the provisioned password so the first root login must choose a new one.
ROOTFS_POSTPROCESS_COMMAND += "expire_root_password;"

expire_root_password() {
        shadow_file="${IMAGE_ROOTFS}${sysconfdir}/shadow"

        [ -f "$shadow_file" ] || bbfatal "Root password database is missing: $shadow_file"
        sed -i -E 's/^(root:[^:]*:)[^:]*/\10/' "$shadow_file"
}

# Create devfs entries for initramfs(bundle) image.
USE_DEVFS = "${@'0' if d.getVar('INITRAMFS_IMAGE_BUNDLE') == '1' else '1'}"
IMAGE_DEVICE_TABLES:append = " files/plnx_dev_table.txt"
