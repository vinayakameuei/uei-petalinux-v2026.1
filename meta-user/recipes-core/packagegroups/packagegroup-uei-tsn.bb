DESCRIPTION = "Add packages for UEI TSN Support through NTL IP"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

TSN_PACKAGES = "\
    kernel-module-ueitsn \
    libueitsn libueitsn-dev \
    tsn-ip-demo \
    uei-config-tsn \
    wireshark \
    "

RDEPENDS:${BPN} += " ${TSN_PACKAGES}"
