package com.kttdevelopment.webdir.server.permissions;

import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.object.Tuple4;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.server.ServerVars;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

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
                final LocaleService locale = Vars.Main.getLocaleService();
                final Logger logger        = Main.getLoggerService() != null && locale != null ? Main.getLoggerService().getLogger(locale.getString("permissions")) : Logger.getLogger("Permissions");

                try{
                    final Object groups = Objects.requireNonNull(value.get(ServerVars.Permissions.groupsKey));
                    if(groups instanceof List)
                        return Arrays.copyOf(((List<?>) groups).toArray(), ((List<?>) groups).size(), String[].class);
                    else
                        return new String[]{groups.toString()};
                }catch(final ClassCastException | NullPointerException ignored){
                    if(locale != null)
                        logger.warning(locale.getString("permissions.PermissionsUser.missingGroups",user));
                    return new String[0];
                }
            }).get(),
            ((Supplier<Map>) () -> {
                final LocaleService locale = Vars.Main.getLocaleService();
                final Logger logger        = Main.getLoggerService() != null && locale != null ? Main.getLoggerService().getLogger(locale.getString("permissions")) : Logger.getLogger("Permissions");

                try{
                    return Collections.unmodifiableMap((Map) Objects.requireNonNull(value.get(ServerVars.Permissions.optionsKey)));
                }catch(final ClassCastException | NullPointerException ignored){
                    if(locale != null)
                        logger.warning(locale.getString("permissions.PermissionsUser.missingOptions",user));
                    return new HashMap();
                }
            }).get(),
            ((Supplier<String[]>) () -> {
                final LocaleService locale = Vars.Main.getLocaleService();
                final Logger logger        = Main.getLoggerService() != null && locale != null ? Main.getLoggerService().getLogger(locale.getString("permissions")) : Logger.getLogger("Permissions");

                try{
                    return ((List<String>) value.get(ServerVars.Permissions.permissionsKey)).toArray(new String[0]);
                }catch(final ClassCastException | NullPointerException ignored){
                    if(locale != null)
                        logger.warning(locale.getString("permissions.PermissionsUser.missingPermissions",user));
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
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(o == null || getClass() != o.getClass())
            return false;
        final PermissionsUser other = ((PermissionsUser) o);
        return (other.getUser().equals(getUser()) || (other.getUser().isLoopbackAddress() && getUser().isLoopbackAddress())) &&
               Arrays.equals(other.getGroups(),getGroups()) &&
               other.getOptions().equals(getOptions()) &&
               Arrays.equals(other.getPermissions(),getPermissions());
    }

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
