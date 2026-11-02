FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://bsp.cfg \
            file://preempt-rt.cfg \
            file://uei-dp-connector.cfg \
            file://uei-linux-kernel-defconfig \
            file://0001-6.18.37-rt6-patch-to-xlnx_rebase_v6.18_LTS_2026.1_up.patch \
            file://v6.18/0001-updated-defconfig-with-rtc-pdna-and-ntl.patch \
            file://v6.18/0002-Support-NTL-PHC-clock-v6.6_LTS_2024.2.patch \
            file://v6.18/0003-correction-on-help-ntl-and-ntl_phc.patch \
            file://v6.18/0004-Fix-build-for-NTL-PHC-driver.patch \
            file://v6.18/0005-Add-rtc-uei-pdna-driver-support.patch \
            file://v6.18/0006-LED-configuration-for-ZynqMP-PHY-interfaces-v6.6_LTS.patch \
            file://v6.18/0007-Add-reboot-using-UEI-watchdog-v6.6_LTS_2024.2.patch \
            file://v6.18/0008-Zynq-Linux-PCIe-driver-After-10-assertions-disable-t.patch \
            file://v6.18/0009-ntl_phc-platform-driver-remove-returns-void-since-Li.patch \
            file://v6.18/0010-phy-zynqmp-keep-psu_init-lane-ref-clock-selection.patch \
"

# uei-linux-kernel-defconfig is supplied via SRC_URI and is not in the kernel tree,
# so KBUILD_DEFCONFIG must be empty for the SRC_URI defconfig to be used.
KBUILD_DEFCONFIG = ""

# Must match CONFIG_LOCALVERSION in uei-linux-kernel-defconfig; the recipe forces it into .config
LINUX_VERSION_EXTENSION = "-uei-zynq"

FIT_CONF_DEFAULT_DTB = "system-top.dtb"

KERNEL_FEATURES:append = " bsp.cfg preempt-rt.cfg fipslinux.cfg uei-dp-connector.cfg"
RM_WORK_EXCLUDE += "${PN}"
