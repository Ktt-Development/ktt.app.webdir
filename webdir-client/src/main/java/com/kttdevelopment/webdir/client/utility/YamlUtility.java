package com.kttdevelopment.webdir.client.utility;

import com.amihaiemil.eoyaml.*;

import java.util.*;

public abstract class YamlUtility {

    public static String asString(final YamlNode e){
        return ExceptionUtility.requireNonExceptionElse(() -> e.asScalar().value(), ExceptionUtility.requireNonExceptionElse(e::toString, null));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List asList(final YamlSequence e){
        final List list = new ArrayList();
        for(final YamlNode yamlNode : e)
            switch(yamlNode.type()){
                case SCALAR:
                    list.add(asString(yamlNode));
                    break;
                case SEQUENCE:
                    list.add(asList((YamlSequence) yamlNode));
                    break;
                case MAPPING:
                    list.add(asMap((YamlMapping) yamlNode));
                    break;
            }
        return list;
    }

    public static Map<String,? super Object> asMap(final YamlMapping e){
        final Map<String,? super Object> map = new HashMap<>();
        for(final YamlNode key : e.keys()){
            switch(e.value(key).type()){
                case SCALAR:
                    map.put(asString(key), e.string(key));
                    break;
                case SEQUENCE:
                    map.put(asString(key), asList(e.yamlSequence(key)));
                    break;
                case MAPPING:
                    map.put(asString(key), asMap(e.yamlMapping(key)));
                    break;
            }
        }
        return map;
    }

    public static boolean containsKey(final String key, final YamlMapping e){
        if(e == null) return false;
        for(final YamlNode yamlNode : e.keys())
            if(key.equals(asString(yamlNode)))
                return true;
        return false;
    }


}
