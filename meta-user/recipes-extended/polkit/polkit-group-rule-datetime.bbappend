do_install:append() {
    chown polkitd:root ${D}${sysconfdir}/polkit-1/rules.d
}
