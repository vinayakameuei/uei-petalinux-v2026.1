DESCRIPTION = "Add packages for UEI apps/libs/drivers"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

# ueiboot-qspi-rfs removed for now since we have many qspi images

UEI_ESSENTIALS = " \
	daqlib daqlib-dev daqlib-staticdev \
	dmapserver dmapserver-dev dmapserver-staticdev \
	kernel-module-daqiomc \
	listlayers \
	paramenv \
	pdnaserver \
	rmmodpdna \
	ueipallib-dev ueipallib-staticdev \
    ueiroot-rootfs \
	ueiuser-rootfs \
"

# 	dnxlogicupdate 

UEI_PACKAGES = " \
	libpdnaconfig libpdnaconfig-dev libpdnaconfig-staticdev \
	libshareddata libshareddata-dev libshareddata-staticdev \
	libueisimtarget libueisimtarget-dev libueisimtarget-staticdev \
	listlayers \
	ueidatalogger \
	ueiexamples \
	ueimodbus \
	ueivistas \
	ueitests \
	ueibootmisc \
"

UEI_FRAMEWORK_PACKAGES = " \
       libueidaq libueidaq-dev \
       ueicppexamples \
"

UEI_DEV_PACKAGES = " \
	ueitestrunner \
"

UEI_iDDS_PACKAGES = " \
	ueimds-rootfs \
	uei-idds-instr \
"

UEI_OPCUA_PACKAGES = " \
	ueiopcserver \
	ueiopcuasdk \
"

RDEPENDS:${PN} = " \
	${UEI_ESSENTIALS} \
	${UEI_PACKAGES} \
	${UEI_OPCUA_PACKAGES} \
	${UEI_FRAMEWORK_PACKAGES} \
"

RDEPENDS:${PN} += " \
	${@bb.utils.contains('UEI_BUILD_TYPE', 'devel', '${UEI_DEV_PACKAGES}', '', d)} \
 "
