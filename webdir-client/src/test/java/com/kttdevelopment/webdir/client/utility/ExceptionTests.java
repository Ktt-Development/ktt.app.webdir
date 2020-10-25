package com.kttdevelopment.webdir.client.utility;

import com.kttdevelopment.webdir.client.utility.exceptions.ExceptionRunnable;
import com.kttdevelopment.webdir.client.utility.exceptions.ExceptionSupplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ExceptionTests {

    @Test
    public final void testStackTraceAsString(){
        final Throwable exception = new NullPointerException();
        final String asString = ExceptionUtility.getStackTraceAsString(exception);
        Assertions.assertTrue(asString.contains(exception.getClass().getSimpleName()));
    }

    //

    @SuppressWarnings("Convert2Lambda")
    @Test
    public final void runIgnoreException(){
        ExceptionUtility.runIgnoreException(new ExceptionRunnable() {
            @Override
            public final void run(){
                throw new RuntimeException();
            }
        });
    }

    @Test
    public final void runIgnoreExceptionLambda(){
        ExceptionUtility.runIgnoreException(() -> {
            throw new RuntimeException();
        });
    }

    @Test
    public final void runIgnoreExceptionMethodReference(){
        ExceptionUtility.runIgnoreException(this::throwException);
    }

    private void throwException() throws IOException{
        throw new IOException();
    }

    //

    @SuppressWarnings("Convert2Lambda")
    @Test
    public final void runRequireNonExceptionElse(){
        Assertions.assertTrue(
            ExceptionUtility.requireNonExceptionElse(
                new ExceptionSupplier<>() {
                    @Override
                    public final Boolean get(){
                        throw new NullPointerException();
                    }
                },
                true
            )
        );
    }

    @Test
    public final void runRequireNonExceptionElseLambda(){
        Assertions.assertTrue(
            ExceptionUtility.requireNonExceptionElse(
                () -> {
                    throw new NullPointerException();
                },
                true
            )
        );
    }

    @Test
    public final void runRequireNonExceptionElseMethodReference(){
        Assertions.assertTrue(
            ExceptionUtility.requireNonExceptionElse(
                this::throwExceptionSupplier,
                true
            )
        );
    }

    private boolean throwExceptionSupplier() throws IOException{
        throw new IOException();
    }

    //

    @Test
    public final void runThrowUnchecked(){
        Assertions.assertThrows(IOException.class, () -> ExceptionUtility.throwUnchecked(new IOException()));
    }

}
