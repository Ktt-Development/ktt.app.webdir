package com.kttdevelopment.webdir.api.handler;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;

import java.io.File;
import java.util.function.BiPredicate;

public abstract class HandlerEntry {

    public abstract SimpleFileHandler getHandler();

    public abstract BiPredicate<SimpleHttpExchange,File> getCondition();

    public abstract String getPermission();

}
