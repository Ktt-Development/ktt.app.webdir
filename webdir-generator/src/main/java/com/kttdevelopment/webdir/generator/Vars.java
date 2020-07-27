package com.kttdevelopment.webdir.generator;

import java.io.File;
import java.util.concurrent.TimeUnit;

public abstract class Vars {

    @SuppressWarnings("SpellCheckingInspection")
    public static final class Test {

        public static boolean safemode = false;
        public static boolean clear = false;
        public static boolean server = false;

        public static int testPort = 8080;

    }

    public static final class Main {

        public static final String localeResource = "lang/bundle";

        public static final File configFile = new File("config.yml");
        public static final String configResource = "/config.yml";

    }

    public static final class Config {
        // config defaults should match config file

        public static final String sourcesKey       = "source_dir";
        public static final String defaultSource    = ".root";
        public static final String outputKey        = "output_dir";
        public static final String defaultOutput    = "_site";

        public static final String defaultsKey      = "default_dir";
        public static final String defaultsDir      = ".default";

        public static final String pluginsKey       = "plugins_dir";
        public static final String defaultPlugins   = ".plugins";

        public static final String cleanKey     = "clean";

        public static final String serverKey        = "preview";
        public static final boolean defaultServer   = false;
        public static final String portKey          = "port";
        public static final int defaultPort         = 80;

    }

    public static final class Plugin {

        public static final String pluginYml        = "plugin.yml";
        public static final String mainClassKey     = "main";

        public static final String nameKey          = "name";
        public static final String versionKey       = "version";
        public static final String authorsKey       = "authors";
        public static final String dependenciesKey  = "dependencies";

        public static final int loadTimeout = 30;
        public static TimeUnit loadTimeoutUnit = TimeUnit.SECONDS;

    }

    public static final class Renderer {

        public static final String importKey            = "import";
        public static final String importRelativeKey    = "import_relative";

        public static final String pluginKey    = "plugin";
        public static final String rendererKey  = "renderer";

        public static final class Default {

            public static final String defaultKey   = "default";
            public static final String indexKey     = "index";
            public static final String scopeKey     = "scope";

            public static final int defaultIndex    = 0;

        }

    }

}
