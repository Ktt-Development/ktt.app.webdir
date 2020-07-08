package com.kttdevelopment.webdir.generator.render;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.Main;
import com.kttdevelopment.webdir.generator.config.ConfigurationFileImpl;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public abstract class YamlFrontMatter {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean hasFrontMatter();

    public abstract ConfigurationSection getFrontMatter();

    public abstract String getFrontMatterAsString();

    public abstract String getContent();

    // imports

    public static ConfigurationSection loadImports(final ConfigurationSection frontMatter){
        return loadImports("import",null,frontMatter);
    }

    public static ConfigurationSection loadRelativeImports(final File source, final ConfigurationSection frontMatter){
       return loadImports("importRelative",source,frontMatter);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ConfigurationSection loadImports(final String key, final File source, final ConfigurationSection frontMatter){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Logger.getLogger(locale.getString("pageRenderer"));

        final List<String> imports = frontMatter.getList(key, String.class);
        final Map OUT = new HashMap();
        imports.forEach(s -> {
            final File IN = Paths.get(Objects.requireNonNullElse(source,new File("")).getAbsolutePath(),s).toFile();
            try{
                final ConfigurationFileImpl impl = new ConfigurationFileImpl(IN);
                impl.load(IN);
                OUT.putAll(impl.toMap());
            }catch(final FileNotFoundException ignored){
                logger.warning(locale.getString("pageRenderer.yfm.notFound",IN.getAbsolutePath()));
            }catch(final ClassCastException |  YamlException e){
                logger.warning(locale.getString("pageRenderer.yfm.badYMLSyntax",IN.getAbsolutePath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });
        OUT.putAll(frontMatter.toMap());
        return new ConfigurationSectionImpl(OUT);
    }

    // renderer

    @SuppressWarnings("rawtypes")
    public static List<Renderer> getRenderers(List renderers){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Logger.getLogger(locale.getString("pageRenderer"));

        final List<PluginRendererEntry> installedRenderers = Main.getPluginLoader().getRenderers();
        final List<Renderer> out = new ArrayList<>();

        for(final Object obj : renderers){
            PluginRenderer renderer = null;
            if(obj instanceof String){
                renderer = new PluginRenderer(null, obj.toString());
            }else if(obj instanceof Map){
                final Map map = (Map) obj;
                try{
                    renderer = new PluginRenderer(
                        Objects.requireNonNull(map.get("plugin")).toString(),
                        Objects.requireNonNull(map.get("renderer")).toString()
                    );
                }catch(final NullPointerException ignored){
                    logger.warning(locale.getString("pageRenderer.rdr.missingKV",obj));
                    continue;
                }
            }

            // compiler fails to recognize it will always be initialized here
            if(renderer == null) continue;

            for(final PluginRendererEntry entry : installedRenderers){
                if(
                    (renderer.getPluginName() == null &&
                     renderer.getRendererName().equals(entry.getRendererName())) ||
                    (renderer.getPluginName() != null &&
                     renderer.getPluginName().equals(entry.getPluginName()) &&
                     renderer.getRendererName().equals(entry.getRendererName()))
                ){
                    out.add(entry.getRenderer());
                    break;
                }
            }
        }
        return Collections.unmodifiableList(out);
    }

}
