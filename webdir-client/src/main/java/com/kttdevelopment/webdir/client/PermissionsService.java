package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.permissions.Permissions;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class PermissionsService {

    private final File permissionsFile;
    // private final Permissions permissions;

    private final Permissions defaultPermissions = new Permissions(Map.of(
        "groups", Map.of(
            "default", Map.of(
                "options", Map.of(
                    "default",true,
                    "connection-limit",0
                )
            ),
            "admin", Map.of(
                "inheritance",List.of("default"),
                "options",Map.of(
                    "connection-limit",1
                ),
                "permissions",List.of("*")
            )
        ),
        "users",Map.of(
            "127.0.0.1",Map.of(
                "group",List.of("admin"),
                "options",Map.of(
                    "connection-limit",-1
                ),
                "permissions",List.of("*")
            )
        )
    ));

    public PermissionsService(final File permissionsFile){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger        = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        this.permissionsFile = Objects.requireNonNull(permissionsFile);

        // load default

        // read perms
    }

}
