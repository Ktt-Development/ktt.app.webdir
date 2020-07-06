package com.kttdevelopment.webdir.sitegenerator.function;

public interface ExceptionConsumer<T> {

    T consume() throws Exception;

}
