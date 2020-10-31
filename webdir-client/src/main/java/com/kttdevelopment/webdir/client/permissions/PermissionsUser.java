package com.kttdevelopment.webdir.client.permissions;

import com.amihaiemil.eoyaml.*;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;

public final class PermissionsUser {

    private final InetAddress user;
    private final List<String> groups = new ArrayList<>(), permissions = new ArrayList<>();
    private final Map<String,String> options = new HashMap<>();

    public PermissionsUser(final String user, final YamlMapping value) throws UnknownHostException{
        this(InetAddress.getByName(user), value);
    }

    public PermissionsUser(final InetAddress user, final YamlMapping value){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        logger.finest(locale.getString("permissions.permissionsUser.start", user.getHostAddress(), value));

        // uniform local address
        {
            // if user is loop back address (127.0.0.1) then use local address instead
            this.user = ExceptionUtility.requireNonExceptionElse(() -> user.isLoopbackAddress() ? InetAddress.getLocalHost() : user, user);
        }

        // null check
        {
            if(value == null){
                logger.severe(locale.getString("permissions.permissionsUser.null", user));
                return;
            }
        }

        // groups
        {
            final YamlSequence seq = value.yamlSequence(PermissionsService.GROUPS);
            String inherit;
            if(seq != null)
                seq.forEach(e -> groups.add(asString(e)));
            else if((inherit = value.string(PermissionsService.GROUPS)) != null)
                groups.add(inherit);
        }

        // options
        {
            final YamlMapping map = value.yamlMapping(PermissionsService.OPTIONS);
            if(map != null)
                for(final YamlNode key : map.keys())
                    options.put(asString(key), map.string(key));
        }

        // permissions
        {
            final YamlSequence seq = value.yamlSequence(PermissionsService.PERMISSIONS);
            if(seq != null)
                seq.forEach(e -> permissions.add(asString(e)));
        }

        logger.finest(locale.getString("permissions.permissionsUser.finish", this.user.getHostAddress()));
    }

    public final InetAddress getUser(){
        return user;
    }

    public final List<String> getGroups(){
        return Collections.unmodifiableList(groups);
    }

    public final Map<String,String> getOptions(){
        return Collections.unmodifiableMap(options);
    }

    public final List<String> getPermissions(){
        return Collections.unmodifiableList(permissions);
    }

    private String asString(final YamlNode e){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        try{
            return e.asScalar().value();
        }catch(final YamlReadingException | ClassCastException err){
            logger.warning(locale.getString("permissions.permissionsUser.string", e, user.getHostAddress()) + LoggerService.getStackTraceAsString(err));
            return null;
        }
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("user",user)
            .addObject("groups", groups)
            .addObject("options", options)
            .addObject("permissions", permissions)
            .toString();
    }

}
