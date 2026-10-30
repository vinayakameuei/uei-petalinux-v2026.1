SUMMARY = "Lexical analyzer generator for Java"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=8e2372bdbf22c99279ae4599a13cc458"

DEPENDS_class-native += "classpath-native"

BBCLASSEXTEND = "native"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

#inherit java-library

SRC_URI = "\
	https://github.com/mozilla/rhino/releases/download/Rhino1_7R5_RELEASE/rhino1_7R5.zip \
	file://rhino \
	file://rhino-jsc \
	"

S = "${WORKDIR}/rhino1_7R5"

PACKAGES = "${JPN} rhino"

FILES_${PN} = "${bindir}/rhino ${bindir}/rhino-jsc"
RDEPENDS:${PN} = "java2-runtime ${JPN}"
RDEPENDS:${PN}_class-native = ""

do_compile() {
	mkdir -p build

	# Compatibility fix for jamvm which has non-genericised
	# java.lang classes. :(
	bcp_arg="-bootclasspath ${STAGING_DATADIR_NATIVE}/classpath/glibj.zip"

	javac $bcp_arg -source 1.6 -sourcepath src -d build `find src -name "*.java"`

	mkdir -p build/org/mozilla/javascript/resources
	cp src/org/mozilla/javascript/resources/*.properties build/org/mozilla/javascript/resources

	fastjar cfm ${JARFILENAME} ${S}/src/manifest -C build .
}

do_install:append() {
	install -d ${D}${bindir}

	install -m 0755 ${WORKDIR}/rhino ${D}${bindir}
	install -m 0755 ${WORKDIR}/rhino-jsc ${D}${bindir}
}

SRC_URI[md5sum] = "e6a5d95f6949dbaa4e97a94bdfb7e1eb"
SRC_URI[sha256sum] = "e3efc88af053fbc0c495e0f14f866ce8df06004561e4d93b575f5dc615550156"
