package com.kttdevelopment.webdir.client.permissions;

import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.webdir.client.*;

import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public final class PermissionsGroup {

    private final String group;
    private final String[] inheritance, permissions;
    private final Map<?,?> options;

    @SuppressWarnings("unchecked")
    public PermissionsGroup(final String group, final Map value){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("permissions"));

        this.group = group;
        {
            String[] tInheritance = new String[0];
            try{
                final Object inheritance = Objects.requireNonNull(value.get("inheritance"));
                if(inheritance instanceof List)
                    tInheritance = Arrays.copyOf(((List<?>) inheritance).toArray(), ((List<?>) inheritance).size(), String[].class);
                else
                    tInheritance = new String[]{inheritance.toString()};
            }catch(final ClassCastException e){
                logger.warning(locale.getString("permissions.permissionsGroup.invalidInheritance", group) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final NullPointerException e){
                logger.warning(locale.getString("permissions.permissionsGroup.missingInheritance", group) + '\n' + LoggerService.getStackTraceAsString(e));
            }
            this.inheritance = tInheritance;
        }
        {
            Map tOptions = new HashMap();
            try{
                tOptions = Collections.unmodifiableMap((Map) Objects.requireNonNull(value.get("options")));
            }catch(final ClassCastException e){
                logger.warning(locale.getString("permissions.permissionsGroup.invalidOptions", group) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final NullPointerException e){
                logger.warning(locale.getString("permissions.permissionsGroup.missingOptions", group) + '\n' + LoggerService.getStackTraceAsString(e));
            }
            this.options = tOptions;
        }
        {
            String[] tPermissions = new String[0];
            try{
                tPermissions = ((List<String>) value.get("permissions")).toArray(new String[0]);
            }catch(final ClassCastException e){
                logger.warning(locale.getString("permissions.permissionsGroup.invalidPermissions", group) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final NullPointerException e){
                logger.warning(locale.getString("permissions.permissionsGroup.missingPermissions", group) + '\n' + LoggerService.getStackTraceAsString(e));
            }
            this.permissions = tPermissions;
        }
    }

    public final String getGroup(){
        return group;
    }

    public final String[] getInheritance(){
        return inheritance;
    }

    public final Map<?,?> getOptions(){
        return Collections.unmodifiableMap(options);
    }

    public final String[] getPermissions(){
        return permissions;
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(o == null || getClass() != o.getClass())
            return false;
        final PermissionsGroup other = ((PermissionsGroup) o);
        return other.getGroup().equals(getGroup()) &&
               Arrays.equals(other.getInheritance(),getInheritance()) &&
               other.getOptions().equals(getOptions()) &&
               Arrays.equals(other.getPermissions(),getPermissions());
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("group",getGroup())
            .addObject("inheritance",getInheritance())
            .addObject("options",getOptions())
            .addObject("permissions",getPermissions())
            .toString();
    }

}
