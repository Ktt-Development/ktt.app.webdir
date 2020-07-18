package com.kttdevelopment.webdir.generator.function;

public interface ExceptionSupplier<T> {

    T get() throws Throwable;

}
