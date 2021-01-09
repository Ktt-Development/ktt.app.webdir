/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.client;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.client.permissions.Permissions;
import com.kttdevelopment.webdir.client.utility.MapUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public final class PermissionsService {

    public static final String
        GROUPS      = "groups",
        DEF         = "default",
        INHERITANCE = "inheritance",
        OPTIONS     = "options",
        PERMISSIONS = "permissions",
        USERS       = "users",
        CONNECTIONS = "connection-limit";

    private static final String defaultYaml =
        "groups:\n" +
        "  default:\n" +
        "    options:\n" +
        "      default: true\n" +
        "      connection-limit: 0\n" +
        "  admin:\n" +
        "    options:\n" +
        "      connection-limit: 1\n" +
        "    permissions:\n" +
        "      - '*'\n" +
        "users:\n" +
        "  127.0.0.1:\n" +
        "    groups:\n" +
        "      - admin\n" +
        "    options:\n" +
        "      connection-limit: -1\n" +
        "    permissions:\n" +
        "      - '*'";

    private final Permissions permissionsSchema;
    private final File permissionsFile;

    PermissionsService(final File permissionsFile) throws YamlException{
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        logger.info(locale.getString("permissions.constructor.start"));

        this.permissionsFile = Objects.requireNonNull(permissionsFile);

        // load default permissions
        final Map<String,Object> defaultPermissions;
        {
            logger.fine(locale.getString("permissions.constructor.default.start"));
            try{
                defaultPermissions = MapUtility.asStringObjectMap((Map<?,?>) new YamlReader(defaultYaml).read());
            }catch(final ClassCastException | YamlException e){
                logger.severe(locale.getString("permissions.constructor.default.fail") + LoggerService.getStackTraceAsString(e));
                throw e;
            }
            logger.fine(locale.getString("permissions.constructor.default.finish"));
        }

        // load permissions
        {
            final String fileName = permissionsFile.getPath();
            logger.info(locale.getString("permissions.constructor.permissions.start", fileName));

            Map<String,Object> yaml = null;
            try(final FileReader IN = new FileReader(permissionsFile)){
                yaml = MapUtility.asStringObjectMap((Map<?,?>) new YamlReader(IN).read());
            }catch(final ClassCastException | IOException e){
                logger.warning(locale.getString("permissions.constructor.permissions." + (e instanceof FileNotFoundException ? "missing" : "malformed"), fileName) + LoggerService.getStackTraceAsString(e));

                if(!permissionsFile.exists())
                    try{
                        Files.write(permissionsFile.toPath(), defaultYaml.getBytes(StandardCharsets.UTF_8));
                        logger.info(locale.getString("permissions.constructor.permissions.default.success", fileName));
                    }catch(final IOException | SecurityException e2){
                        logger.severe(locale.getString("permissions.constructor.permissions.default.fail", fileName) + LoggerService.getStackTraceAsString(e2));
                    }
            }

            permissionsSchema = new Permissions(yaml == null ? defaultPermissions : yaml);

            logger.info(locale.getString("permissions.constructor.permissions.finish"));
        }
        logger.info(locale.getString("permissions.constructor.finish"));
    }

    public static String getDefaultYaml(){
        return defaultYaml;
    }

    public final Permissions getPermissions(){
        return permissionsSchema;
    }

    public final File getPermissionsFile(){
        return permissionsFile;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("permissionsFile", permissionsFile)
            .addObject("permissions", permissionsSchema)
            .toString();
    }

}
