## Build UEI Petalinux-v2026.1 Scarthgap images for UEI Zynq (offline-bsp branch)

This branch (`offline-bsp`) is for building without access to UEI's internal SVN server.

- UEI provides the source packages as a **package feed** (tarballs). The feed is copied to `/srv/yocto/sources` on the build machine and served over HTTP during the first build (see step 6 of Build Steps).
- `meta-user/conf/machine/include/uei-zynq-base.inc` defines `UEI_FEED_URL` (default `http://10.221.7.142:8080`, UEI's internal mirror). The recipes' `SRC_URI` and `SOURCE_MIRROR_URL` in `petalinuxbsp.conf` (`own-mirrors` + `PREMIRRORS`) all use it. You must change it to the IP address of the machine serving the package feed.
- The first build fetches the feed packages and stores them in the PetaLinux downloads folder (`uei-zynqmp/build/downloads`). Later builds reuse that folder and no longer need the HTTP server, unless the downloads folder is deleted.
- A few recipes (e.g. `pdnaexplorer`, `dnxlogicupdate`, `uei-lxqt-theme`, `uei-sddm-theme`, `ueimds-rootfs`, `ueitestsuite`) still fetch from SVN and need access to the SVN server. Their `.bb-nouse` copies are kept alongside for reference.
- `meta-user/recipes-core/swupdate-images/uei-offline-update.bb` builds an offline SWU bundle (kernel `Image`, DTB and QSPI boot image). See `meta-user/recipes-core/swupdate-images/README.md`.

### System Requirement:
   1. Ubuntu 20.04 / 22.04 / 24.04 / 24.10
   2. CPU: 2 GHz or higher, minimum 8 cores
   3. RAM: 8 GB minimum. For faster builds: 16–32 GB recommended
   4. Storage: ~90 GB free disk space

### Build Requirement:

   1. Install required packages
      ```bash
      sudo apt-get update
      sudo apt-get install gcc xterm autoconf libtool texinfo zlib1g-dev gcc-multilib build-essential ncurses-dev libncurses5
      ```

   2. Download PetaLinux 2026.1 Installer
       https://www.xilinx.com/member/forms/download/xef.html?filename=petalinux-v2026.1-06061130-installer.run

### Build Steps:

   1. Install

      ```bash
      ./petalinux-v2026.1-06061130-installer.run --dir petalinux-zynqmp --platform "aarch64"
      ```

   2. Source the PetaLinux environment

      ```bash
      source settings.sh
      ```

   3. Create the project

      ```bash
      petalinux-create project --template zynqMP --name uei-zynqmp
      ```

   4. Enter uei-zynqmp folder and Clone this repository.

      ```bash
      git clone -b offline-bsp https://github.com/vinayakameuei/uei-petalinux-v2026.1.git
      ```

   5. Rename folder

      ```bash
      rm -rf project-spec/
      mv uei-petalinux-v2026.1/ project-spec/
      ```

   6. Set up the UEI source package feed (required for the very first build, once the feed is provided by UEI).

      1. Copy the package feed provided by UEI into `/srv/yocto/sources`. The package files (e.g. `paramenv-1.4.tar.gz`) must be directly inside this folder, not in a sub-folder.

         ```bash
         sudo mkdir -p /srv/yocto/sources
         sudo cp -r <path-to-uei-package-feed>/. /srv/yocto/sources/
         ```

      2. In a separate terminal, serve the feed over HTTP on port 8080 and keep it running until the first build completes.

         ```bash
         cd /srv/yocto/sources
         sudo python3 -m http.server 8080
         ```

      3. Find the IP address of this build machine.

         ```bash
         hostname -I
         ```

      4. Replace `10.221.7.142` with that IP address in `UEI_FEED_URL` of `project-spec/meta-user/conf/machine/include/uei-zynq-base.inc` (this is the only place to change), e.g. for `192.168.1.50`:

         ```bash
         sed -i 's|http://10.221.7.142:8080|http://192.168.1.50:8080|' project-spec/meta-user/conf/machine/include/uei-zynq-base.inc
         ```

      5. Check that the feed is reachable from the build machine (replace the IP address).

         ```bash
         curl -I http://192.168.1.50:8080/
         ```

      Note: if a package fails to download, check that the HTTP server is running, that `UEI_FEED_URL` has the correct IP address (no trailing slash), and that the package is present in `/srv/yocto/sources`.

   7. Use 4EG XSA as base platform build.

      ```bash
      petalinux-config --get-hw-description project-spec/hw/zu4eg.xsa
      ```

   8. In petalinux-configuration system set machine name, go to Yocto Settings -> Yocto Machine Name -> zu4eg -> exit -> exit -> save

   9. This machine configuration will be saved in `build/conf/machine/zu4eg.conf`

   10. Run the build for 4EG machine. The HTTP server from step 6 must be running; packages are downloaded from the feed into `build/downloads` during this first build.

       ```bash
       petalinux-build
       ```

   11. Build device-tree for other machine e.g; zu4ev. Follow step 7. and step 8. & run the command below for their respective machine.

       ```bash
       petalinux-build -c device-tree
       ```

   12. Make sure device-tree for all machine is generated in `uei-zynqmp/images/linux` before generating WIC image.

### Build artifacts:

Upon successful build will generate output inside folder `uei-zynqmp/build/tmp/deploy/images/zu4eg` and their respective machine folder.

### Creation of QSPI image and WIC image:

For QSPI image generation, run `scripts/build_qspi.sh <machine> <variant>` from the `scripts` folder, e.g:

```bash
cd project-spec/scripts
./build_qspi.sh 4eg 35-MIL
```

Petalinux build also keep copy output binary inside folder. Enter `uei-zynqmp/images/linux`.

Use below command to generate WIC image

```bash
cp -L ../../build/tmp/deploy/images/zu4eg/uei-image-recovery-zynq-zu4eg.ext4 .
mv uei-image-recovery-zynq-zu4eg.ext4 uei-image-recovery-zynq.ext4
petalinux-package wic --rootfs-file ../../build/tmp/deploy/images/zu4eg/uei-image-sec-zynq-zu4eg.tar.gz --wks ../../project-spec/meta-user/wic/rootfs.wks
```

### SWU package creation of QSPI image:

Make sure you are in same build for which you are creating SWU package. e.g: MACHINE = zu4eg

Once you have created QSPI image for your respective machine build using `scripts/build_qspi.sh`, you will find output as e.g: qspi_boot_35_MIL_4eg.bin

Copy qspi_boot_35_MIL_4eg.bin in `meta-user/recipes-support/swupdate-qspi/files`

Update VARIANT Name as per your QSPI image `meta-user/recipes-support/swupdate-qspi/swupdate-qspi.bb`

e.g: If you have built QSPI image for 4eg and 35_MIL, update as below:

```bitbake
VARIANT:zu4eg = "35_MIL"
```

Update SRC_URI= as per your QSPI image name `meta-user/recipes-support/swupdate-qspi/swupdate-qspi.bb`
e.g:

```bitbake
SRC_URI = "\
    file://sw-description.in \
    file://postinstall-qspi.sh \
    file://qspi_boot_35_MIL_4eg.bin \
"
```

Run the below command from your petalinux build directory (or `bitbake swupdate-qspi`)

```bash
petalinux-build -c swupdate-qspi
```

Output will be created inside: `build/tmp/deploy/images/zu4eg/swupdate-qspi-zu4eg.rootfs.swu`

### SWU package creation of RootFS image:

Make sure you are in same build machine for which you are creating SWU package. e.g: MACHINE = zu4eg

Run the below command from your petalinux build directory (or `bitbake swupdate-image`)

```bash
petalinux-build -c swupdate-image
```

Output will be created inside: `build/tmp/deploy/images/zu4eg/swupdate-image-zu4eg.rootfs.swu`


### SWU package installation steps:

Understanding is that you are running on latest image which support `ueiswitchrootfs` tool.

Boot and login Main Image, then switch to Recovery Image:

```bash
ueiswitchrootfs recovery
```

Open browser in host machine like linux/windows and login `http://<target_ip>:8080`

Note: Make sure host machine is able to ping target

Click on Software Update section and select swupdate-image-zu4ev.rootfs.swu or swupdate-qspi-zu4eg.rootfs.swu file which you generated or available from UEI engineering team.

Once you see 100% and SWUpdate successful! message, switch to Main Image

```bash
ueiswitchrootfs main
```

This will reboot and enter main image with newer rootfs or qspi.

