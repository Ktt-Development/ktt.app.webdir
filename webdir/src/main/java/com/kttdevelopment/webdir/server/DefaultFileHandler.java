package com.kttdevelopment.webdir.server;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.simplehttpserver.handler.FileHandler;
import com.kttdevelopment.webdir.api.formatter.YamlFrontMatter;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.formatter.YamlFrontMatterReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultFileHandler extends FileHandler {

    @Override
    public void handle(final SimpleHttpExchange exchange, final File source, final byte[] bytes) throws IOException{
        final String str = new String(bytes);

        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(frontMatter.hasFrontMatter()){
            final ConfigurationSection config = frontMatter.getFrontMatter();

            final List<String> imports = config.getList("import",String.class); // import relative file 
            final List<String> formatter = config.getList("formatter",String.class);

            // handle formatters & send finished content w/o front matter
        }else{
            // send literal
            super.handle(exchange, source, bytes);
        }
    }

}
