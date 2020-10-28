package com.kttdevelopment.webdir.client.utility;

import com.amihaiemil.eoyaml.YamlNode;

public abstract class YamlUtility {

    public static String asString(final YamlNode e){
        return ExceptionUtility.requireNonExceptionElse(() -> e.asScalar().value(), null);
    }

}
