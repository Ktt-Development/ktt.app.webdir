package com.kttdevelopment.webdir.generator.function;

@SuppressWarnings("SpellCheckingInspection")
public interface QuadriFunction<X,Y,Z,A,R> {

    R apply(X x, Y y, Z z, A a);

}
