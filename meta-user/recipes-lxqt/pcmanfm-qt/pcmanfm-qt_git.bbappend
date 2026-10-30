do_install:append() {
    sed -i \
        -e 's,frost/lxqt-origami-light.png,uei-lxqt-theme/uei-solox-bg.jpg,g' \
         ${D}${datadir}/pcmanfm-qt/lxqt/settings.conf
}
