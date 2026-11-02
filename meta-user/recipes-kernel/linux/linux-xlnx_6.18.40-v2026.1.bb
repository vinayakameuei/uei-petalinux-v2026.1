LINUX_VERSION = "6.18.40"
YOCTO_META ?= "git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.18;destsuffix=yocto-kmeta"
# Xilinx/linux-xlnx tag xlnx_rebase_v6.18_LTS_2026.1_update_merge_v6.18.40
KBRANCH="xlnx_rebase_v6.18_LTS_2026.1_update"
SRCREV = "c3faafdafc13d2592825d909395b95da390dc2be"
SRCREV_meta = "f202157d297182244bde71c8ed8b6d4946deec8f"

KCONF_AUDIT_LEVEL="0"

# linux-xlnx-kmeta and the other kernel cfg files live next to the meta-xilinx-core recipes
FILESEXTRAPATHS:prepend := "${@os.path.dirname(bb.utils.which(d.getVar('BBPATH'), 'recipes-kernel/linux/linux-xlnx.inc'))}/linux-xlnx:"

require recipes-kernel/linux/linux-xlnx.inc

# CVE exclusions (no 6.18.40 specific list is available)
include recipes-kernel/linux/cve-exclusion-linux-xlnx.inc

PV .= "+v2026.1"
