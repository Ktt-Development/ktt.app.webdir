package com.kttdevelopment.webdir.client.renderer.yaml;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.core.tests.exceptions.ExceptionUtil;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.config.ConfigurationSectionImpl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YamlFrontMatterReader {

    private static final Pattern pattern = Pattern.compile("^(---)$\\n?(.*)\\n^(---)$",Pattern.MULTILINE | Pattern.DOTALL);

    private final String content;

    public YamlFrontMatterReader(final String content){
        this.content = content.replace("\r",""); // Carriage return causes read issues; new line doesn't even need carriage return
    }

    @SuppressWarnings("rawtypes")
    public final YamlFrontMatter read(){
        final Matcher matcher = pattern.matcher(content);

        boolean hasFrontMatter           = false;
        ConfigurationSection frontMatter = null;
        String frontMatterStr            = null;
        String cont                      = content;

        if(matcher.find()){
            final String g2 = matcher.group(2);
            final int len   = matcher.group(0).length();
            final String ct = content.length() == len ? "" : content.substring(len + 1);

            final YamlReader IN = new YamlReader(g2);
            try{
                frontMatter    = new ConfigurationSectionImpl(Objects.requireNonNullElse((Map) IN.read(), new HashMap()));
                hasFrontMatter = true;
                frontMatterStr = g2;
                cont           = ct;
            }catch(final ClassCastException | YamlException ignored){
                // invalid yaml
            }finally{
                ExceptionUtil.runIgnoreException( IN::close);
            }
        }

        final boolean hfm             = hasFrontMatter;
        final ConfigurationSection fm = frontMatter;
        final String fms              = frontMatterStr;
        final String ct               = cont;

        return new YamlFrontMatter() {

            @Override
            public final boolean hasFrontMatter(){
                return hfm;
            }

            @Override
            public final ConfigurationSection getFrontMatter(){
                return fm;
            }

            @Override
            public final String getFrontMatterAsString(){
                return fms;
            }

            @Override
            public final String getContent(){
                return ct;
            }

        };
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("frontMatterRegex",pattern.pattern())
            .addObject("content",content)
            .toString();
    }

}
