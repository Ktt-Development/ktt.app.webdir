package com.kttdevelopment.webdir.client.config;

import com.kttdevelopment.core.classes.ToStringBuilder;

public final class Setting<T> {

    private final String desc, yaml;
    
    private final String key;
    private final T defaultValue;

    public Setting(final String key, final T defaultValue, final String desc){
        this.key          = key;
        this.defaultValue = defaultValue;
        this.desc         = String.format("%s\nDefault: %s",desc,defaultValue);
        this.yaml         = String.format("%s\n%s: %s", desc.replaceAll("^(.*)$","# $1"),key,defaultValue);
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
