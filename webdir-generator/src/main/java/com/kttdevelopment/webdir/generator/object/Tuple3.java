package com.kttdevelopment.webdir.generator.object;

import com.kttdevelopment.webdir.generator.function.toStringBuilder;

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
        return new toStringBuilder("Tuple3")
            .addObject("var1",getVar1())
            .addObject("var2",getVar2())
            .addObject("var3",getVar3())
            .toString();
    }

}
