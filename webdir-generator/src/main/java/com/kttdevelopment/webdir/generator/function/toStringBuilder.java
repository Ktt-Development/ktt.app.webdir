package com.kttdevelopment.webdir.generator.function;

import java.util.*;

public final class toStringBuilder {

    private final String className;
    private final Map<String,Object> map = new LinkedHashMap<>();

    public toStringBuilder(final String className){
        this.className = className;
    }

    public toStringBuilder(final String className, final Map<String,Object> map){
        this(className);
        this.map.putAll(map);
    }

    public toStringBuilder addObject(final String key, final Object value){
        map.put(key,value);
        return this;
    }

    //

    @Override
    public final String toString(){
        final StringBuilder OUT = new StringBuilder();
        OUT.append(className).append('{');
        map.forEach((s, o) -> OUT.append(s).append('=').append(asString(o)).append(", "));
        if(OUT.toString().endsWith(", "))
            OUT.delete(OUT.length()-2,OUT.length());
        OUT.append('}');
        return OUT.toString();
    }

    private String asString(final Object object){
        if(object == null)
            return null;
        else if(object instanceof String)
            return '"' + object.toString() + '"';
        else if(object instanceof Object[])
            return Arrays.toString((Object[]) object);
        else
            return object.toString();
    }

}
