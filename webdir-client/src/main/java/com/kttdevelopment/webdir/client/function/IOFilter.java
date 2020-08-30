package com.kttdevelopment.webdir.client.function;

public interface IOFilter<I,O> {

    O filter(I in);

}
