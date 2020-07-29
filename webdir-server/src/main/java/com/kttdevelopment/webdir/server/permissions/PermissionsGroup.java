package com.kttdevelopment.webdir.server.permissions;

import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.object.Tuple4;
import com.kttdevelopment.webdir.server.ServerVars;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public final class PermissionsGroup extends Tuple4<String,String[],Map,String[]> {


    @SuppressWarnings("unchecked")
    public PermissionsGroup(final String group, final Map value){
        super(
            group,
            ((Supplier<String[]>) () -> {
                try{
                    final Object inheritance = Objects.requireNonNull(value.get(ServerVars.Permissions.inheritanceKey));
                    if(inheritance instanceof List)
                        return Arrays.copyOf(((List<?>) inheritance).toArray(), ((List<?>) inheritance).size(), String[].class);
                    else
                        return new String[]{inheritance.toString()};
                }catch(final ClassCastException | NullPointerException ignored){
                    return new String[0];
                }
            }).get(),
            ((Supplier<Map>) () -> {
                try{
                    return Collections.unmodifiableMap((Map) Objects.requireNonNull(value.get(ServerVars.Permissions.optionsKey)));
                }catch(final ClassCastException | NullPointerException ignored){
                    return new HashMap();
                }
            }).get(),
            ((Supplier<String[]>) () -> {
                try{
                    return
                        ((List<String>) Objects.requireNonNull(value.get(ServerVars.Permissions.permissionsKey)))
                            .stream()
                            .map(String::toLowerCase)
                            .toArray(String[]::new);
                }catch(final ClassCastException | NullPointerException ignored){
                    return new String[0];
                }
            }).get()
        );
    }

    public final String getGroup(){
        return getVar1();
    }

    public final String[] getInheritance(){
        return getVar2();
    }

    public final Map getOptions(){
        return getVar3();
    }

    public final String[] getPermissions(){
        return getVar4();
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("PermissionsGroup")
            .addObject("group",getGroup())
            .addObject("inheritance",getInheritance())
            .addObject("options",getOptions())
            .addObject("permissions",getPermissions())
            .toString();
    }

}
