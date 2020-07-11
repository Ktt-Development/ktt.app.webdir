package com.kttdevelopment.webdir.generator.object;

public class Tuple4<X,Y,Z,A> extends Tuple3<X,Y,Z> {

    private final A var4;

    public Tuple4(final X var1, final Y var2, final Z var3, final A var4){
        super(var1, var2, var3);
        this.var4 = var4;
    }

    public final A getVar4(){
        return var4;
    }

    @Override
    public String toString(){
        return
            "Tuple3" + '{' +
            "var1"  + '=' + getVar1() + ", " +
            "var2"  + '=' + getVar2() + ", " +
            "var3"  + '=' + getVar3() + ", " +
            "var4"  + '=' + getVar4() +
            '}';
    }

}
