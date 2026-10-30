#!/bin/sh

mkdir -p /run/media/boot-mmcblk0p1

mount -t vfat /dev/mmcblk0p1 /run/media/boot-mmcblk0p1
