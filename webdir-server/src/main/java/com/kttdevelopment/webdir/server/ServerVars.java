package com.kttdevelopment.webdir.server;

public abstract class ServerVars {

    public static class Config {
        
        public static final String permissionsKey     = "permissions";
        public static final String defaultPermissions = "permissions.yml";

        public static final String filesContextKey     = "files_dir";
        public static final String defaultFilesContext = "files";

    }

    public static class Permissions {
        
        public static final String groupsKey        = "groups";
        public static final String inheritanceKey   = "inheritance";
        public static final String optionsKey       = "options";
        public static final String defaultKey       = "default";
        public static final String permissionsKey   = "permissions";

        public static final String usersKey = "users";

    }

    public static class Renderer {

        public static final String exchangeRendererKey = "exchangeRenderer";

    }

}
