package com.kttdevelopment.webdir.server.permissions;

import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.function.*;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
import com.kttdevelopment.webdir.server.ServerVars;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public final class Permissions {

    private final Set<PermissionsGroup> groups = new HashSet<>();
    private final Set<PermissionsUser> users   = new HashSet<>();

    @SuppressWarnings("unchecked")
    public Permissions(final Map obj){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("permissions"));

        try{
            final Map g = (Map) Objects.requireNonNull(obj.get(ServerVars.Permissions.groupsKey));
            g.forEach((k, v) -> {
                try{ groups.add(new PermissionsGroup(k.toString(), (Map) v));
                }catch(final ClassCastException ignored){
                    logger.severe(locale.getString("permissions.Permissions.invalidGroupType",k));
                }
            });
        }catch(final ClassCastException ignored){
            logger.severe(locale.getString("permissions.Permissions.invalidGroups"));
        }catch(final NullPointerException ignored){
            logger.severe(locale.getString("permissions.Permissions.missingGroups"));
        }

        try{
            final Map u = (Map) Objects.requireNonNull(obj.get(ServerVars.Permissions.usersKey));
            u.forEach((k, v) -> {
                try{ users.add(new PermissionsUser(k.toString(), (Map) v));
                }catch(final ClassCastException ignored){
                    logger.severe(locale.getString("permissions.Permissions.invalidUserType",k));
                }catch(final UnknownHostException e){
                    logger.severe(locale.getString("permissions.Permissions.invalidUser",k) + '\n' + Exceptions.getStackTraceAsString(e));
                }
            });
        }catch(final ClassCastException ignored){
            logger.severe(locale.getString("permissions.Permissions.invalidUsers"));
        }catch(final NullPointerException ignored){
            logger.severe(locale.getString("permissions.Permissions.missingUsers"));
        }
    }

    //

    public final List<PermissionsGroup> getGroups(){
        return new ArrayList<>(groups);
    }

    public final List<PermissionsUser> getUsers(){
        return new ArrayList<>(users);
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

        Object defaultValue = null;
        for(final PermissionsGroup group : getDefaultGroupsAndInherited())
            if(group.getOptions().get(option) != null)
                defaultValue = group.getOptions().get(option);

        if(user == null) return defaultValue;

        if(user.getOptions().containsKey(option)) // user option is most specific one available
            return user.getOptions().get(option);

        if(option.equals("option")) return null; // should not inherit option 'default' from groups, this value just tells permissions which ones are default groups.

        for(final PermissionsGroup group : getGroupsAndInherited(user.getGroups()))
            if(group.getOptions().get(option) != null)
                return group.getOptions().get(option);
        return defaultValue;
    }

    public final boolean hasPermission(final InetAddress address, final String permission){
        final PermissionsUser user = getUser(address);

        boolean hasDefaultPermission = false;
        for(final PermissionsGroup group : getDefaultGroupsAndInherited())
            if(hasPermission(group,permission))
                hasDefaultPermission = true;

        if(user == null) return hasDefaultPermission;

        if(hasPermission(permission,user.getPermissions()))
            return true;

        for(final PermissionsGroup group : getGroupsAndInherited(user.getGroups()))
            if(hasPermission(permission,group.getPermissions()))
                return true;
        return hasDefaultPermission;
    }

    private boolean hasPermission(final PermissionsGroup group, final String permission){
        for(final PermissionsGroup g : getGroupsAndInherited(Collections.singletonList(group.getGroup())))
            if(hasPermission(permission,g.getPermissions()))
                return true;
        return false;
    }

    private boolean hasPermission(final String permission, final String[] permissions){
        return hasPermission(permission,Arrays.asList(permissions));
    }

    private boolean hasPermission(final String permission, final List<String> permissions){
        boolean hasPermission = false;
        for(final String perm : permissions)
            switch(SymbolicStringMatcher.matches(perm,permission)){
                case NEGATIVE_MATCH:
                    return false;
                case MATCH:
                    hasPermission = true;
            }
        return hasPermission;
    }

//

    public final List<PermissionsGroup> getDefaultGroupsAndInherited(){
        final List<PermissionsGroup> defaultGroups = new ArrayList<>();

        for(final PermissionsGroup group : groups){
            if(group.getOptions().containsKey(ServerVars.Permissions.defaultKey) && Boolean.parseBoolean(group.getOptions().get(ServerVars.Permissions.defaultKey).toString())){
                defaultGroups.add(group);
                defaultGroups.addAll(getInheritedGroups(group)); // if default group inherits another group the default option doesn't matter, sub inheritance as well
            }
        }
        return defaultGroups.stream().distinct().collect(Collectors.toList());
    }

    //

    public final List<PermissionsGroup> getGroupsAndInherited(final String[] groups){
        return getGroupsAndInherited(Arrays.asList(groups));
    }

    public final List<PermissionsGroup> getGroupsAndInherited(final List<String> groups){
        final List<PermissionsGroup> fullGroups = new ArrayList<>();
        for(final PermissionsGroup group : this.groups){
            if(groups.contains(group.getGroup())){
                fullGroups.add(group);
                fullGroups.addAll(getInheritedGroups(group));
            }
        }
        return fullGroups.stream().distinct().collect(Collectors.toList());
    }

    //

    private List<PermissionsGroup> getInheritedGroups(final PermissionsGroup group){
        return getInheritedGroups(Collections.singletonList(group));
    }

    private List<PermissionsGroup> getInheritedGroups(final List<PermissionsGroup> groups){
        final List<PermissionsGroup> OUT = new LinkedList<>();
        groups.forEach(permissionsGroup -> OUT.addAll(getInheritedGroups(permissionsGroup, OUT)));
        return OUT;
    }

    private List<PermissionsGroup> getInheritedGroups(final PermissionsGroup group, final List<PermissionsGroup> read){
        final List<PermissionsGroup> OUT = new ArrayList<>(read);
        OUT.add(group);

        final List<String> inheritance = Arrays.asList(group.getInheritance());
        for(final PermissionsGroup g : this.groups){
            if(!read.contains(g) && inheritance.contains(g.getGroup())){
                read.add(g);
                read.addAll(getInheritedGroups(g,read));
            }
        }
        return OUT;
    }

    //

    @Override
    public boolean equals(final Object o){
        if(this == o)
            return true;
        else if(!(o instanceof Permissions))
            return false;
        final Permissions other = ((Permissions) o);
        return other.groups.equals(groups) &&
               other.users.equals(users);
    }

    @Override
    public String toString(){
        return new toStringBuilder("Permissions")
            .addObject("groups",groups)
            .addObject("users",users)
            .toString();
    }

}
