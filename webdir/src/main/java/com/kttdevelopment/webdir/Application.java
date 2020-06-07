package com.kttdevelopment.webdir;

import java.io.File;

public abstract class Application {

    public static final String parent = new File("").getAbsolutePath();

    //

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final LoggerService logger = new LoggerService();

    public static final LocaleService locale = new LocaleService("lang/bundle");

    public static final ConfigService config = new ConfigService(
        new File(parent + '\\' + "config.yml"),
        "config.yml"
    );

    @SuppressWarnings("ConstantConditions")
    public static final PermissionsService permissions = new PermissionsService(
        new File(parent + '\\' + "permissions.yml"),
        new File(Application.class.getClassLoader().getResource("permissions.yml").getFile())
    );

    public static final Server server = new Server();

    public static final PluginServiceLoader pluginService = new PluginServiceLoader(new File(parent + '\\' + "plugins"));


    //

    public synchronized static void main(String[] args){
        server.getServer().start();
    }

}
