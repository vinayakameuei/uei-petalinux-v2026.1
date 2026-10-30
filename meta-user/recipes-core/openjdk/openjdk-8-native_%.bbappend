require openjdk-fix.inc

PACKAGECONFIG:remove = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', '', 'alsa', d)} \
"
