package com.kttdevelopment.webdir.sitegenerator.function;

public interface ExceptionSupplier<T> {

    T get() throws Exception;

}
