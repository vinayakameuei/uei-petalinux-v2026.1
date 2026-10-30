DESCRIPTION = " Generate SWU QSPI"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit swupdate

# NOTE: Update VARIANT name below required for swu generation
VARIANT:zu4ev = "31_DNR"
VARIANT:zu4eg = "30_DNA"
VARIANT:zu9eg-tsn = "35_MIL"

MACHINE_NAME:zu4ev = "4ev"
MACHINE_NAME:zu4eg = "4eg"
MACHINE_NAME:zu9eg-tsn = "9eg-tsn"

# NOTE: Update QSPI file name below required for swu generation
SRC_URI = "\
    file://sw-description.in \
    file://postinstall-qspi.sh \
    file://qspi_boot_31_DNR_4ev.bin \
"

do_generate_swdescription() {
    sed \
        -e "s|@VARIANT@|${VARIANT}|g" \
        -e "s|@MACHINE_NAME@|${MACHINE_NAME}|g" \
        ${WORKDIR}/sw-description.in > ${WORKDIR}/sw-description
}

addtask generate_swdescription before do_swuimage after do_unpack

RM_WORK_EXCLUDE += "${PN}"
