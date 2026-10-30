# This tells bitbake where to find the files we're providing on the local filesystem
#FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

do_install:append() {
    sed -i -e 's,nullok_secure,nullok_secure nodelay,g' ${D}${sysconfdir}/pam.d/common-auth
}

