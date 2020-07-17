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

    private static final Pattern hasExtension = Pattern.compile("^(.*)\\.(.*)$");

    //

    public static ConfigurationSection loadImports(final File file){
        return loadImports(file,new ArrayList<>(),new ArrayList<>());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ConfigurationSection loadImports(final File file, final List<File> checkedImports, final List<File> checkedRelativeImports){
        final LocaleService locale  = !Main.testMode ? Main.getLocaleService() : null;
        final Logger logger         = !Main.testMode ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        try{
            final ConfigurationFileImpl impl = new ConfigurationFileImpl(file);
            impl.load(file);

            // reverse lists so top imports#putAll will override lower imports
            final List<String> imports = impl.getList(importKey, new ArrayList<>());
            Collections.reverse(imports);
            final List<String> relativeImports = impl.getList(importRelativeKey, new ArrayList<>());
            Collections.reverse(relativeImports);

            if(imports.isEmpty() && relativeImports.isEmpty())
                return impl;

            final Map out = new HashMap<>();

            imports.forEach(s -> {
                // if has no extension assume .yml
                final String fileName = s + (hasExtension.matcher(s).matches() ? "" : ".yml");
                final File IN = Paths.get(new File("").getAbsolutePath(),fileName).toFile();

                if(!checkedImports.contains(IN)){ // only apply imports if not already done so (circular import prevention)
                    checkedImports.add(IN);
                    final ConfigurationSection imported = loadImports(IN,checkedImports,new ArrayList<>());
                    final Map importedMap = imported != null ? imported.toMap() : new HashMap();
                    importedMap.remove(importKey);
                    importedMap.remove(importRelativeKey);
                    out.putAll(importedMap);
                }else if(!Main.testMode){
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.yfm.duplImport",IN.getPath()));
                }
            });

            // relative imports will override exact imports
            relativeImports.forEach(s -> {
                // if has no extension assume .yml
                final String fileName = s + (hasExtension.matcher(s).matches() ? "" : ".yml");
                final File IN = Paths.get(new File("").getAbsolutePath(),fileName).toFile();

                if(!checkedRelativeImports.contains(IN)){ // only apply imports if not already done so (circular import prevention)
                    checkedRelativeImports.add(IN);
                    final ConfigurationSection imported = loadImports(IN,new ArrayList<>(),checkedRelativeImports);
                    final Map importedMap = imported != null ? imported.toMap() : new HashMap();
                    importedMap.remove(importKey);
                    importedMap.remove(importRelativeKey);
                    out.putAll(importedMap);
                }else if(!Main.testMode){
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.yfm.duplImport",IN.getPath()));
                }
            });

            out.putAll(impl.toMap());
            return new ConfigurationSectionImpl(out);
        }catch(final FileNotFoundException ignored){
            if(!Main.testMode)
                // IntelliJ defect; locale will not be null while not in test mode
                //noinspection ConstantConditions
                logger.warning(locale.getString("pageRenderer.yfm.notFound",file.getAbsolutePath()));
        }catch(final ClassCastException | YamlException e){
            if(!Main.testMode)
                // IntelliJ defect; locale will not be null while not in test mode
                //noinspection ConstantConditions
                logger.warning(locale.getString("pageRenderer.yfm.badYMLSyntax",file.getAbsolutePath()) + '\n' + Exceptions.getStackTraceAsString(e));
        }
        return new ConfigurationSectionImpl();
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
