package com.kttdevelopment.webdir.client.config;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.regex.Pattern;

public final class Setting<T> {

    private final String desc, yaml;
    
    private final String key;
    private final T defaultValue;

    final Pattern pattern = Pattern.compile("^(.*)$", Pattern.MULTILINE);

    public Setting(final String key, final T defaultValue, final String desc){
        this.key          = key;
        this.defaultValue = defaultValue;
        this.desc         = String.format("%s\nDefault: %s",desc,defaultValue);
        this.yaml         = String.format("%s\n%s: %s",pattern.matcher(this.desc).replaceAll("# $1"),key,defaultValue);
    }

    public final String getDesc(){
        return desc;
    }

    public final String getYaml(){
        return yaml;
    }

    public final String getKey(){
        return key;
    }

    public final T getDefaultValue(){
        return defaultValue;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("<type>",defaultValue.getClass().getSimpleName())
            .addObject("key",key)
            .addObject("defaultValue",defaultValue)
            .addObject("desc",desc)
            .addObject("yaml",yaml)
            .toString();
    }

}
