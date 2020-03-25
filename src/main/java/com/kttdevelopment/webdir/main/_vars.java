package com.kttdevelopment.webdir.main;

import java.util.*;
import java.util.Locale;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.logging.Logger;

abstract class _vars {

    abstract static class config {

    }

    abstract static class logger {

        public static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        public static final Level  level  = Level.ALL;
        public static final Formatter formatter = new com.kttdevelopment.webdir.main.Logger.Formatter();

    }

    abstract static class locale {

        public static final String resource = "/lang";
        public static final Map<String, ResourceBundle> bundle = new HashMap<>();
        public static Locale loadedLocale;

    }

}
