package com.kttdevelopment.webdir.api.serviceprovider;

public interface LocaleBundle {

    String getString(final String key);

    String getString(final String key, final Object... args);

}
