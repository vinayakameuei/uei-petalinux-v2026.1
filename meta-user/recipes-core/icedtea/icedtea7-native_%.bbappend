#AJT: need format overflow flag specifically for GCC 7, shipped natively with Ubuntu 18.04
CFLAGS:append = " -Wno-error=stringop-overflow -Wno-error=return-type -Wno-error=format-overflow"
