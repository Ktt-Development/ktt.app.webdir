package com.kttdevelopment.webdir.client.permissions;

import com.amihaiemil.eoyaml.*;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.util.*;
import java.util.logging.Logger;

public final class PermissionsGroup {

    private final String group;
    private final List<String> inheritance = new ArrayList<>(), permissions = new ArrayList<>();
    private final Map<String,String> options = new HashMap<>();

    public PermissionsGroup(final String group, final YamlMapping value){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        logger.finest(locale.getString("permissions.permissionsGroup.start", group, value));

        this.group = group;

        // null check
        {
            if(value == null){
                logger.severe(locale.getString("permissions.permissionsGroup.null", group));
                return;
            }
        }

        // inheritance
        {
            final YamlSequence seq = value.yamlSequence(PermissionsService.INHERITANCE);
            String inherit;
            if(seq != null)
                seq.forEach(e -> inheritance.add(asString(e)));
            else if((inherit = value.string(PermissionsService.INHERITANCE)) != null)
                inheritance.add(inherit);
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

        logger.finest(locale.getString("permissions.permissionsGroup.finish", group));
    }

    private String asString(final YamlNode e){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        try{
            return e.asScalar().value();
        }catch(final YamlReadingException | ClassCastException err){
            logger.warning(locale.getString("permissions.permissionsGroup.string", e, group) + LoggerService.getStackTraceAsString(err));
            return null;
        }
    }

    public final String getGroup(){
        return group;
    }

    public final List<String> getInheritance(){
        return Collections.unmodifiableList(inheritance);
    }

    public final List<String> getPermissions(){
        return Collections.unmodifiableList(permissions);
    }

    public final Map<String,String> getOptions(){
        return Collections.unmodifiableMap(options);
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("group", getGroup())
            .addObject("inheritance", getInheritance())
            .addObject("options", getOptions())
            .addObject("permissions", getPermissions())
            .toString();
    }

}
