require openjdk-fix.inc

#AJT not sure below is needed but will try again TODO
export TARGET_CFLAGS
export TARGET_CXXFLAGS

PACKAGECONFIG:remove = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', '', 'alsa', d)} \
"
