inherit extrausers

DESCRIPTION = "UEI Image Zynqmp"
LICENSE = "MIT"

EXTRA_PYTHON_PACKAGES = " \
        packagegroup-petalinux-pynq \
        python3-lxml \
        python3-multiprocessing \
        python3-pandas \
        python3-pyserial \
    "

USB_WIFI_PACKAGES = "\
        hostapd \
        linux-firmware-rtl8192cu \
        wpa-supplicant \
    "

IMAGE_INSTALL:append = " edid-decode ${USB_WIFI_PACKAGES} "

DISTRO_FEATURES:remove = " wayland xen rauc wifi opengl bluetooth virtualization openamp x11"

IMAGE_FEATURES:remove = " debug-tweaks"

IMAGE_FEATURES += " \
        eclipse-debug \
        package-management \
        tools-debug \
        tools-profile \
        tools-sdk \
        ssh-server-openssh \
        hwcodecs \
"
CORE_IMAGE_EXTRA_INSTALL:append = " \
        haveged \
        packagegroup-core-boot \
"

GUI_PACKAGES = " \
        libmali-xlnx \
        desktop-file-utils \
        uei-setguitrust \
        "

#remove for now kernel-devsrc
MISC_PACKAGES = " \
        ethtool \
        gsl gsl-dev \
        iproute2 \
        iproute2-tc \
        libmodbus libmodbus-dev \
        libmosquitto1 libmosquittopp1 mosquitto-dev \
        libwebsockets libwebsockets-dev \
        linuxptp \
        python3-numpy python3-pip \
        tcpdump \
        tzdata \
	u-boot-fw-utils-xlnx \
        uei-swupdate-support \
        vsftpd \
        zeromq zeromq-dev \
        zlib-dev \
        "

IMAGE_INSTALL:append = " packagegroup-uei-base packagegroup-uei-tsn ${MISC_PACKAGES} packagegroup-core-buildessential"

# removed to avoid conflicts, need to revisit
IMAGE_INSTALL:remove = " iperf3 fpga-manager-script"

EXTRA_USERS_PARAMS = "\
	usermod -a -G sudo,audio,video root; \
	usermod -a -G root,sudo,adm,lp,dialout,audio,video,systemd-journal,systemd-network root; \
	"
