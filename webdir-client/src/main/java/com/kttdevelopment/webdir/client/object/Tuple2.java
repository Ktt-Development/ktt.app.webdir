package com.kttdevelopment.webdir.client.object;

import com.kttdevelopment.core.classes.ToStringBuilder;

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

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof Tuple2))
            return false;
        final Tuple2<?,?> other = (Tuple2<?,?>) o;
        return other.getVar1().equals(getVar1()) &&
               other.getVar2().equals(getVar2());
    }

    @Override
    public String toString(){
        return new ToStringBuilder("Tuple2")
            .addObject("var1",getVar1())
            .addObject("var2",getVar2())
            .toString();
    }

}
