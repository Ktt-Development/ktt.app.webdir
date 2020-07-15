package com.kttdevelopment.webdir.generator.render;

import com.esotericsoftware.yamlbeans.YamlException;
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
import java.util.regex.Pattern;

public abstract class YamlFrontMatter {

    public abstract boolean hasFrontMatter();

    public abstract ConfigurationSection getFrontMatter();

    public abstract String getFrontMatterAsString();

    public abstract String getContent();

    // Global Settings //

    private static final String importKey = "import", importRelativeKey = "import_relative";

    //

    // imports

    public static ConfigurationSection loadImports(final ConfigurationSection frontMatter){
        return loadImports("import",null,frontMatter);
    }

    public static ConfigurationSection loadRelativeImports(final File source, final ConfigurationSection frontMatter){
       return loadImports("import_relative",source,frontMatter);
    }

    private static final Pattern pattern = Pattern.compile("^(.*)\\.(.*)$");

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ConfigurationSection loadImports(final String key, final File source, final ConfigurationSection frontMatter){
        final LocaleService locale = !Main.testMode ? Main.getLocaleService() : null;
        final Logger logger = !Main.testMode ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        final List<String> imports =frontMatter.getList(key, String.class),new ArrayList<>();

        if(imports == null || imports.isEmpty())
            return new ConfigurationSectionImpl(frontMatter.toMap());

        Collections.reverse(imports);

        final Map OUT = new HashMap();

        imports.forEach(s -> {
            // if does not end with an extension, assume it to be a yaml file
            final String fileName = s + (pattern.matcher(s).matches() ? "" : ".yml");
            final File IN = Paths.get((source == null ? new File("") : source.getParentFile()).getAbsolutePath(),fileName).toFile();
            try{
                final ConfigurationFileImpl impl = new ConfigurationFileImpl(IN);
                impl.load(IN);

                // imported files may also have imports as well
                final Map innerImports = new HashMap();

                if(impl.contains(importKey))
                    innerImports.putAll(loadImports(impl).toMap());
                if(impl.contains(importRelativeKey))
                    innerImports.putAll(loadRelativeImports(IN,impl).toMap());

                final Map map = impl.toMap();
                innerImports.putAll(map);
                innerImports.remove(importKey);
                innerImports.remove(importRelativeKey);

                OUT.putAll(innerImports);
            }catch(final FileNotFoundException ignored){
                if(!Main.testMode)
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.yfm.notFound",IN.getAbsolutePath()));
            }catch(final ClassCastException |  YamlException e){
                if(!Main.testMode)
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.yfm.badYMLSyntax",IN.getAbsolutePath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });
        OUT.putAll(frontMatter.toMap());
        return new ConfigurationSectionImpl(OUT);
    }

    // renderer

    @SuppressWarnings("rawtypes")
    public static List<PluginRendererEntry> getRenderers(List renderers){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        final List<PluginRendererEntry> installedRenderers = Main.getPluginLoader().getRenderers();
        final List<PluginRendererEntry> out = new ArrayList<>();

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
                    out.add(entry);
                    break;
                }
            }
        }
        return Collections.unmodifiableList(out);
    }

}
