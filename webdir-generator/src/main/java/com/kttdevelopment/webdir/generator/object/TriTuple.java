package com.kttdevelopment.webdir.generator.object;

public class TriTuple<X,Y,Z> extends BiTuple<X,Y> {

    private final Z var3;

    public TriTuple(final X var1, final Y var2, final Z var3){
        super(var1,var2);
        this.var3 = var3;
    }

    public final Z getVar3(){
        return var3;
    }

    @Override
    public String toString(){
        return
            "Tuple" + '{' +
            "var1"  + '=' + getVar1() + ", " +
            "var2"  + '=' + getVar2() + ", " +
            "var3"  + '=' + var3 +
            '}';
    }

}
