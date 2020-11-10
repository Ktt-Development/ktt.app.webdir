#!/bin/bash
Version=1.0.0
Vendor="Ktt Development"
Workspace="package"
Dest="WebDir"

mkdir -p $Workspace

cp modules/webdir-client-$Version.jar $Workspace/webdir-client-$Version.jar

jpackage \
--name "WebDir" \
--icon icon.ico \
--input $Workspace \
--dest . \
--type app-image \
--module-path "modules" \
--module webdir.client \
--app-version $Version \
--vendor "$Vendor" \
--copyright "Copyright $Vendor 2020" \
--win-console

cp LICENSE $Dest/LICENSE