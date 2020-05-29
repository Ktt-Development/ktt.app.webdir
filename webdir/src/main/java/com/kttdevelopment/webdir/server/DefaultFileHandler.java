package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class DefaultFileHandler extends FileHandler {

    private static final Pattern fm = Pattern.compile("^(---)(.*)(---)$",Pattern.DOTALL | Pattern.MULTILINE);



    @Override
    public void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);

        if(fm.matcher(str).matches()){

            // use formatter
        }else{
            // send literal
            super.handle(exchange, source, bytes);
        }
    }

}
