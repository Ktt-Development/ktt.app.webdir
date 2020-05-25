package com.kttdevelopment.webdir.permissions;

import java.util.*;
import java.util.stream.Collectors;

public final class PermissionsGroup {

    private final String group;

    private final List<String> inheritance;
    private final Map options;
    private final List<String> permissions;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public PermissionsGroup(final String group, final Map value){
        this.group = group;

        // inheritance
        List<String> tInheritance = new ArrayList<>();
        try{
            final Object groups = Objects.requireNonNull(value.get("inheritance"));
            if(groups instanceof List)
                tInheritance = (List<String>) groups;
            else
                tInheritance.add(groups.toString());
        }catch(final ClassCastException | NullPointerException ignored){ }
        inheritance = Collections.unmodifiableList(tInheritance);

        // options
        Map tOptions = new HashMap();
        try{
            tOptions = (Map) value.get("options");
        }catch(final ClassCastException ignored){ }
        options = Collections.unmodifiableMap(tOptions);

        // permissions
        List<String> tPermissions = new ArrayList<>();
        try{
            tPermissions =
                ((List<String>) Objects.requireNonNull(value.get("permissions")))
                    .stream()
                    .map(String::toLowerCase).collect(Collectors.toList());
        }catch(final ClassCastException | NullPointerException ignored){ }
        permissions = Collections.unmodifiableList(tPermissions);
    }

    // ↓ inherently immutable
    public final String getGroup(){
        return group;
    }

    // ↓ declared as unmodifiable at assignment
    public List<String> getInheritance(){
        return inheritance;
    }

    public final Map getOptions(){
        return options;
    }

    public final List<String> getPermissions(){
        return permissions;
    }

}
