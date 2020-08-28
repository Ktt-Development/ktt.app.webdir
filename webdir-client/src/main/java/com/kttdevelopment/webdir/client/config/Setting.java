package com.kttdevelopment.webdir.client.config;

public class Setting<T> {

    private final String yaml;

    private final String argName, flag, longFlag;

    private final String desc;
    private final T def;
    private final Class<?> type;

    public Setting(final String argName, final String flag, final String longFlag, final String desc, final T def){
        this.argName    = argName;
        this.flag       = flag;
        this.longFlag   = longFlag;
        this.desc       = String.format("%s\nDefault: %s",desc,def);
        this.def        = def;
        this.type       = def.getClass();
        this.yaml       = desc.replaceAll("^(.*)$", "# " + "$1");
    }

    public final String getArgName(){
        return argName;
    }

    public final String getFlag(){
        return flag;
    }

    public final String getLongFlag(){
        return longFlag;
    }

    public final String getDesc(){
        return desc;
    }

    public final String getYaml(){
        return yaml;
    }

    public final T getDefault(){
        return def;
    }

    public final Class<?> getType(){
        return type;
    }

}
