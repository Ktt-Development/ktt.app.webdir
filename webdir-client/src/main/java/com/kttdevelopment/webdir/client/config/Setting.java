package com.kttdevelopment.webdir.client.config;

import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.util.regex.Pattern;

public final class Setting {

    private final String key, def, desc, yaml;

    @SuppressWarnings("FieldCanBeLocal")
    private final Pattern pattern = Pattern.compile("^(.*)$", Pattern.MULTILINE); // line

    public Setting(final String key, final String defaultValue, final String desc){
        this.key  = key;
        this.def  = defaultValue;
        this.desc = String.format("%s\nDefault: %s", desc, defaultValue);
        this.yaml = String.format("%s\n%s: %s", pattern.matcher(this.desc).replaceAll("# $1"), key, defaultValue);
    }

    public final String getKey(){
        return key;
    }

    public final String getDefaultValue(){
        return def;
    }

    public final String getDesc(){
        return desc;
    }

    public final String getYaml(){
        return yaml;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("key", key)
            .addObject("defaultValue", def)
            .addObject("desc", desc)
            .addObject("yaml", yaml)
            .toString();
    }

}
