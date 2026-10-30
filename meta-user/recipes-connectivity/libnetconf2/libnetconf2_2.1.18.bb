# Copyright (C) 2019 Ruslan Bilovol <rbilovol@cisco.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "TODO"

DESCRIPTION = "TODO"
HOMEPAGE = "TODO"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=08a5578c9bab06fb2ae84284630b973f"
SECTION = "libs"

DEPENDS = "libyang libssh openssl"

PV = "2.1.18"
SRC_URI = "https://github.com/CESNET/libnetconf2/archive/refs/tags/v${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "f684b5640f72b509a44614fd5711d045313f73926a860a9da3999a1da2662762"

S = "${WORKDIR}/libnetconf2-${PV}"

inherit cmake pkgconfig

BBCLASSEXTEND += "nativesdk"
