package com.kttdevelopment.webdir.generator.object;

import com.kttdevelopment.webdir.generator.function.toStringBuilder;

public class Tuple4<X,Y,Z,A> extends Tuple3<X,Y,Z> {

    private final A var4;

    public Tuple4(final X var1, final Y var2, final Z var3, final A var4){
        super(var1, var2, var3);
        this.var4 = var4;
    }

    public final A getVar4(){
        return var4;
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof Tuple4))
            return false;
        final Tuple4<?,?,?,?> other = (Tuple4<?,?,?,?>) o;
        return other.getVar1().equals(getVar1()) &&
               other.getVar2().equals(getVar2()) &&
               other.getVar3().equals(getVar3()) &&
               other.getVar4().equals(getVar4());
    }

    @Override
    public String toString(){
        return new toStringBuilder("Tuple4")
            .addObject("var1",getVar1())
            .addObject("var2",getVar2())
            .addObject("var3",getVar3())
            .addObject("var4",getVar4())
            .toString();
    }

}
