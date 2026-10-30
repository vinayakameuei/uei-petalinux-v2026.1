#!/bin/sh

sync

if mountpoint -q /run/media/boot-mmcblk0p1; then
    umount /run/media/boot-mmcblk0p1
fi

exit $?
