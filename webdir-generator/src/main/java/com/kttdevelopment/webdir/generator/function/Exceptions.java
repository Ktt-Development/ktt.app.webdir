package com.kttdevelopment.webdir.generator.function;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Exceptions {

    private static final String stackTrace = "--- Begin Stack Trace ---\n%s--- End Stack Trace ---";

    public static String getStackTraceAsString(final Throwable e){
        final StringWriter err = new StringWriter();
        e.printStackTrace(new PrintWriter(err));
        return String.format(stackTrace,err.toString());
    }

    public static void runIgnoreException(final ExceptionConsumer consumer){
        try{ consumer.run();
        }catch(final Throwable ignored){ }
    }

    public static <T> T requireNonExceptionElse(final ExceptionSupplier<T> supplier, T def){
        try{ return supplier.get();
        }catch(final Throwable ignored){ return def; }
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public static <T extends Throwable> RuntimeException throwUnchecked(Throwable throwable) throws T {
        throw (T) throwable;
    }

}
