## How to build Petalinux-v2025.2 Scarthgap images for UEI Zynq

### System Requirement:
   1. Ubuntu 20.04 / 22.04 / 24.04 / 24.10
   2. CPU: 2 GHz or higher, minimum 8 cores
   3. RAM: 8 GB minimum. For faster builds: 16–32 GB recommended
   4. Storage: ~90 GB free disk space

### Build Requirement:

   1. Install required packages
      `$ sudo apt-get update`

      `$ sudo apt-get install gcc xterm autoconf libtool texinfo zlib1g-dev gcc-multilib build-essential ncurses-dev libncurses5`

   3. Download PetaLinux 2026.1 Installer
       https://www.xilinx.com/member/forms/download/xef.html?filename=petalinux-v2026.1-06061130-installer.run

### Build Steps:

   1. Install
     `$ ./petalinux-v2026.1-06061130-installer.run --dir petalinux-zynqmp --platform "aarch64"`

   2. `$ source settings.sh`

   3. `$ petalinux-create project --template zynqMP --name uei-zynqmp`

   4. Enter uei-zynqmp folder and Clone this repository.

      `$ git clone -b master https://github.com/UEI-AMETEK/uei-petalinux-v2026.1.git`

   5. Rename folder

      `$ rm -rf project-spec/`

      `$ mv uei-petalinux-v2026.1/ project-spec/`

   7. Use 4EG XSA as base platform build.

      `$ petalinux-config --get-hw-description project-spec/hw/zu4eg.xsa`

   8. In petalinux-configuration system set machine name, go to Yocto Settings -> Yocto Machine Name -> zu4eg -> exit -> exit -> save

   9. This machine configuration will be saved in `build/conf/machine/zu4eg.conf`

   10. Run `$ petalinux-build` for 4EG machine

   11. Build device-tree for other machine e.g; zu4ev. Follow step 6. and step 7. & Run `$ petalinux-build -c device-tree` for their respective machine.

   12. Make sure device-tree for all machine is generated in `uei-zynqmp/images/linux` before generating WIC image.

### Build artifacts:

Upon successful build will generate output inside folder `uei-zynqmp/build/tmp/deploy/images/zu4eg` and their respective machine folder.

### Creation of QSPI image and WIC image:

For QSPI image generation follow and run `scripts/build_qspi.sh`

Petalinux build also keep copy output binary inside folder. Enter `uei-zynqmp/images/linux`.

Use below command to generate WIC image

    $ cp -L ../../build/tmp/deploy/images/zu4eg/uei-image-recovery-zynq-zu4eg.ext4 .
    $ mv uei-image-recovery-zynq-zu4eg.ext4 uei-image-recovery-zynq.ext4
    $ petalinux-package wic   --rootfs-file ../../build/tmp/deploy/images/zu4eg/uei-image-sec-zynq-zu4eg.tar.gz   --wks ../../project-spec/meta-user/wic/rootfs.wks

### SWU package creation of QSPI image:

Make sure you are in same build for which you are creating SWU package. e.g: MACHINE = zu4eg

Once you have created QSPI image for your respective machine build using `scripts/build_qspi.sh`, you will find output as e.g: qspi_boot_35_MIL_4eg.bin

Copy qspi_boot_35_MIL_4eg.bin in `meta-user/recipes-support/swupdate-qspi/files`

Update VARIANT Name as per your QSPI image `meta-user/recipes-support/swupdate-qspi/swupdate-qspi.bb`

e.g: If you have built QSPI image for 4eg and 35_MIL, update as below:

`VARIANT:zu4eg = "35_MIL"`

Update SRC_URI= as per your QSPI image name `meta-user/recipes-support/swupdate-qspi/swupdate-qspi.bb`
e.g:

SRC_URI = "\
    file://sw-description.in \
    file://postinstall-qspi.sh \
    file://qspi_boot_35_MIL_4eg.bin \
"

Run `$ bitbake swupdate-qspi` or `petalinux-build -c swupdate-qspi` from your petalinux build directory

Output will be created inside: `build/tmp/deploy/images/zu4eg/swupdate-qspi-zu4eg.rootfs.swu`

### SWU package creation of RootFS image:

Make sure you are in same build machine for which you are creating SWU package. e.g: MACHINE = zu4eg

Run `$ bitbake swupdate-image` or `petalinux-build -c swupdate-image` from your petalinux build directory

Output will be created inside: `build/tmp/deploy/images/zu4eg/swupdate-image-zu4eg.rootfs.swu`


### SWU package installation steps:

Understanding is that you are running on latest image which support `ueiswitchrootfs` tool.

Boot and login Main Image, then switch to Recovery Image:

`root@ueizynq:~# ueiswitchrootfs recovery`

Open browser in host machine like linux/windows and login `http://<target_ip>:8080`

Note: Make sure host machine is able to ping target

Click on Software Update section and select swupdate-image-zu4ev.rootfs.swu or swupdate-qspi-zu4eg.rootfs.swu file which you generated or available from UEI engineering team.

Once you see 100% and SWUpdate successful! message, switch to Main Image

`root@ueizynq:~# ueiswitchrootfs main`

This will reboot and enter main image with newer rootfs or qspi.

