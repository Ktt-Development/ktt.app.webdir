package com.kttdevelopment.webdir.generator.render;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.config.ConfigurationFile;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
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

    private static final Pattern hasExtension = Pattern.compile("^(.*)\\.(.*)$");

    //

    // load imports via config → loads exact only
    public static ConfigurationSection loadImports(final ConfigurationSection config){
        return loadImports(null,config,new ArrayList<>());
    }

    public static ConfigurationSection loadImports(final File file){
        return loadImports(file,new ArrayList<>());
    }

    // load imports via file → loads both, may be empty if bad file
    public static ConfigurationSection loadImports(final File file, final List<File> checkedImports){
        final ILocaleService locale  = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        final ConfigurationFile config = new ConfigurationFile();
        try{
            config.load(file);
            return loadImports(file,config, checkedImports);
        }catch(final FileNotFoundException ignored){
            logger.warning(locale.getString("pageRenderer.yamlFrontMatter.getImports.notFound", file.getAbsolutePath()));
        }catch(final ClassCastException |  YamlException e){
            logger.warning(locale.getString("pageRenderer.yamlFrontMatter.getImports.malformedYML", file.getAbsolutePath()) + '\n' + Exceptions.getStackTraceAsString(e));
        }
        return new ConfigurationSectionImpl();
    }

    // load imports via file and config → loads both safe
    public static ConfigurationSection loadImports(final File file, final ConfigurationSection config){
        return loadImports(file,config,new ArrayList<>());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static ConfigurationSection loadImports(final File source, final ConfigurationSection config, final List<File> checkedImports){
        final ILocaleService locale = Vars.Main.getLocaleService() ;
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        final String sourceABS = Exceptions.requireNonExceptionElse(source::getAbsolutePath,"null");

        logger.finest(locale.getString("pageRenderer.debug.yamlFrontMatter.getImports",sourceABS,config,checkedImports));

        // reverse lists so top imports#putAll will override lower imports
        final List<String> imports = config.getList(Vars.Renderer.importKey, new ArrayList<>());
        Collections.reverse(imports);
        final List<String> relativeImports = config.getList(Vars.Renderer.importRelativeKey, new ArrayList<>());
        Collections.reverse(relativeImports);

        if(imports.isEmpty() && relativeImports.isEmpty())
            return config;

        final Map out = new HashMap<>();

        final List<List<String>> repeat = List.of(imports,relativeImports);

        repeat.forEach(list -> list.forEach(s -> {
            // if has no extension assume .yml
            final String fileName = ContextUtil.getContext(s + (hasExtension.matcher(s).matches() ? "" : ".yml"),true,false);
            //noinspection ConstantConditions // the assertion that its true is actually false; issued caused by nonExceptionElse
            final File IN = Paths.get((source != null && list == relativeImports ? source.getParentFile() : new File("")).getAbsolutePath(),fileName).toFile();

            logger.finest(locale.getString("pageRenderer.debug.yamlFrontMatter.getImports.load",fileName,IN,sourceABS));

            if(!checkedImports.contains(IN)){ // only apply imports if not already done so (circular import prevention)
                checkedImports.add(IN);
                final Map imported = loadImports(IN,checkedImports).toMap();
                imported.remove(Vars.Renderer.importKey);
                imported.remove(Vars.Renderer.importRelativeKey);
                out.putAll(imported);
            }else{
                logger.warning(locale.getString("pageRenderer.yamlFrontMatter.getImports.circularImport", IN.getPath()));
            }
        }));

        out.putAll(config.toMap());
        return new ConfigurationSectionImpl(out);
    }

    // renderer

    @SuppressWarnings("rawtypes")
    public static List<PluginRendererEntry> getRenderers(final String renderKey, final List renderers){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        final List<PluginRendererEntry> installedRenderers = Vars.Main.getPluginLoader().getRenderers();
        final List<PluginRendererEntry> out                = new ArrayList<>();

        logger.finest(locale.getString("pageRenderer.debug.yamlFrontMatter.getRenderers",renderers));

        for(final Object obj : renderers){
            logger.fine(locale.getString("pageRenderer.debug.yamlFrontMatter.getRenderers.map",obj));
            PluginRendererEntry renderer = null;
            if(obj instanceof String){
                renderer = new PluginRendererEntry(null, obj.toString(),null);
            }else if(obj instanceof Map){
                final Map map = (Map) obj;
                try{
                    renderer = new PluginRendererEntry(
                        Objects.requireNonNull(map.get(Vars.Renderer.pluginKey)).toString(),
                        Objects.requireNonNull(map.get(renderKey)).toString(),
                        null
                    );
                }catch(final NullPointerException ignored){
                    logger.warning(locale.getString("pageRenderer.yamlFrontMatter.getRenderers.missingKV", obj));
                    continue;
                }
            }

            // compiler fails to recognize it will always be initialized here
            if(renderer == null) continue;

            for(final PluginRendererEntry entry : installedRenderers){
                logger.finest(locale.getString("pageRenderer.debug.yamlFrontMatter.getRenderers.match",renderer,entry));
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

    //

    @Override
    public boolean equals(final Object obj){
        if(this == obj)
            return true;
        else if(!(obj instanceof YamlFrontMatter))
            return false;
        final YamlFrontMatter other = ((YamlFrontMatter) obj);
        return other.hasFrontMatter() == hasFrontMatter() &&
               other.getFrontMatter().equals(getFrontMatter()) &&
               other.getFrontMatterAsString().equals(getFrontMatterAsString()) &&
               other.getContent().equals(getContent());
    }

    @Override
    public String toString(){
        return new toStringBuilder("YamlFrontMatter")
            .addObject("hasExtensionRegex",hasExtension.pattern())
            .addObject("hasFrontMatter",hasFrontMatter())
            .addObject("frontMatter",getFrontMatter())
            .addObject("frontMatterString",getFrontMatterAsString())
            .addObject("content",getContent())
            .toString();
    }

}
