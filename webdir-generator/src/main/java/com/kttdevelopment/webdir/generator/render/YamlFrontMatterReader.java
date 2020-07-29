package com.kttdevelopment.webdir.generator.render;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YamlFrontMatterReader {

    private static final Pattern pattern = Pattern.compile("^(---)$\\n(.*)\\n^(---)$",Pattern.MULTILINE | Pattern.DOTALL);

    private final String content;

    public YamlFrontMatterReader(final String content){
        this.content = content.replace("\r",""); // Carriage return causes read issues; new line doesn't even need carriage return
    }

    @SuppressWarnings("rawtypes")
    public final YamlFrontMatter read(){
        final Matcher matcher = pattern.matcher(content);

        boolean hasFrontMatter = false;
        ConfigurationSection frontMatter = null;
        String frontMatterStr = null;
        String cont = content;

        if(matcher.find()){
            final String g2 = matcher.group(2);
            final int len = matcher.group(0).length();
            final String ct = content.length() == len ? "" : content.substring(len + 1);

            final YamlReader IN = new YamlReader(g2);
            try{
                frontMatter = new ConfigurationSectionImpl((Map) IN.read());
                hasFrontMatter = true;
                frontMatterStr = g2;
                cont = ct;
            }catch(final ClassCastException | YamlException ignored){
                // invalid yaml
            }finally{
                try{ IN.close();
                }catch(final IOException ignored){ }
            }
        }

        final boolean hfm = hasFrontMatter;
        final ConfigurationSection fm = frontMatter;
        final String fms = frontMatterStr;
        final String ct = cont;

        return new YamlFrontMatter() {

            final boolean hasFrontMatter = hfm;
            final ConfigurationSection frontMatter = fm;
            final String frontMatterString = fms;
            final String content = ct;

            @Override
            public final boolean hasFrontMatter(){
                return hasFrontMatter;
            }

            @Override
            public final ConfigurationSection getFrontMatter(){
                return frontMatter;
            }

            @Override
            public final String getFrontMatterAsString(){
                return frontMatterString;
            }

            @Override
            public final String getContent(){
                return content;
            }
        };
    }

    //


    @Override
    public String toString(){
        return new toStringBuilder("YamLFrontMatterReader")
            .addObject("frontMatterRegex",pattern.pattern())
            .addObject("content",content)
            .toString();
    }

}
