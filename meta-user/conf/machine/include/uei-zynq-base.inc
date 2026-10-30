PREFERRED_VERSION_linux-xlnx = "6.6.40+git"
PREFERRED_VERSION_scap-security-guide = "0.1.69"

UBOOT_MACHINE = "ueizynq_defconfig"

UEI_BUILD_TYPE="devel"
UEI_SVN="svn://subversion2.ametek.com/Software/PowerDNA/3.3.x"

SERIAL_CONSOLES = "57600;ttyPS0"

MACHINEOVERRIDES .= ":zu_family"

# DTB's required for WIC image
DTBS = " \
   system-top-4ev.dtb \
   system-top-4eg.dtb \
   system-top-9eg-tsn.dtb \
   "

IMAGE_BOOT_FILES:append:zu_family = " Image ${DTBS}"

IMAGE_FSTYPES += "tar.gz cpio.gz.u-boot jffs2 wic.bmap wic.bz2"

# BusyBox features
BUSYBOX_FEATURES = " \
   busybox-hd \
   busybox-ftpd \
   busybox-ftp \
   busybox-telnetd \
   busybox-mdev \
   busybox-inetd \
   busybox-httpd \
"

# Use systemd as default init manager
DISTRO_FEATURES:remove = "sysvinit"
DISTRO_FEATURES:append = " systemd"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_syslog = "sysklogd"
VIRTUAL-RUNTIME_base-utils-syslog = ""
PREFERRED_PROVIDER_udev = "systemd"
PREFERRED_PROVIDER_udev-utils = "systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED = "sysvinit"

DISTRO_FEATURES:append = " ${BUSYBOX_FEATURES}"
