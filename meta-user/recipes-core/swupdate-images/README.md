# UEI SWUpdate Offline Update Starter

This directory contains a disabled SWUpdate bundle template for offline updates of:

- `/boot/Image`
- `/boot/system-top-*.dtb`
- `QSPI_BOOT.BIN`
- local RPM payloads packaged as `rpms.tar.gz`

The bundle recipe is intentionally named `uei-offline-update.bb-nouse` so this layer does not fail to parse before `meta-swupdate` is added to the PetaLinux project.

## Enable SWUpdate

1. Add the `meta-swupdate` layer to the project.
2. Install SWUpdate into the rootfs. `uei-swupdate-support` is already included by the UEI base image:

   ```bitbake
   IMAGE_INSTALL:append = " swupdate"
   ```

3. Rename `uei-offline-update.bb-nouse` to `uei-offline-update.bb`.
4. Add the actual update artifacts to `SRC_URI` in the recipe:

   ```bitbake
   SRC_URI:append = " file://Image file://system-top-4eg.dtb file://QSPI_BOOT.BIN file://rpms.tar.gz "
   ```

   Remove unused file entries from `uei-offline-update/sw-description`; every file listed there should be present in the `.swu` bundle.

5. The example recipe is unsigned for lab bring-up. For production, enable RSA signing in the recipe and provide `SWUPDATE_PRIVATE_KEY` plus the matching public key on the target.

   To generate a lab key pair:

   ```sh
   mkdir -p build/keys
   openssl genrsa -out build/keys/swupdate-priv.pem 4096
   openssl rsa -in build/keys/swupdate-priv.pem -out build/keys/swupdate-public.pem -outform PEM -pubout
   ```

## Target Notes

The current `sw-description` uses the standard libconfig syntax and defines the selectable update set as `stable/main`. Test or install it with:

```sh
swupdate -c -e stable,main -H zu4ev:1.0 -i /tmp/uei-offline-update.swu
swupdate -v -e stable,main -H zu4ev:1.0 -i /tmp/uei-offline-update.swu
```

It declares this hardware compatibility revision:

```text
1.0
```

If the target reports a different hardware revision, either add that revision string to `hardware-compatibility` in `sw-description` or pass a matching lab value with `-H <board>:<revision>`.

On the current UEI target, `/proc/mtd` shows:

```text
mtd0: 01e00000 00020000 "qspi-boot"
mtd1: 00020000 00020000 "qspi-bootenv"
mtd2: 00020000 00020000 "qspi-pdna"
```

`QSPI_BOOT.BIN` should be written to `qspi-boot`, which is `/dev/mtd0`. The `postinstall.sh` template uses `qspi-boot` by default.

The QSPI update script accepts the target MTD partition as either a device path or an MTD partition name:

```sh
/usr/bin/uei-swupdate/update-qspi.sh /tmp/QSPI_BOOT.BIN /dev/mtd0
```

or:

```sh
/usr/bin/uei-swupdate/update-qspi.sh /tmp/QSPI_BOOT.BIN qspi-boot
```

To override the default used by `postinstall.sh`:

```sh
export SWU_QSPI_MTD=qspi-boot
```

Confirm the correct target on hardware with:

```sh
cat /proc/mtd
```

Do not point this at `qspi-bootenv` or `/dev/mtd1`; that partition contains the U-Boot environment.