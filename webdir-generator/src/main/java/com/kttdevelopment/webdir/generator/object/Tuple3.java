package com.kttdevelopment.webdir.generator.object;

public class Tuple3<X,Y,Z> extends Tuple2<X,Y> {

    private final Z var3;

    public Tuple3(final X var1, final Y var2, final Z var3){
        super(var1,var2);
        this.var3 = var3;
    }

    public final Z getVar3(){
        return var3;
    }

    @Override
    public String toString(){
        return
            "Tuple3" + '{' +
            "var1"  + '=' + getVar1() + ", " +
            "var2"  + '=' + getVar2() + ", " +
            "var3"  + '=' + getVar3() +
            '}';
    }

}
