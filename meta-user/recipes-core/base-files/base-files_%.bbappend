FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
        file://fstab.sec \
        file://tmout.sh \
        file://secure.issue \
        file://secure.motd \
"

do_install:append(){

    # sed -i 's/\/dev\/mmcblk0p1/#\/dev\/mmcblk0p1/g' ${D}${sysconfdir}/fstab
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}" ]; then
        install -m 0644 ${WORKDIR}/fstab.sec ${D}${sysconfdir}/fstab
        install -Dm 0644 ${WORKDIR}/tmout.sh ${D}${sysconfdir}/profile.d/tmout.sh
        sed -i \
                -e 's,umask 022,umask 027,g' \
                ${D}${sysconfdir}/profile
        install -m 0600 ${WORKDIR}/secure.issue ${D}${sysconfdir}/issue
        install -m 0600 ${WORKDIR}/secure.motd ${D}${sysconfdir}/motd
        fi
}

# This ensures a clean replacement and avoids conflicts.
FILES:${PN}:remove = "${sysconfdir}/issue.net"
