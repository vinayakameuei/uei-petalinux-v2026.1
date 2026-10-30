# Copyright (C) 2017 - 2023 Armin Kuster  <akuster808@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMARRY = "SCAP content for various platforms, upstream version"
HOME_URL = "https://www.open-scap.org/security-policies/scap-security-guide/"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9bfa86579213cb4c6adaffface6b2820"
LICENSE = "BSD-3-Clause"

#SRCREV = "da283b9a360f2f42ad642c0c9b08b061501bc667"
SRCREV = "5582fc27c1688da301e26d62b963b6a524ee3ee8"
SRC_URI = "git://github.com/ComplianceAsCode/content.git;branch=master;protocol=https \
           file://run_eval.sh \
           file://run_eval_uei.sh \
           file://run-ptest \
           file://0001-Add-UEI-support.patch \
           file://0002-Add-UEI-profile.patch \
           file://0003-Set-package-to-libselinux1-for-package_libselinux_in.patch \
           file://0004-Add-back-missing-rules-from-expanded-profile.patch \
           file://0005-Fix-require_emergency_target_auth-and-require_single.patch \
           file://0006-Fix-banner_etc_issue-and-banner_etc_motc.patch \
           file://0007-Remove-rule-configure_crypto_policy.patch \
           file://0008-Update-description-in-UEI-profile.patch \
           file://0009-Set-correct-aide_bin_path-variable-for-UEI-distro.patch \
           file://0010-Add-explanation-for-failures-of-Set-Password-Expirat.patch \
"

DEPENDS = "openscap-native python3-pyyaml-native python3-jinja2-native libxml2-native expat-native coreutils-native"

S = "${WORKDIR}/git"
B = "${S}/build"

inherit cmake pkgconfig python3native python3targetconfig ptest

STAGING_OSCAP_BUILDDIR = "${TMPDIR}/work-shared/openscap/oscap-build-artifacts"
export OSCAP_CPE_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/cpe"
export OSCAP_SCHEMA_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/schemas"
export OSCAP_XSLT_PATH="${STAGING_OSCAP_BUILDDIR}${datadir_native}/openscap/xsl"

OECMAKE_GENERATOR = "Unix Makefiles"

EXTRA_OECMAKE += "-DENABLE_PYTHON_COVERAGE=OFF -DSSG_PRODUCT_DEFAULT=OFF -DSSG_PRODUCT_OPENEMBEDDED=ON"

do_configure[depends] += "openscap-native:do_install"

do_configure:prepend () {
    sed -i -e 's:NAMES\ sed:NAMES\ ${HOSTTOOLS_DIR}/sed:g' ${S}/CMakeLists.txt
    sed -i -e 's:NAMES\ grep:NAMES\ ${HOSTTOOLS_DIR}/grep:g' ${S}/CMakeLists.txt
}

SSG_UEI_OS_ID = "petalinux"

do_configure:prepend () {
      sed -i 's|\^ID=uei-solox-xway\$|^ID=${SSG_UEI_OS_ID}$|' \
          ${S}/shared/checks/oval/installed_OS_is_uei.xml

      grep -Fq "^ID=${SSG_UEI_OS_ID}$" \
          ${S}/shared/checks/oval/installed_OS_is_uei.xml || \
          bbfatal "Failed to configure UEI OVAL OS ID"
}

do_install:append() {
    install -d ${D}${datadir}/openscap
    install  ${WORKDIR}/run_eval.sh ${D}${datadir}/openscap/.
    install  ${WORKDIR}/run_eval_uei.sh ${D}${datadir}/openscap/.
}

do_compile_ptest() {
    cd ${S}/build
    cmake ${EXTRA_OECMAKE} ../
    make 
}

do_install_ptest() {

    # remove host & work dir from tests
    for x in $(find ${S}/build -type f) ;
    do
       sed -e 's#${HOSTTOOLS_DIR}/##g' \
           -e 's#${RECIPE_SYSROOT_NATIVE}##g' \
           -e 's#${WORKDIR}#${PTEST_PATH}#g' \
           -e 's#/.*/xmllint#/usr/bin/xmllint#g' \
           -e 's#/.*/oscap#/usr/bin/oscap#g' \
           -e 's#/python3-native##g' \
           -i ${x}
    done

    for x in $(find ${S}/build-scripts -type f) ;
    do
       sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${x}
    done

    for x in $(find ${S}/tests -type f) ;
    do
       sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${x}
    done

    for x in $(find ${S}/utils -type f) ;
    do
       sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' ${x}
    done

    PDIRS="apple_os build controls products shared components applications linux_os ocp-resources tests utils ssg build-scripts"
    t=${D}/${PTEST_PATH}/git
    for d in ${PDIRS}; do
        install -d ${t}/$d
        cp -fr ${S}/$d/* ${t}/$d/.
    done
}

FILES:${PN} += "${datadir}/xml ${datadir}/openscap"

RDEPENDS:${PN} = "openscap"
RDEPENDS:${PN}-ptest = "cmake grep sed bash git python3 python3-modules python3-mypy python3-pyyaml python3-yamlpath python3-xmldiff python3-json2html python3-pandas python3-openpyxl python3-pytest libxml2-utils libxslt-bin"

COMPATIBLE_HOST:libc-musl = "null"

RM_WORK_EXCLUDE += "${PN}"
