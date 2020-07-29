package com.kttdevelopment.webdir.server.permissions;

import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.server.ServerVars;

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
            final Map g = (Map) Objects.requireNonNull(obj.get(ServerVars.Permissions.groupsKey));
            g.forEach((k, v) -> {
                try{ groups.add(new PermissionsGroup(k.toString(), (Map) v));
                }catch(final ClassCastException ignored){ }
            });
        }catch(final ClassCastException | NullPointerException ignored){ }

        try{
            final Map u = (Map) Objects.requireNonNull(obj.get(ServerVars.Permissions.usersKey));
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
        for(final PermissionsUser u : users) // if user happens to use 127.0.0.1 wildcard address resolve to local machine address
            if(u.getUser().equals(address) || Exceptions.requireNonExceptionElse(() -> address.isLoopbackAddress() && u.getUser().equals(InetAddress.getLocalHost()), false))
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
        for(final PermissionsGroup group : groups)
            if(group.getOptions().containsKey(option)) // if group contains option
                if(user != null && Arrays.asList(user.getGroups()).contains(group.getGroup())) // if user is a member of the group
                    return group.getOptions().get(option);
                else if(Objects.requireNonNullElse(Boolean.parseBoolean(group.getOptions().get("default").toString()),false)) // if group is default use set default option
                    def = group.getOptions().get(option);
        return def;
    }

    public final boolean hasPermission(final InetAddress address, final String permission){
        final PermissionsUser user = getUser(address);

        boolean hasPerm = false;
        if(user != null)
            for(final String perm : user.getPermissions())
                if(perm.equals('!' + permission) || (perm.startsWith('!' + permission) && perm.endsWith("*")))
                    return false;
                else if(perm.equals(permission) || (perm.startsWith(permission) && perm.endsWith("*")))
                    hasPerm = true;

        if(hasPerm) return true;

        for(final PermissionsGroup group : groups)
            if(
                (
                    Objects.requireNonNullElse(Boolean.parseBoolean(group.getOptions().get("default").toString()), false) ||
                    (
                        user != null &&
                        Arrays.asList(user.getGroups()).contains(group.getGroup())
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

    public final List<PermissionsGroup> getInheritedGroups(final List<PermissionsGroup> groups){ // this code needs infinite loop prevention
        final List<PermissionsGroup> OUT = new LinkedList<>();
        groups.forEach(permissionsGroup -> OUT.addAll(getInheritedGroups(permissionsGroup, OUT)));
        return OUT;
    }

    private List<PermissionsGroup> getInheritedGroups(final PermissionsGroup group, final List<PermissionsGroup> read){
        final List<PermissionsGroup> OUT = new LinkedList<>(read);
        OUT.add(group);
        final List<String> inheritance = Arrays.asList(group.getInheritance());
        final List<PermissionsGroup> queue = new LinkedList<>();
        for(final PermissionsGroup g : this.groups)
            if(!read.contains(g) && inheritance.contains(g.getGroup()))
                queue.add(g);

        for(final PermissionsGroup g : queue)
            OUT.addAll(getInheritedGroups(g,Collections.unmodifiableList(OUT)));

        return Collections.unmodifiableList(OUT);
    }

}
