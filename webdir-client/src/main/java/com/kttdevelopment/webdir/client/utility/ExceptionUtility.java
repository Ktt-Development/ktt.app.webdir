package com.kttdevelopment.webdir.client.utility;

import com.kttdevelopment.webdir.client.utility.exceptions.ExceptionRunnable;
import com.kttdevelopment.webdir.client.utility.exceptions.ExceptionSupplier;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class ExceptionUtility {

    public static String getStackTraceAsString(final Throwable throwable){
        final StringWriter err = new StringWriter();
        throwable.printStackTrace(new PrintWriter(err));
        return err.toString();
    }

    public static void runIgnoreException(final ExceptionRunnable runnable){
        try{ runnable.run();
        }catch(final Throwable ignored){ }
    }

    public static <T> T requireNonExceptionElse(final ExceptionSupplier<T> supplier, final T defaultObj){
        try{ return supplier.get();
        }catch(final Throwable ignored){ return defaultObj; }
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public static <T extends Throwable> RuntimeException throwUnchecked(final Throwable throwable) throws T {
        throw (T) throwable;
    }

}
