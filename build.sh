#!/bin/bash
Version=1.0.0
Vendor="Ktt Development"
Workspace="package"
Dest="WebDir"

mkdir -p $Workspace

jpackage \
--name "WebDir" \
--icon icon.ico \
--input $Workspace \
--dest . \
--type app-image \
--main-jar webdir-client-$Version.jar \
--main-class com.kttdevelopment.webdir.client.Main \
--app-version $Version \
--vendor "$Vendor" \
--copyright "Copyright $Vendor 2020" \
--win-console

cp LICENSE $Dest/LICENSE