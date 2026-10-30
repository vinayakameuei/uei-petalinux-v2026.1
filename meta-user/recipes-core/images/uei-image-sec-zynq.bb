# Copyright (C) 2025 UEI Inc.

DESCRIPTION = "UEI SEC Linux Image Zynqmp"
LICENSE = "MIT"

require uei-image-common-zynq.inc
require uei-image-base-zynq.inc
require uei-image-sec-zynq.inc
include conf/distro/include/cve-extra-exclusions.inc

EXTRA_USERS_PARAMS:append = " usermod -s /sbin/nologin sshd;"

IMAGE_NAME_SUFFIX ?= ""
