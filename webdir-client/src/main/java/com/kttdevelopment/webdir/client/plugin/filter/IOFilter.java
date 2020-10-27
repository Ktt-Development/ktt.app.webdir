package com.kttdevelopment.webdir.client.plugin.filter;

public interface IOFilter<I,O> {

    O filter(I in);

}