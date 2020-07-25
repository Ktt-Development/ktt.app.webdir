package com.kttdevelopment.webdir.server.permissions;

import com.kttdevelopment.webdir.generator.object.Tuple4;
import com.kttdevelopment.webdir.server.ServerVars;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public final class PermissionsUser extends Tuple4<InetAddress,List<String>,Map,List<String>> {

    public PermissionsUser(final String user, final Map value) throws UnknownHostException{
        this(InetAddress.getByName(user),value);
    }

    @SuppressWarnings("unchecked")
    public PermissionsUser(final InetAddress user, final Map value){
        super(
            user,
            ((Supplier<List<String>>) () -> {
                try{
                    final Object groups = Objects.requireNonNull(value.get(ServerVars.Permissions.groupsKey));
                    if(groups instanceof List)
                        return Collections.unmodifiableList(((List) groups));
                    else
                        return Collections.singletonList(groups.toString());
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

    public final InetAddress getUser(){
        return getVar1();
    }

    public final List<String> getGroups(){
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
            "PermissionsUser" + '{' +
            "user"          + '=' + getVar1() + ", " +
            "groups"        + '=' + getVar2() + ", " +
            "options"       + '=' + getVar3() + ", " +
            "permissions"   + '=' + getVar4() +
            '}';
    }

}
