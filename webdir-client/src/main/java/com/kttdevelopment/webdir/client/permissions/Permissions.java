package com.kttdevelopment.webdir.client.permissions;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.utility.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.kttdevelopment.webdir.client.utility.SymbolicStringMatcher.MatchState.*;

public final class Permissions {

    private final Set<PermissionsGroup> groups = new HashSet<>();
    private final Set<PermissionsUser> users   = new HashSet<>();

    public Permissions(final YamlMapping value){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        logger.finer(locale.getString("permissions.permissions.start", value));

        // null
        {
            if(value == null){
                logger.severe(locale.getString("permissions.permissions.null"));
                return;
            }
        }

        // groups
        {
            final YamlMapping map = value.yamlMapping(PermissionsService.GROUPS);
            if(map != null)
                for(final YamlNode key : map.keys())
                    groups.add(new PermissionsGroup(asString(key), map.yamlMapping(key)));
        }

        // users
        {
            final YamlMapping map = value.yamlMapping(PermissionsService.USERS);
            if(map != null)
                for(final YamlNode key : map.keys()){
                    final String k = asString(key);
                    try{
                        users.add(new PermissionsUser(k, map.yamlMapping(key)));
                    }catch(final UnknownHostException e){
                        logger.severe(locale.getString("permissions.permissions.unknownUser", k) + LoggerService.getStackTraceAsString(e));
                    }
                }
        }

        logger.finer(locale.getString("permissions.permissions.finish", value));
    }

    private String asString(final YamlNode e){
        final LocaleService locale = Main.getLocale();
        final Logger logger = Main.getLogger(locale.getString("permissions.name"));

        try{
            return e.asScalar().value();
        }catch(final YamlReadingException | ClassCastException err){
            logger.warning(locale.getString("permissions.permissions.string", e) + LoggerService.getStackTraceAsString(err));
            return null;
        }
    }

    public final List<PermissionsGroup> getGroups(){
        return List.copyOf(groups);
    }

    public final List<PermissionsUser> getUsers(){
        return List.copyOf(users);
    }

    public final PermissionsUser getUser(final InetAddress address){
        for(final PermissionsUser user : users)
            if(user.getUser().equals(address) || ExceptionUtility.requireNonExceptionElse(() -> address.isLoopbackAddress() && user.getUser().equals(InetAddress.getLocalHost()), false))
                return user;
        return null;
    }

    // options
    public final String getOption(final String option){
        return getOption(null, option);
    }

    public final String getOption(final InetAddress address, final String option){
        final PermissionsUser user = getUser(address);

        // default
        String defaultValue = null;
        for(final PermissionsGroup group : getDefaultGroupsAndInherited())
            if(defaultValue == null && group.getOptions().get(option) != null)
                defaultValue = group.getOptions().get(option);

        if(user == null) return defaultValue;

        // user
        // user option is most specific one available
        if(user.getOptions().containsKey(option))
            return user.getOptions().get(option);

        // group
        // should not inherit option 'default' from groups, this value just tells permissions which ones are default groups.
        if(option.equals(PermissionsService.DEF)) return null;

        for(final PermissionsGroup group : getGroupsAndInherited(user.getGroups()))
            if(group.getOptions().get(option) != null)
                return group.getOptions().get(option);
        return defaultValue;
    }

    // permissions
    public final boolean hasPermission(final String permission){
        return hasPermission((InetAddress) null, permission);
    }

    public final boolean hasPermission(final InetAddress address, final String permission){
        if(permission == null) // treat null as no required permission (true)
            return true;

        final PermissionsUser user = getUser(address);

        // default
        SymbolicStringMatcher.MatchState hasDefaultPermission = NO_MATCH;
        SymbolicStringMatcher.MatchState perm;
        for(final PermissionsGroup group : getDefaultGroupsAndInherited())
            if(hasDefaultPermission != NEGATIVE_MATCH && (perm = hasPermission(group,permission)) != NO_MATCH) // let negative match take precedence
                hasDefaultPermission = perm;

        if(user == null) return hasDefaultPermission == MATCH; // only if has perm

        // user
        if((perm = hasPermission(permission, user.getPermissions())) != NO_MATCH) // only if has perm
            return perm == MATCH;

        // group
        for(final PermissionsGroup group : getGroupsAndInherited(user.getGroups()))
            if(hasPermission(group, permission) == MATCH)
                return true;
        return hasDefaultPermission == MATCH;
    }

    private SymbolicStringMatcher.MatchState hasPermission(final PermissionsGroup group, final String permission){
        boolean hasPermission = false;
        for(final PermissionsGroup g : getGroupsAndInherited(Collections.singletonList(group.getGroup()))){
            final SymbolicStringMatcher.MatchState hp = hasPermission(permission, g.getPermissions());
            if(hp == MATCH)
                hasPermission = true;
            else if(hp == NEGATIVE_MATCH)
                return NEGATIVE_MATCH;
        }
        return hasPermission ? MATCH : NO_MATCH;
    }

    private SymbolicStringMatcher.MatchState hasPermission(final String permission, final List<String> permissions){
        boolean hasPermission = false;
        for(final String perm : permissions)
            switch(SymbolicStringMatcher.matches(perm, permission)){
                case NEGATIVE_MATCH:
                    return NEGATIVE_MATCH;
                case MATCH:
                    hasPermission = true;
            }
        return hasPermission ? MATCH : NO_MATCH;
    }

    // get default groups + inherited

    private List<PermissionsGroup> getDefaultGroupsAndInherited(){
        final List<PermissionsGroup> defaultGroups = new ArrayList<>();

        for(final PermissionsGroup group : groups){
            // try catch not needed because invalid boolean resolves to false
            if(group.getOptions().containsKey(PermissionsService.DEF) && Boolean.parseBoolean(group.getOptions().get(PermissionsService.DEF))){
                defaultGroups.add(group);
                defaultGroups.addAll(getInheritedGroups(group));
                // if default group inherits another group the default option doesn't matter, sub inheritance as well
            }
        }
        return defaultGroups.stream().distinct().collect(Collectors.toList());
    }

    // get groups + inherited groups

    private List<PermissionsGroup> getGroupsAndInherited(final List<String> groups){
        final List<PermissionsGroup> fullGroups = new ArrayList<>();
        for(final PermissionsGroup group : this.groups){
            if(groups.contains(group.getGroup())){
                fullGroups.add(group);
                fullGroups.addAll(getInheritedGroups(group));
            }
        }
        return fullGroups.stream().distinct().collect(Collectors.toList());
    }

    // inheritance checkers

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

        final List<String> inheritance = group.getInheritance();
        for(final PermissionsGroup g : this.groups){
            if(!read.contains(g) && inheritance.contains(g.getGroup())){
                read.add(g);
                read.addAll(getInheritedGroups(g,read));
                // read param is required to make sure we don't have an infinite loop
            }
        }
        return OUT;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("groups", groups)
            .addObject("users", users)
            .toString();
    }

}
