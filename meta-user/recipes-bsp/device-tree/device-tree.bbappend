FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:zu3eg = " file://system-user-zu3eg.dtsi"
SRC_URI:append:zu4eg = " file://system-user-zu4eg.dtsi"
SRC_URI:append:zu4ev = " file://system-user-zu4ev.dtsi"
SRC_URI:append:zu4eg-tsn = " file://system-user-zu4eg-tsn.dtsi"
SRC_URI:append:zu4ev-tsn = " file://system-user-zu4ev-tsn.dtsi"
SRC_URI:append:zu9eg-tsn = " file://system-user-zu9eg-tsn.dtsi"

# configure dtb
do_configure:append:zu3eg() {
        cp ${WORKDIR}/system-user-zu3eg.dtsi ${S}/../system-user.dtsi
}
do_configure:append:zu4eg() {
        cp ${WORKDIR}/system-user-zu4eg.dtsi ${S}/../system-user.dtsi
}

do_configure:append:zu4ev() {
        cp ${WORKDIR}/system-user-zu4ev.dtsi ${S}/../system-user.dtsi
}

do_configure:append:zu4eg-tsn() {
        cp ${WORKDIR}/system-user-zu4eg-tsn.dtsi ${S}/../system-user.dtsi
}

do_configure:append:zu4ev-tsn() {
        cp ${WORKDIR}/system-user-zu4ev-tsn.dtsi ${S}/../system-user.dtsi
}

do_configure:append:zu9eg-tsn() {
        cp ${WORKDIR}/system-user-zu9eg-tsn.dtsi ${S}/../system-user.dtsi
}

# deploy dtb
do_deploy:append:zu3eg() {
        #deploy base dtb
        install -Dm 0644 ${B}/${BASE_DTS}.dtb ${PLNX_DEPLOY_DIR}/system.dtb
}

do_deploy:append:zu4eg() {
        #deploy base dtb
        install -Dm 0644 ${B}/${BASE_DTS}.dtb ${PLNX_DEPLOY_DIR}/system-top-4eg.dtb
}

do_deploy:append:zu4ev() {
        #deploy base dtb
        install -Dm 0644 ${B}/${BASE_DTS}.dtb ${PLNX_DEPLOY_DIR}/system-top-4ev.dtb
}

do_deploy:append:zu4eg-tsn() {
        #deploy base dtb
        install -Dm 0644 ${B}/${BASE_DTS}.dtb ${PLNX_DEPLOY_DIR}/system-top-4eg-tsn.dtb
}

do_deploy:append:zu4ev-tsn() {
        #deploy base dtb
        install -Dm 0644 ${B}/${BASE_DTS}.dtb ${PLNX_DEPLOY_DIR}/system-top-4ev-tsn.dtb
}

do_deploy:append:zu9eg-tsn() {
        #deploy base dtb
        install -Dm 0644 ${B}/${BASE_DTS}.dtb ${PLNX_DEPLOY_DIR}/system-top-9eg-tsn.dtb
}

require ${@'device-tree-sdt.inc' if d.getVar('SYSTEM_DTFILE') != '' else ''}
