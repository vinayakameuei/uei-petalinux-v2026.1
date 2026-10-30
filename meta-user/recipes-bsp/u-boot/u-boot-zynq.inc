SRC_URI:append = " \
    file://platform-top.h \
    file://bsp.cfg \
    file://0001-Add-UEI-zynq-support-with-licensing-v2025.02.patch \
    file://0002-Reset-the-USB-PHY-before-a-boot.patch \
    file://0003-Enable-reset-support.patch \
    file://0004-Use-the-reset-line-to-reset-all-PHYs-before-configur.patch \
    file://0005-Add-clk_ignore_unused-to-the-boot-sequence.patch \
    file://0006-support-external-phy-mode-rgmii-id.patch \
    file://0007-Provide-phy-interface-mode-to-converter.patch \
    file://0008-On-TSN-toggle-reset-for-PHY-control-logic.patch \
    file://0009-Add-support-for-4EG-TSN.patch \
    file://0010-u-boot-add-support-for-9EG-TSN.patch \
    file://0011-u-boot-enable-security-option.patch \
    file://0012-remove-usb-check-for-swupdate.patch \
    file://0013-set-zu4-partial-identifier-for-4eg.patch \
    file://ueizynq_defconfig \
"
