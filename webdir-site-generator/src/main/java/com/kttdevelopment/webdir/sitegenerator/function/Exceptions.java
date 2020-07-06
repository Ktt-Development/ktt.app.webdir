package com.kttdevelopment.webdir.sitegenerator.function;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Exceptions {

    public static String getStackTraceAsString(final Throwable e){
        final StringWriter err = new StringWriter();
        e.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

    public static <T> T requireNonExceptionElse(final ExceptionSupplier<T> consumer, T def){
        try{
            return consumer.get();
        }catch(Exception e){
            return def;
        }
    }

}
