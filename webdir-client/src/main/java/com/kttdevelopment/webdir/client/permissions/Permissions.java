/*
 * Copyright (C) 2021 Ktt Development
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.kttdevelopment.webdir.client.permissions;

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

    public Permissions(final Map<String,Object> value){
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
            final Object obj = value.get(PermissionsService.GROUPS);
            if(obj instanceof Map<?,?> && !((Map<?, ?>) obj).isEmpty())
                for(final Map.Entry<String,Object> entry : MapUtility.asStringObjectMap((Map<?,?>) obj).entrySet())
                    groups.add(new PermissionsGroup(entry.getKey(), MapUtility.asStringObjectMap((Map<?,?>) entry.getValue())));
        }

        // users
        {
            final Object obj = value.get(PermissionsService.USERS);
            if(obj instanceof Map<?,?> && !((Map<?, ?>) obj).isEmpty())
                for(final Map.Entry<String,Object> entry : MapUtility.asStringObjectMap((Map<?,?>) value.get(PermissionsService.USERS)).entrySet())
                    try{
                        users.add(new PermissionsUser(entry.getKey(), MapUtility.asStringObjectMap((Map<?, ?>) entry.getValue())));
                    }catch(final UnknownHostException e){
                        logger.severe(locale.getString("permissions.permissions.unknownUser", entry.getKey()) + LoggerService.getStackTraceAsString(e));
                    }
        }

        logger.finer(locale.getString("permissions.permissions.finish"));
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
                defaultValue = group.getOptions().get(option).toString();

        if(user == null) return defaultValue;

        // user
        // user option is most specific one available
        if(user.getOptions().containsKey(option) && user.getOptions().get(option) != null)
            return user.getOptions().get(option).toString();

        // group
        // should not inherit option 'default' from groups, this value just tells permissions which ones are default groups.
        if(option.equals(PermissionsService.DEF)) return null;

        for(final PermissionsGroup group : getGroupsAndInherited(user.getGroups()))
            if(group.getOptions().get(option) != null)
                return group.getOptions().get(option).toString();
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
            Object def;
            if(group.getOptions().containsKey(PermissionsService.DEF) && Boolean.parseBoolean((def = group.getOptions().get(PermissionsService.DEF)) == null ? null : def.toString())){
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
