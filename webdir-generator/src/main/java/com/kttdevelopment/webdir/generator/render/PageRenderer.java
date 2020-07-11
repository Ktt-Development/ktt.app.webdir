package com.kttdevelopment.webdir.generator.render;

import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

public final class PageRenderer implements BiFunction<File,byte[],byte[]> {

    // todo: skip bad renders and warn to logger

    @Override
    public final byte[] apply(final File file, final byte[] bytes){
        final String str = new String(bytes);
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(str).read();

        if(!frontMatter.hasFrontMatter()) return bytes;

        ConfigurationSection tFrontMatter = frontMatter.getFrontMatter();
        tFrontMatter = YamlFrontMatter.loadImports(tFrontMatter);
        tFrontMatter = YamlFrontMatter.loadRelativeImports(file,tFrontMatter);
        final ConfigurationSection finalFrontMatter = tFrontMatter;

        final List<Renderer> renderers = YamlFrontMatter.getRenderers(finalFrontMatter.getList("renderers"));

        final AtomicReference<String> content = new AtomicReference<>(str);
        renderers.forEach(renderer -> content.set(renderer.format(file, finalFrontMatter, content.get())));

        return content.get().getBytes();
    }

}
