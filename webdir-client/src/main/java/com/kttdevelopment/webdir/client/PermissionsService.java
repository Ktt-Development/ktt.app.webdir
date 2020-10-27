package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.client.permissions.Permissions;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Logger;

public final class PermissionsService {

    public static final String
        groups      = "groups",
        def         = "default",
        inheritance = "inheritance",
        options     = "options",
        permissions = "permissions",
        users       = "users";

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

    PermissionsService(final File permissionsFile) throws IOException{
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        logger.info(locale.getString("permissions.constructor.start"));

        this.permissionsFile = Objects.requireNonNull(permissionsFile);

        // load default permissions
        final YamlMapping defaultPermissions;
        {
            logger.fine(locale.getString("permissions.constructor.default.start"));
            try{
                defaultPermissions = Yaml.createYamlInput(defaultYaml).readYamlMapping();
            }catch(final IOException e){
                logger.severe(locale.getString("permissions.constructor.default.fail") + LoggerService.getStackTraceAsString(e));
                throw e;
            }
            logger.fine(locale.getString("permissions.constructor.default.finish"));
        }

        // load permissions
        {
            final String fileName = permissionsFile.getName();
            logger.info(locale.getString("permissions.constructor.permissions.start", fileName));

            YamlMapping yaml = null;
            try{
                yaml = Yaml.createYamlInput(permissionsFile).readYamlMapping();
            }catch(final IOException e){
                logger.warning(locale.getString("permissions.constructor.permissions." + (e instanceof FileNotFoundException ? "missing" : "malformed")) + LoggerService.getStackTraceAsString(e));

                if(!permissionsFile.exists())
                    try{
                        Files.write(permissionsFile.toPath(), defaultYaml.getBytes(StandardCharsets.UTF_8));
                        logger.info(locale.getString("permissions.constructor.permissions.default.success", fileName));
                    }catch(final IOException | SecurityException e2){
                        logger.severe(locale.getString("permissions.constructor.permissions.default.fail", fileName) + LoggerService.getStackTraceAsString(e2));
                    }
            }

            permissionsSchema = new Permissions(yaml == null ? defaultPermissions : yaml);

            logger.info(locale.getString("permissions.constructor.permissions.finish", fileName));
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
            .addObject("default", defaultYaml)
            .addObject("permissions", permissions)
            .toString();
    }

}
