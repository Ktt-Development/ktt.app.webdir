package com.kttdevelopment.webdir.server.permissions;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public final class PermissionsUser {

    private final InetAddress user;

    private final List<String> groups;
    private final Map options;
    private final List<String> permissions;

    public PermissionsUser(final String user, final Map value) throws UnknownHostException{
        this(InetAddress.getByName(user),value);
    }

    @SuppressWarnings("unchecked")
    public PermissionsUser(final InetAddress user, final Map value){
        this.user = user;

        // groups
        List<String> tGroup = new ArrayList<>();
        try{
            final Object groups = Objects.requireNonNull(value.get("permissions"));
            if(groups instanceof List)
                tGroup = (List<String>) groups;
            else
                tGroup.add(groups.toString());
        }catch(final ClassCastException | NullPointerException ignored){ }
        groups = Collections.unmodifiableList(tGroup);

        // options
        Map tOptions = new HashMap();
        try{
            tOptions = (Map) Objects.requireNonNull(value.get("options"));
        }catch(final ClassCastException | NullPointerException ignored){ }
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
    public final InetAddress getUser(){
        return user;
    }

    // ↓ declared as unmodifiable at assignment
    public final String getGroup(){
        return groups.get(0);
    }

    public final List<String> getGroups(){
        return groups;
    }

    public final Map getOptions(){
        return options;
    }

    public final List<String> getPermissions(){
        return permissions;
    }

}
