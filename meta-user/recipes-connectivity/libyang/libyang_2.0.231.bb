# Copyright (C) 2019 Ruslan Bilovol <rbilovol@cisco.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "TODO"

DESCRIPTION = "TODO"
HOMEPAGE = "TODO"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f3916d7d8d42a6508d0ea418cfff10ad"
SECTION = "libs"

DEPENDS = "libpcre2"

PV = "2.0.231"

SRC_URI = "https://github.com/CESNET/libyang/archive/refs/tags/v${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "50c63a131bca2bee5867fd00f0e3357d9ed3d2a68e7ab7386af467e6d630ca31"

S = "${WORKDIR}/libyang-${PV}"

inherit cmake pkgconfig

FILES_${PN} += " \
    /usr/share \
    /usr/share/libyang \
    /usr/share/libyang/* \
    "

BBCLASSEXTEND += "nativesdk"
#
# We will skip parsing this packagegeoup for non-glibc systems
#


