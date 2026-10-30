/*
 * This is simple application to switch to main to recovery and vice-versa
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAIN_ARGS \
"setenv bootargs ${quietargs} console=${console},${baudrate} root=${mmcroot_main} rootwait rw clk_ignore_unused"

#define RECOVERY_ARGS \
"setenv bootargs ${quietargs} console=${console},${baudrate} root=${mmcroot_recovery} rootwait rw clk_ignore_unused"

int main(int argc, char *argv[])
{
    char cmd[512];

    if (argc != 2) {
        fprintf(stderr, "Usage: %s {main|recovery}\n", argv[0]);
        return 1;
    }

    if (strcmp(argv[1], "main") == 0) {
        printf("Switching to main image...\n");
        snprintf(cmd, sizeof(cmd),
                 "fw_setenv mmcargs '%s'", MAIN_ARGS);
    }
    else if (strcmp(argv[1], "recovery") == 0) {
        printf("Switching to recovery OS...\n");
        snprintf(cmd, sizeof(cmd),
                 "fw_setenv mmcargs '%s'", RECOVERY_ARGS);
    }
    else {
        fprintf(stderr, "Usage: %s {main|recovery}\n", argv[0]);
        return 1;
    }

    if (system(cmd) != 0) {
        fprintf(stderr, "Failed to update mmcargs\n");
        return 1;
    }

    system("fw_printenv mmcargs");
    sleep(5);
    system("reboot");

    return 0;
}
