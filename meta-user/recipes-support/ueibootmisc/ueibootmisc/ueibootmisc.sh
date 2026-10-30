#!/bin/sh -e
#
# ueibootmisc.sh
#
# This script is executed at the end of each multiuser runlevel.
# Make sure that the script will "exit 0" on success or any other
# value on error.
#
# SELinux prevents execution of rc.local and does not exist for 
# Secure Linux. This script replaces all functionality of rc.local.
#


# Set THP as madvise mode
if [ -e /sys/kernel/mm/transparent_hugepage/enabled ]
then
    echo madvise >/sys/kernel/mm/transparent_hugepage/enabled
fi

echo 30000 >  /proc/sys/vm/min_free_kbytes

echo "Loading UEI PDNADEV..."
modprobe pdnadev

ROFLAG=$(</etc/fstab awk -v var="/dev/root" '$1 == var {print $4}')
if [ "${ROFLAG}" = "ro" ]; then
    mkdir -p /var/volatile/home
    cp -a /home/* /var/volatile/home/.
    mount --bind /var/volatile/home /home
#else
#    if [ ! -f /etc/rc5.d/S20busybox-httpd ]; then
#        /bin/systemctl enable busybox-httpd;
#        /bin/systemctl start busybox-httpd;
#    fi
fi

#if [ ! -f /etc/rc5.d/S20busybox-inetd ]; then
#    /bin/systemctl enable busybox-inetd;
#    /bin/systemctl start busybox-inetd;
#fi
# Just start inetd directly without systemd
#/etc/init.d/busybox-inetd start

# Disable in Kirkstone for future use.
# On first boot only, set "trust" attribute on *.desktop files
#if [ -f /home/root/setguitrust.sh ]; then
#    /home/root/setguitrust.sh
#fi    

# On first boot for Secure Linux only, create fipsmodule.cnf for openssl
if [ -r /proc/sys/crypto/fips_enabled ] && [ "$(cat /proc/sys/crypto/fips_enabled)" = "1" ]; then
    FIPS_EN=$(cat /proc/sys/crypto/fips_enabled)
    if [[ ! -e /etc/ssl/fipsmodule.cnf ]]; then
        openssl fipsinstall -out /etc/ssl/fipsmodule.cnf -module /usr/lib/ossl-modules/fips.so
        cp /etc/ssl/openssl.cnf /etc/ssl/openssl.cnf.orig
        sed -i \
            -e 's,#alg_section =,alg_section =,g' \
            -e 's,#.include \/etc\/ssl\/fipsmodule.cnf,.include \/etc\/ssl\/fipsmodule.cnf,g' \
            -e 's,#fips = fips_,fips = fips_,g' \
            -e 's,default = default_,base = base_,g' \
            -e 's,\[ default_sect,\[ base_sect,g' \
            /etc/ssl/openssl.cnf
    fi    
else
    FIPS_EN=0
fi

# STIG: shadow-utils backup files are regenerated on every passwd/group edit,
# so lock down their perms/ownership on every boot rather than only at build time.
for f in /etc/shadow- /etc/gshadow-; do
    if [ -e "$f" ]; then
        chown root:root "$f"
        chmod 0000 "$f"
    fi
done

listlayers > /etc/layers.xml

#####################################################################        
# Set Default Subnet:
# DEFSUBNET1 = 192.168.100
# DEFSUBNET2 = 192.168.101
DEFSUBNET1="192.168.100"
DEFSUBNET2="192.168.101"

#####################################################################        
# Set Default IP addresses:
# DEFIPADDR1 = 192.168.100.2
# DEFIPADDR2 = 192.168.101.2
# For boards with only one eth device, always use DEFIPADDR1
DEFIPADDR1="$DEFSUBNET1.2"
DEFIPADDR2="$DEFSUBNET2.2"

#####################################################################
# Set Default Gateway addresses:
# DEFGWY1 = 192.168.100.1
# DEFGWY2 = 192.168.101.1                                
DEFGWY1="$DEFSUBNET1.1"
DEFGWY2="$DEFSUBNET1.1"

#####################################################################
# SoloX-PIO1010 IP address settings:
# SWITCH = 0           Use default IP address DEFIPADDR1
#        = 1 or 255    Use DHCP to set IP address
#        = 2 - 254     Set IP address to switch value

STR=$(cat /proc/cmdline)
rd_LAYERID=$(printf %s "${STR}" | awk -F= '$1=="ueilid"{print $2}' RS=' ')
rd_SX6REV=$(printf %s "${STR}" | awk -F= '$1=="ueilid"{print $2}' RS=' ')

# Uncomment lines below for manual testing only
#rd_LAYERID=00001010
#rd_LAYERID=00020011
#rd_LAYERID=00030011
#echo "rd_LAYERID=$rd_LAYERID"
#rd_PIOSWITCH=000000FC
#echo "rd_PIOSWITCH=$rd_PIOSWITCH"
#echo "DEFIPADDR1=$DEFIPADDR1"
#echo "DEFIPADDR2=$DEFIPADDR2"                                       

LAYERID=`echo $rd_LAYERID | cut -d" " -f10`
LAYERID=`printf '%X\n' "$(( 0x7FFF & 0x$LAYERID ))"`

SX6REV=`echo $rd_SX6REV | cut -d" " -f10`
SX6REV=`printf '%X\n' "$(( (0xFFFF0000 & 0x$SX6REV) >> 16 ))"`

# Uncomment lines below only for debugging
#echo "LAYERID=$LAYERID"
#echo "PIOSWITCH=$PIOSWITCH"

# Check kernel cmdline if TPM or TSN devices are present
UEITPM=0
UEITSN=0
if cat /proc/cmdline | grep -q "ueitpm"; then      
    echo "TPM option present..."
    UEITPM=1
fi

if cat /proc/cmdline | grep -q "ueitsn"; then    
    echo "TSN option present..."
    UEITSN=1
fi

# Check if SoloX-PIO1010
if [[ 0x$LAYERID -eq 0x1010 ]]; then
    rd_PIOSWITCH=$(printf %s "${STR}" | awk -F= '$1=="ueipiosw"{print $2}' RS=' ')
    PIOSWITCH=`echo $rd_PIOSWITCH | cut -d" " -f10`
    PIOSWITCH=`printf '%X\n' "$(( ~ 0x$PIOSWITCH ))"`
    PIOSWITCH=`printf '%d\n' "$(( 0xFF & 0x$PIOSWITCH ))"`

    echo SoloX-PIO1010

    # Create symlink setip -> setipeth1
    if [ -f /usr/bin/setip ]; then
        rm -f /usr/bin/setip
    fi
    ln -s /usr/bin/setipeth1 /usr/bin/setip

    # No eth0 on PIO1010 so remove 20-wired.network
    if [ -f /etc/systemd/network/20-wired.network ]; then
        mv /etc/systemd/network/20-wired.network /etc/systemd/network/20-wired.network.orig
    fi

    # Create 21-wired.network if it does not exist
    if [ ! -f /etc/systemd/network/21-wired.network ]; then
        echo "[Match]" > /etc/systemd/network/21-wired.network
        echo "Name=eth1" >> /etc/systemd/network/21-wired.network
        echo "" >> /etc/systemd/network/21-wired.network
        echo "[Network]" >> /etc/systemd/network/21-wired.network

        # Set IP to switch value
        if [[ $PIOSWITCH -ge 2 ]] && [[ $PIOSWITCH -le 254 ]]; then
            echo Set IP address to switch value
            echo "Address=$DEFSUBNET1.$PIOSWITCH/24" >> /etc/systemd/network/21-wired.network
            echo "Gateway=$DEFGWY1" >> /etc/systemd/network/21-wired.network
            echo "#DHCP=ipv4" >> /etc/systemd/network/21-wired.network
        # Set IP using DHCP
        elif [[ $PIOSWITCH -eq 1 ]] || [[ $PIOSWITCH -eq 255 ]]; then
            echo Use DHCP to set IP address
            echo "DHCP=ipv4" >> /etc/systemd/network/21-wired.network
        # Use default IP $DEFIPADDR1
        else
            echo Use default IP address
            echo "Address=$DEFIPADDR1/24" >> /etc/systemd/network/21-wired.network
            echo "Gateway=$DEFGWY1" >> /etc/systemd/network/21-wired.network
            echo "#DHCP=ipv4" >> /etc/systemd/network/21-wired.network
        fi

        echo "" >> /etc/systemd/network/21-wired.network
        echo "# Uncomment line below to set DNS server" >> /etc/systemd/network/21-wired.network
        echo "# Restart systemd, \"systemctl restart systemd-networkd\"" >> /etc/systemd/network/21-wired.network
        echo "#DNS=" >> /etc/systemd/network/21-wired.network
    fi

# On non PIO-1010 CPUs, generate IP configuration
else
    echo SoloX Rev. $SX6REV
    # Create symlink setip -> setipeth0
    if [ -f /usr/bin/setip ]; then
        rm -f /usr/bin/setip
    fi
    ln -sf /usr/bin/setipeth0 /usr/bin/setip

    # SoloX SJA1105 TSN chip present?
    if [[ $UEITSN -eq 1 ]]; then
        # Install SJA1105 driver module
        echo "modprobe SJA1105"
        modprobe sja1105

        # Create 20-wired.network
        if [ ! -f /etc/systemd/network/20-wired.network ]; then
            echo "[Match]" > /etc/systemd/network/20-wired.network
            echo "Name=swp3" >> /etc/systemd/network/20-wired.network
            echo "" >> /etc/systemd/network/20-wired.network
            echo "[Network]" >> /etc/systemd/network/20-wired.network
            echo "Address=$DEFIPADDR1/24" >> /etc/systemd/network/20-wired.network
            echo "Gateway=$DEFGWY1" >> /etc/systemd/network/20-wired.network
            echo "" >> /etc/systemd/network/20-wired.network
            echo "# Uncomment line below to set DNS server" >> /etc/systemd/network/20-wired.network
            echo "# Restart systemd, \"systemctl restart systemd-networkd\"" >> /etc/systemd/network/20-wired.network
            echo "#DNS=" >> /etc/systemd/network/20-wired.network
        fi

        # Create 21-wired.network
        if [ ! -f /etc/systemd/network/21-wired.network ]; then
            echo "[Match]" > /etc/systemd/network/21-wired.network
            echo "Name=swp2" >> /etc/systemd/network/21-wired.network
            echo "" >> /etc/systemd/network/21-wired.network
            echo "[Network]" >> /etc/systemd/network/21-wired.network
            echo "Address=$DEFIPADDR2/24" >> /etc/systemd/network/21-wired.network
            echo "Gateway=$DEFGWY2" >> /etc/systemd/network/21-wired.network
            echo "" >> /etc/systemd/network/21-wired.network
            echo "# Uncomment line below to set DNS server" >> /etc/systemd/network/21-wired.network
            echo "# Restart systemd, \"systemctl restart systemd-networkd\"" >> /etc/systemd/network/21-wired.network
            echo "#DNS=" >> /etc/systemd/network/21-wired.network
        fi
    else
        # Create 20-wired.network
        if [ ! -f /etc/systemd/network/20-wired.network ]; then
            echo "[Match]" > /etc/systemd/network/20-wired.network
            echo "Name=eth0" >> /etc/systemd/network/20-wired.network
            echo "" >> /etc/systemd/network/20-wired.network
            echo "[Network]" >> /etc/systemd/network/20-wired.network
            echo "Address=$DEFIPADDR1/24" >> /etc/systemd/network/20-wired.network
            echo "Gateway=$DEFGWY1" >> /etc/systemd/network/20-wired.network
            echo "" >> /etc/systemd/network/20-wired.network
            echo "# Uncomment line below to set DNS server" >> /etc/systemd/network/20-wired.network
            echo "# Restart systemd, \"systemctl restart systemd-networkd\"" >> /etc/systemd/network/20-wired.network
            echo "#DNS=" >> /etc/systemd/network/20-wired.network
        fi
                                                                                                                     
        # Create 21-wired.network
        if [ ! -f /etc/systemd/network/21-wired.network ]; then
            echo "[Match]" > /etc/systemd/network/21-wired.network
            echo "Name=eth1" >> /etc/systemd/network/21-wired.network
            echo "" >> /etc/systemd/network/21-wired.network
            echo "[Network]" >> /etc/systemd/network/21-wired.network
            echo "Address=$DEFIPADDR2/24" >> /etc/systemd/network/21-wired.network
            echo "Gateway=$DEFGWY2" >> /etc/systemd/network/21-wired.network
            echo "" >> /etc/systemd/network/21-wired.network
            echo "# Uncomment line below to set DNS server" >> /etc/systemd/network/21-wired.network
            echo "# Restart systemd, \"systemctl restart systemd-networkd\"" >> /etc/systemd/network/21-wired.network
            echo "#DNS=" >> /etc/systemd/network/21-wired.network
        fi
    fi
fi

systemctl restart systemd-networkd

# SoloX TPM chip present?
if [[ $UEITPM -eq 1 ]]; then
#    # On first boot for Secure Linux only, create tpm2module.cnf for openssl
#    if [[ ! -e /etc/ssl/tpm2module.cnf ]]; then
#        echo "[tpm2_sect]" > /etc/ssl/tpm2module.cnf
#        echo "activate = 1" >> /etc/ssl/tpm2module.cnf
#
#        sed -i \
#            -e '/# For FIPS/i \
## For TPM2-openssl \
#.include /etc/ssl/tpm2module.cnf \
# ' \
#            -e '/# included fipsmodule.cnf/a \
#tpm2 = tpm2_sect' \
#            /etc/ssl/openssl.cnf
#    fi    
    # Install TPM tpm_tis_spi driver module
        echo "modprobe tpm_tis_spi"
        modprobe tpm_tis_spi
fi

exit 0
