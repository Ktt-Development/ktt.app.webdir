package com.kttdevelopment.webdir.main;

import java.util.*;
import java.util.Locale;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.logging.Logger;

abstract class _vars {

    public static final String root = "../";

    abstract static class config {

        public static final String resource = "/config";

        public static final String config = "config.yml";

        public static Map loadedConfig;

    }

    abstract static class logger {

        public static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        public static final Level  level  = Level.ALL;
        public static final Formatter formatter = new com.kttdevelopment.webdir.main.Logger.Formatter();

    }

    abstract static class locale {

        public static final String resource = "/lang";
        public static final Map<Locale, ResourceBundle> bundle = new HashMap<>();
        public static Locale loadedLocale;

    }

}
