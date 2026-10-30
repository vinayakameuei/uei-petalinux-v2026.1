#!/bin/sh

oscap xccdf eval --results results_uei.xml --report report_uei.html --profile xccdf_org.ssgproject.content_profile_uei /usr/share/xml/scap/ssg/content/ssg-openembedded-ds.xml
