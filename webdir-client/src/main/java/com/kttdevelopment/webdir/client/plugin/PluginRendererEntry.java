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

package com.kttdevelopment.webdir.client.plugin;

import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.client.utility.ToStringBuilder;

public class PluginRendererEntry {

    private final String pluginName, rendererName;
    private final Renderer renderer;

    public PluginRendererEntry(final String pluginName, final String rendererName, final Renderer renderer){
        this.pluginName   = pluginName;
        this.rendererName = rendererName;
        this.renderer     = renderer;
    }

    public final String getPluginName(){
        return pluginName;
    }

    public final String getRendererName(){
        return rendererName;
    }

    public final Renderer getRenderer(){
        return renderer;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("pluginName", pluginName)
            .addObject("rendererName", rendererName)
            .addObject("renderer", renderer)
            .toString();
    }

}
