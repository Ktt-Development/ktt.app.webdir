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

package com.kttdevelopment.webdir.client.config;

import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

import java.util.regex.Pattern;

public final class Setting {

    private final String key, def, desc, yaml;

    @SuppressWarnings("FieldCanBeLocal")
    private final Pattern pattern = Pattern.compile("^(.*)$", Pattern.MULTILINE); // line

    public Setting(final String key, final String defaultValue, final String desc){
        this.key  = key;
        this.def  = defaultValue;
        this.desc = String.format("%s\nDefault: %s", desc, defaultValue);
        this.yaml = String.format("%s\n%s: %s", pattern.matcher(this.desc).replaceAll("# $1"), key, defaultValue);
    }

    public final String getKey(){
        return key;
    }

    public final String getDefaultValue(){
        return def;
    }

    public final String getDesc(){
        return desc;
    }

    public final String getYaml(){
        return yaml;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("key", key)
            .addObject("defaultValue", def)
            .addObject("desc", desc)
            .addObject("yaml", yaml)
            .toString();
    }

}
