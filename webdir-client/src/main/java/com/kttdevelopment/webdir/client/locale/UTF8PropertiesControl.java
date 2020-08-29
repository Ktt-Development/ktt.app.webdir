package com.kttdevelopment.webdir.client.locale;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

// this class forces resource files to be read with UTF-8 encoding
public final class UTF8PropertiesControl extends ResourceBundle.Control {

    @Override
    public final ResourceBundle newBundle(final String baseName, final Locale locale, final String format, final ClassLoader loader, final boolean reload) throws IOException{
        final String resourceName = toResourceName(toBundleName(baseName,locale),"properties");

        InputStream IN = null;

        final URL url = loader.getResource(resourceName);
        if(url != null){
            final URLConnection conn = url.openConnection();
            if(conn != null){
                conn.setUseCaches(false);
                IN = conn.getInputStream();
            }
        }else{
            IN = loader.getResourceAsStream(resourceName);
        }

        ResourceBundle bundle = null;
        if(IN != null){
            try{
                bundle = new PropertyResourceBundle(new InputStreamReader(IN, StandardCharsets.UTF_8));
            }finally{
                IN.close();
            }
        }
        return bundle;
    }

}
