package com.kttdevelopment.webdir.permissions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings("rawtypes")
public final class Permissions {

    private final Map obj;

    private final List<PermissionsGroup> groups = new ArrayList<>();
    private final List<PermissionsUser>  users = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public Permissions(final Map obj){
        this.obj = obj;

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
    }

    //

    public final List<PermissionsGroup> getGroups(){
        return Collections.unmodifiableList(groups);
    }

    public final List<PermissionsUser> getUsers(){
        return Collections.unmodifiableList(users);
    }

    public final PermissionsUser getUser(final InetAddress address){
        for(final PermissionsUser u : users)
            if(u.getUser().equals(address))
                return u;
        return null;
    }

    //

    public final Object getOption(final InetAddress address, final String option){
        final PermissionsUser user = getUser(address);

        if(user != null) // test options directly assigned to user
            if(user.getOptions().containsKey(option))
                return user.getOptions().get(option);

        Object def = null;
        for(final PermissionsGroup group : groups){ // find options by group
            if(user != null && user.getGroups().contains(group.getGroup()))
                if(group.getOptions().containsKey(option))
                    return group.getOptions().get(option);
            else if(Objects.requireNonNullElse(Boolean.parseBoolean(group.getOptions().get("default").toString()),false)) // use default if none found
                if(group.getOptions().containsKey(option))
                    def = group.getOptions().get(option);
        return def;
    }

    public final boolean hasPermission(final InetAddress address, final String permission){
        final PermissionsUser user = getUser(address);

        if(user != null)
            for(final String perm : user.getPermissions())
                if(perm.equals(permission) || (permission.endsWith("*") && perm.startsWith(permission)))
                    return true;

        for(final PermissionsGroup group : groups)
            if(
                (
                    Objects.requireNonNullElse(Boolean.parseBoolean(group.getOptions().get("default").toString()), false) ||
                    (
                        user != null &&
                        user.getGroups().contains(group.getGroup())
                    )
                ) && hasPermission(group,permission)
            )
                return true;
        return false;
    }

    public final boolean hasPermission(final PermissionsGroup group, final String permission){
        for(final PermissionsGroup g : getInheritedGroups(group))
            for(final String perm : g.getPermissions())
                if(perm.equals(permission) || (permission.endsWith("*") && perm.startsWith(permission)))
                    return true;
        return false;
    }

    public final Map toMap(){
        return Collections.unmodifiableMap(obj);
    }

    //

    public final List<PermissionsGroup> getInheritedGroups(final PermissionsGroup group){
        return getInheritedGroups(Collections.singletonList(group));
    }

    public final List<PermissionsGroup> getInheritedGroups(final List<PermissionsGroup> groups){
        final List<PermissionsGroup> OUT = new LinkedList<>();
        groups.forEach(permissionsGroup -> OUT.addAll(getInheritedGroups(permissionsGroup, OUT)));
        return OUT;
    }

    private List<PermissionsGroup> getInheritedGroups(final PermissionsGroup group, final List<PermissionsGroup> read){
        final List<PermissionsGroup> OUT = new LinkedList<>(read);
        OUT.add(group);
        final List<String> inheritance = group.getInheritance();
        final List<PermissionsGroup> queue = new LinkedList<>();
        for(final PermissionsGroup g : this.groups)
            if(!read.contains(g) && inheritance.contains(g.getGroup()))
                queue.add(g);

        for(final PermissionsGroup g : queue)
            OUT.addAll(getInheritedGroups(g,Collections.unmodifiableList(OUT)));

        return Collections.unmodifiableList(OUT);
    }

}
