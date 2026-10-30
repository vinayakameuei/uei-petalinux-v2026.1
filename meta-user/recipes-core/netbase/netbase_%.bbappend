do_install:append () {
	echo "pdnaserver      6334/udp                        # pdnaserver over UDP" \
            >> ${D}${sysconfdir}/services
    echo "pdnaservera      6344/udp                        # pdnaserver async" \
            >> ${D}${sysconfdir}/services
	echo "pdnaservers     6354/udp                        # secure pdnaserver over UDP/DTLS" \
            >> ${D}${sysconfdir}/services
}
