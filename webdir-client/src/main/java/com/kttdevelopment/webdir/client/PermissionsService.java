package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.permissions.Permissions;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public class PermissionsService {

    private final File permissionsFile;
    private final Permissions permissions;

    public PermissionsService(final File permissionsFile){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger        = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        this.permissionsFile = Objects.requireNonNull(permissionsFile);


    }

}
