DESCRIPTION = "Kernel, DTB, and rootfs, generating SWU image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit swupdate

DTB_FILE:zu4ev = "system-top-4ev.dtb"
DTB_FILE:zu4eg = "system-top-4eg.dtb"
DTB_FILE:zu9eg-tsn = "system-top-9eg-tsn.dtb"

ROOTFS_FILE = "uei-image-sec-zynq-${MACHINE}.ext4"

SRC_URI = "\
    file://sw-description.in \
    file://postinstall-Image.sh \
    file://preinstall-Image.sh \
"

do_generate_swdescription() {
    sed \
        -e "s|@DTB_FILE@|${DTB_FILE}|g" \
        -e "s|@ROOTFS_FILE@|${ROOTFS_FILE}|g" \
        ${WORKDIR}/sw-description.in > ${WORKDIR}/sw-description
}

addtask generate_swdescription before do_swuimage after do_unpack

SWUPDATE_DTB = "system"
SWUPDATE_DTB:zu4ev = "system-top-4ev"
SWUPDATE_DTB:zu4eg = "system-top-4eg"
SWUPDATE_DTB:zu9eg-tsn = "system-top-9eg-tsn"
SWUPDATE_IMAGES = "Image ${SWUPDATE_DTB} uei-image-sec-zynq"

SWUPDATE_IMAGES_FSTYPES[Image] = ""
SWUPDATE_IMAGES_FSTYPES[uei-image-sec-zynq] = ".ext4"
SWUPDATE_IMAGES_FSTYPES[system-top-4ev] = ".dtb"
SWUPDATE_IMAGES_FSTYPES[system-top-4eg] = ".dtb"
SWUPDATE_IMAGES_FSTYPES[system-top-9eg-tsn] = ".dtb"

do_prepare_swu_files() {
    cp ${DEPLOY_DIR_IMAGE}/system.dtb \
       ${DEPLOY_DIR_IMAGE}/${SWUPDATE_DTB}.dtb
}

do_compress_swu() {
    zstd -f \
        ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.swu \
            -o ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.swu.zst
}

addtask prepare_swu_files before do_swuimage after do_unpack
addtask compress_swu after do_swuimage before do_build

DEPENDS += "zstd-native"
RM_WORK_EXCLUDE += "${PN}"
