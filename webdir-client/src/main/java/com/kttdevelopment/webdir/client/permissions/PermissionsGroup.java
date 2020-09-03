package com.kttdevelopment.webdir.client.permissions;

import com.kttdevelopment.core.classes.ToStringBuilder;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public final class PermissionsGroup {

    private final String group;
    private final String[] inheritance, permissions;
    private final Map<?,?> options;

    @SuppressWarnings("unchecked")
    public PermissionsGroup(final String group, final Map value){
        super(
            group,
            ((Supplier<String[]>) () -> {
                final ILocaleService locale = Vars.Main.getLocaleService();
                final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("permissions"));

                try{
                    final Object inheritance = Objects.requireNonNull(value.get(ServerVars.Permissions.inheritanceKey));
                    if(inheritance instanceof List)
                        return Arrays.copyOf(((List<?>) inheritance).toArray(), ((List<?>) inheritance).size(), String[].class);
                    else
                        return new String[]{inheritance.toString()};
                }catch(final ClassCastException | NullPointerException ignored){
                    logger.warning(locale.getString("permissions.PermissionsGroup.missingInheritance",group));
                    return new String[0];
                }
            }).get(),
            ((Supplier<Map>) () -> {
                final ILocaleService locale = Vars.Main.getLocaleService();
                final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("permissions"));

                try{
                    return Collections.unmodifiableMap((Map) Objects.requireNonNull(value.get(ServerVars.Permissions.optionsKey)));
                }catch(final ClassCastException | NullPointerException ignored){
                    logger.warning(locale.getString("permissions.PermissionsGroup.missingOptions",group));
                    return new HashMap();
                }
            }).get(),
            ((Supplier<String[]>) () -> {
                final ILocaleService locale = Vars.Main.getLocaleService();
                final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("permissions"));

                try{
                    return ((List<String>) value.get(ServerVars.Permissions.permissionsKey)).toArray(new String[0]);
                }catch(final ClassCastException | NullPointerException ignored){
                    logger.warning(locale.getString("permissions.PermissionsGroup.missingPermissions",group));
                    return new String[0];
                }
            }).get()
        );
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
