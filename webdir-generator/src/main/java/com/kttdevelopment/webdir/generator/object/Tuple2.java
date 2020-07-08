package com.kttdevelopment.webdir.generator.object;

public class Tuple2<X,Y> {

    private final X var1;
    private final Y var2;

    public Tuple2(final X var1, final Y var2){
        this.var1 = var1;
        this.var2 = var2;
    }

    public final X getVar1(){
        return var1;
    }

    public final Y getVar2(){
        return var2;
    }

    @Override
    public String toString(){
        return
            "Tuple" + '{' +
            "var1"  + '=' + getVar1() + ", " +
            "var2"  + '=' + getVar2() + ", " +
            '}';
    }

}
