package com.kttdevelopment.webdir.generator.object;

public class Tuple5<X,Y,Z,R,S> extends Tuple4<X,Y,Z,R> {

    private final S var5;

    public Tuple5(final X var1, final Y var2, final Z var3, final R var4, final S var5){
        super(var1, var2, var3, var4);
        this.var5 = var5;
    }

    public final S getVar5(){
        return var5;
    }

    @Override
    public String toString(){
        return
            "Tuple" + '{' +
            "var1"  + '=' + getVar1() + ", " +
            "var2"  + '=' + getVar2() + ", " +
            "var3"  + '=' + getVar3() + ", " +
            "var4"  + '=' + getVar4() + ", " +
            "var5"  + '=' + getVar5() +
            '}';
    }

}
