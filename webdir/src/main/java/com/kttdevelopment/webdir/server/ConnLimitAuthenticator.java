package com.kttdevelopment.webdir.server;

import com.kttdevelopment.webdir.Application;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetAddress;

public class ConnLimitAuthenticator extends Authenticator {

    @Override
    public Result authenticate(final HttpExchange exch){
        final InetAddress addr = exch.getRemoteAddress().getAddress();


        return null;
    }

}
