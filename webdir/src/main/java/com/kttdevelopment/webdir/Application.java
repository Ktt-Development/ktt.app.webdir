package com.kttdevelopment.webdir;

import java.io.File;

public abstract class Application {

    public static final String parent = new File("").getAbsolutePath();

    //

    public static final Logger logger = new Logger();

    @SuppressWarnings("ConstantConditions")
    public static final ConfigService config = new ConfigService(
        new File(parent + '\\' + "config.yml"),
        new File(Application.class.getClassLoader().getResource("config/config.yml").getFile())
    );

    public static final Locale locale = new Locale("lang/bundle");

    @SuppressWarnings("ConstantConditions")
    public static final PermissionsService permissions = new PermissionsService(
        new File(parent + '\\' + "permissions.yml"),
        new File(Application.class.getClassLoader().getResource("permissions/permissions.yml").getFile())
    );

    public static final Server server = new Server();

    public static final PluginService apiLoader = new PluginService(new File(parent + '\\' + "plugins"));

    //

    public synchronized static void main(String[] args){
        server.getServer().start();
    }

}
