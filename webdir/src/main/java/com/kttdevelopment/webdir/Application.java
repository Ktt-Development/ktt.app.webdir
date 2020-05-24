package com.kttdevelopment.webdir;

import java.io.File;

public abstract class Application {

    public static final String parent = new File("").getAbsolutePath();

    //

    public static final Logger logger = new Logger();

    @SuppressWarnings("ConstantConditions")
    public static final Config config = new Config(
        new File(parent + '\\' + "config.yml"),
        new File(Application.class.getClassLoader().getResource("config/config.yml").getFile())
    );

    public static final Locale locale = new Locale("lang/bundle");

    @SuppressWarnings("ConstantConditions")
    public static final Permissions permissions = new Permissions(
        new File(parent + '\\' + "permissions.yml"),
        new File(Application.class.getClassLoader().getResource("permissions/permissions.yml").getFile())
    );

    public static final ApiLoader apiLoader = new ApiLoader(new File(parent + '\\' + "plugins"));

    public static final Server server = new Server();

    //

    public synchronized static void main(String[] args){

    }

}
