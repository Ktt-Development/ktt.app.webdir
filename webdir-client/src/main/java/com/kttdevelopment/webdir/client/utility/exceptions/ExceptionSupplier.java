package com.kttdevelopment.webdir.client.utility.exceptions;

public interface ExceptionSupplier<T> {

    T get() throws Throwable;

}