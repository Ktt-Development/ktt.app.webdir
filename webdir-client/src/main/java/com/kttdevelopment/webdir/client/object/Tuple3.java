package com.kttdevelopment.webdir.client.object;

import com.kttdevelopment.core.classes.ToStringBuilder;

public class Tuple3<X,Y,Z> extends Tuple2<X,Y> {

    private final Z var3;

    public Tuple3(final X var1, final Y var2, final Z var3){
        super(var1,var2);
        this.var3 = var3;
    }

    public final Z getVar3(){
        return var3;
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof Tuple3))
            return false;
        final Tuple3<?,?,?> other = (Tuple3<?,?,?>) o;
        return other.getVar1().equals(getVar1()) &&
               other.getVar2().equals(getVar2()) &&
               other.getVar3().equals(getVar3());
    }

    @Override
    public String toString(){
        return new ToStringBuilder("Tuple3")
            .addObject("var1",getVar1())
            .addObject("var2",getVar2())
            .addObject("var3",getVar3())
            .toString();
    }

}
