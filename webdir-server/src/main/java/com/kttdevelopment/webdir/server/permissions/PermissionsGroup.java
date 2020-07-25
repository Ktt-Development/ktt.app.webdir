package com.kttdevelopment.webdir.server.permissions;

import com.kttdevelopment.webdir.generator.object.Tuple4;
import com.kttdevelopment.webdir.server.ServerVars;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public final class PermissionsGroup extends Tuple4<String,List<String>,Map,List<String>> {


    @SuppressWarnings("unchecked")
    public PermissionsGroup(final String group, final Map value){
        super(
            group,
            ((Supplier<List<String>>) () -> {
                try{
                    final Object inheritance = Objects.requireNonNull(value.get(ServerVars.Permissions.inheritanceKey));
                    if(inheritance instanceof List)
                        return Collections.unmodifiableList(((List) inheritance));
                    else
                        return Collections.singletonList(inheritance.toString());
                }catch(final ClassCastException | NullPointerException ignored){
                    return new ArrayList<>();
                }
            }).get(),
            ((Supplier<Map>) () -> {
                try{
                    return Collections.unmodifiableMap((Map) Objects.requireNonNull(value.get(ServerVars.Permissions.optionsKey)));
                }catch(final ClassCastException | NullPointerException ignored){
                    return new HashMap();
                }
            }).get(),
            ((Supplier<List<String>>) () -> {
                try{
                    return
                        ((List<String>) Objects.requireNonNull(value.get(ServerVars.Permissions.permissionsKey)))
                            .stream()
                            .map(String::toLowerCase).collect(Collectors.toList());
                }catch(final ClassCastException | NullPointerException ignored){
                    return new ArrayList<>();
                }
            }).get()
        );
    }

    public final String getGroup(){
        return getVar1();
    }

    public final List<String> getInheritance(){
        return getVar2();
    }

    public final Map getOptions(){
        return getVar3();
    }

    public final List<String> getPermissions(){
        return getVar4();
    }

    //

    @Override
    public String toString(){
        return
            "PermissionsGroup" + '{' +
            "group"         + '=' + getVar1() + ", " +
            "inheritance"   + '=' + getVar2() + ", " +
            "options"       + '=' + getVar3() + ", " +
            "permissions"   + '=' + getVar4() +
            '}';
    }

}
