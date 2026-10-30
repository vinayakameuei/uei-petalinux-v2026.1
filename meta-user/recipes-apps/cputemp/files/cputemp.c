/*
* Copyright (C) 2001-2022  United Electronic Industries (UEI), Inc.  All rights reserved.
*
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without restriction,
* including without limitation the rights to use, copy, modify, merge,
* publish, distribute, sublicense, and/or sell copies of the Software,
* and to permit persons to whom the Software is furnished to do so,
* subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included
* in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL UEI BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
* CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*
* Except as contained in this notice, the name of the UEI shall not be used
* in advertising or otherwise to promote the sale, use or other dealings in this
* Software without prior written authorization from UEI, Inc.
*
*/

#include <stdio.h>

int main(int argc, char **argv)
{
    FILE* fin;
    char raw[32], scale[32], offset[32];
    float rawf, scalef, offsetf, cpu_tempf;


    fin = fopen("/sys/bus/iio/devices/iio:device0/in_temp0_ps_temp_raw", "r");
    if (NULL == fin) {
        printf("couldn't open in_temp0_ps_temp_raw\n");
        return 1;
    }
    fscanf(fin, "%f", &rawf);
    fclose(fin);

    fin = fopen("/sys/bus/iio/devices/iio:device0/in_temp0_ps_temp_scale", "r");
    if (NULL == fin) {
        printf("couldn't open in_temp0_ps_temp_scale\n");
        return 1;
    }
    fscanf(fin, "%f", &scalef);
    fclose(fin);

    fin = fopen("/sys/bus/iio/devices/iio:device0/in_temp0_ps_temp_offset", "r");
    if (NULL == fin) {
        printf("couldn't open in_temp0_ps_temp_offset\n");
        return 1;
    }
    fscanf(fin, "%f", &offsetf);
    fclose(fin);

    
    cpu_tempf = scalef*(rawf + offsetf) / 1000.0;

    printf("CPU temp=%6.3f\n", cpu_tempf);

    return 0;
}
