package com.kttdevelopment.webdir.permissions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Consumer;

public final class Permissions {

    private final List<PermissionsGroup> groups = new ArrayList<>();
    private final List<PermissionsUser>  users = new ArrayList<>();
    private final List<String> defaultPermissions = new ArrayList<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Permissions(final Map obj){
        try{
            final Map g = (Map) Objects.requireNonNull(obj.get("groups"));
            g.forEach((k, v) -> {
                try{ groups.add(new PermissionsGroup(k.toString(), (Map) v));
                }catch(final ClassCastException ignored){ }
            });
        }catch(final ClassCastException | NullPointerException ignored){ }

        try{
            final Map u = (Map) Objects.requireNonNull(obj.get("users"));
            u.forEach((k, v) -> {
                try{ users.add(new PermissionsUser(k.toString(), (Map) v));
                }catch(final ClassCastException | UnknownHostException ignored){ }
            });
        }catch(final ClassCastException | NullPointerException ignored){ }

        // get def permissions and loop through inheritance
    }

    public final boolean hasPermission(final InetAddress address, final String permission){
        PermissionsUser user = null;
        for(final PermissionsUser u : users){
            if(u.getUser().equals(address)){
                user = u;
                break;
            }
        }

        final List<PermissionsGroup> allowedGroups = new ArrayList<>();
        final List<PermissionsGroup> defaultGroups = new ArrayList<>();

        final PermissionsUser finalUser = user;
        groups.forEach(group -> {
            try{
                if(Objects.requireNonNullElse(Boolean.parseBoolean(group.getOptions().get("default").toString()), false))
                    defaultGroups.add(group);
            }catch(final ClassCastException ignored){ }
        });
        // todo
        return false;
    }

    public final boolean hasPermission(final PermissionsGroup group, final String permission){
        // check direct permissions
        for(final String perm : group.getPermissions())
            if(perm.equalsIgnoreCase(permission) || (permission.endsWith("*") && perm.startsWith(permission)))
                return true;

        // check inherited permissions

        return hasPermission(List.of(group), group, permission);
    }

    private boolean hasPermission(final List<PermissionsGroup> read, final PermissionsGroup group, final String permission){
        // populate direct inheritance
        final List<PermissionsGroup> groups = new ArrayList<>();
        for(final PermissionsGroup g : this.groups){
            if(!read.contains(g) && group.getInheritance().contains(g.getGroup())){
                groups.add(g);

                // check direct permissions
                for(final String perm : g.getPermissions())
                    if(perm.equalsIgnoreCase(permission) || (permission.endsWith("*") && perm.startsWith(permission)))
                        return true;
            }
        }

        if(groups.size() == 0)
            return false;
        else
            //for(final Permissions g : groups){

           // }
            return hasPermission(Collections.unmodifiableList(read),group,permission);
    }



}
