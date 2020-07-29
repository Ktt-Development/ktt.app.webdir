package com.kttdevelopment.webdir.server.permissions;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.object.Tuple4;
import com.kttdevelopment.webdir.server.ServerVars;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public final class PermissionsUser extends Tuple4<InetAddress,String[],Map,String[]> {

    public PermissionsUser(final String user, final Map value) throws UnknownHostException{
        this(InetAddress.getByName(user),value);
    }

    @SuppressWarnings("unchecked")
    public PermissionsUser(final InetAddress user, final Map value){
        super( // if user uses loop back address (127.0.0.1) then use local address instead; (server uses machine address instead of 127.0.0.1)
            Exceptions.requireNonExceptionElse(() -> user.isLoopbackAddress() ? InetAddress.getLocalHost() : user, user),
            ((Supplier<String[]>) () -> {
                try{
                    final Object groups = Objects.requireNonNull(value.get(ServerVars.Permissions.groupsKey));
                    if(groups instanceof List)
                        return Arrays.copyOf(((List<?>) groups).toArray(), ((List<?>) groups).size(), String[].class);
                    else
                        return new String[]{groups.toString()};
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

    public final InetAddress getUser(){
        return getVar1();
    }

    public final String[] getGroups(){
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
        return new toStringBuilder("PermissionsUser")
            .addObject("user",getUser())
            .addObject("groups",getGroups())
            .addObject("options",getOptions())
            .addObject("permissions",getPermissions())
            .toString();
    }

}
