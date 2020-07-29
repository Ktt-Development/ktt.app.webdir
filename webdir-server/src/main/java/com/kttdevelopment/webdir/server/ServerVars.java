package com.kttdevelopment.webdir.server;

public abstract class ServerVars {

    public static class Config {

        public static final String permissionsKey     = "permissions";
        public static final String defaultPermissions = "permissions.yml";

    }

    public static class Permissions {

        public static final String usersKey = "users";

        public static final String groupsKey        = "groups";
        public static final String optionsKey       = "options";
        public static final String permissionsKey   = "permissions";

        public static final String inheritanceKey   = "inheritance";

    }

}
