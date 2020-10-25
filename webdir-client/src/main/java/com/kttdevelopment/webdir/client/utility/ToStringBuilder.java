package com.kttdevelopment.webdir.client.utility;

import java.util.*;

public final class ToStringBuilder {

    private final String className;
    private final Map<String,Object> map = new LinkedHashMap<>();

    public ToStringBuilder(final String className){
        this.className = className;
    }

    public ToStringBuilder(final String className, final Map<String,Object> map){
        this(className);
        map.forEach(this.map::put);
    }

    public final ToStringBuilder addObject(final String key, final Object value){
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
            return '\'' + object.toString() + '\'';
        else if(object instanceof Object[])
            return Arrays.toString((Object[]) object);
        else
            return object.toString();
    }

}
