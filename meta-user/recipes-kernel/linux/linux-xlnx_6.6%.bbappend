FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://bsp.cfg \
            file://preempt-rt.cfg \
            file://uei-linux-kernel-defconfig \
            file://0001-updated-defconfig-with-rtc-pdna-and-ntl.patch \
            file://0002-Support-NTL-PHC-clock-v6.6_LTS_2024.2.patch \
            file://0003-correction-on-help-ntl-and-ntl_phc.patch \
            file://0004-Fix-build-for-NTL-PHC-driver.patch \
            file://0005-6.6.40-rt36-patch-to-xlnx_rebase_v6.6_LTS_2024.2.patch \
            file://0006-Add-rtc-uei-pdna-driver-support.patch \
            file://0007-LED-configuration-for-ZynqMP-PHY-interfaces-v6.6_LTS.patch \
            file://0008-Add-reboot-using-UEI-watchdog-v6.6_LTS_2024.2.patch \
            file://0009-Zynq-Linux-PCIe-driver-After-10-assertions-disable-t.patch \
"

KBUILD_DEFCONFIG = "uei-linux-kernel-defconfig"
KBUILD_DEFCONFIG:remove = "xilinx_defconfig"

KERNEL_FEATURES:append = " bsp.cfg preempt-rt.cfg fipslinux.cfg"
