package com.kttdevelopment.webdir.generator.render;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.config.ConfigurationFile;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.*;
import com.kttdevelopment.webdir.generator.object.Tuple2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

public final class DefaultFrontMatterLoader {

    private final List<Tuple2<List<String>,ConfigurationSection>> defaultConfigurations = new ArrayList<>();

    private final File sourcesDir;

    public DefaultFrontMatterLoader(final File defaultDir, final File sourcesDir){
        final LocaleService locale  = Main.getLocaleService();
        final Logger logger         = Main.getLoggerService() != null ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        if(locale != null)
            logger.fine(locale.getString("pageRenderer.debug.default.dir",defaultDir.getAbsolutePath(),sourcesDir.getAbsolutePath()));

        this.sourcesDir = sourcesDir;
        for(final File file : Objects.requireNonNullElse(defaultDir.listFiles(File::isFile),new File[0])){
            if(locale != null)
                logger.finest(locale.getString("pageRenderer.debug.default.file",file.getAbsolutePath()));

            try{
                final ConfigurationFile config = new ConfigurationFile();
                config.load(file);

                try{
                    defaultConfigurations.add(new Tuple2<>(
                        Objects.requireNonNull(config.get(Vars.Renderer.Default.defaultKey)).getList(Vars.Renderer.Default.scopeKey, new ArrayList<>()),
                        config
                    ));
                }catch(final NullPointerException ignored){
                    if(locale != null)
                        logger.warning(locale.getString("pageRenderer.default.missingDefault", file.getPath()));
                }catch(final ClassCastException ignored){
                    if(locale != null)
                        logger.warning(locale.getString("pageRenderer.default.invalidDefaultType", file.getPath()));
                }
            }catch(final FileNotFoundException ignored){
                if(locale != null)
                    logger.warning(locale.getString("pageRenderer.default.noDefaultFile", file.getPath()));
            }catch(final ClassCastException | YamlException e){
                if(locale != null)
                    logger.warning(locale.getString("pageRenderer.default.malformedYML", file.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        }
        if(locale != null)
            logger.fine(locale.getString("pageRenderer.debug.default.loaded",defaultConfigurations.size(),Objects.requireNonNullElse(defaultDir.listFiles(File::isFile),new File[0])));
    }

    public final ConfigurationSection getDefaultFrontMatter(final File file){
        final LocaleService locale  = Main.getLocaleService();
        final Logger logger         = Main.getLoggerService() != null ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");
        final String fabs = file.getAbsolutePath();
        if(locale != null)
            logger.finest(locale.getString("pageRenderer.debug.default.getDefaultFrontMatter.file",fabs));

        final String path = ContextUtil.getContext(sourcesDir.getAbsoluteFile().toPath().relativize(file.getAbsoluteFile().toPath()).toString(),true,false);

        return getDefaultFrontMatter(path);
    }

    public final ConfigurationSection getDefaultFrontMatter(final String context){
        final LocaleService locale  = Main.getLocaleService();
        final Logger logger         = Main.getLoggerService() != null ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        final String path = ContextUtil.getContext(context,true,false);

        if(locale != null)
            logger.finest(locale.getString("pageRenderer.debug.default.getDefaultFrontMatter.path",path));

        final List<ConfigurationSection> configs = new ArrayList<>();

        // <scope,configuration?
        defaultConfigurations.forEach((tuple) -> {
            boolean canUseConfig = false;
            for(final String scope : tuple.getVar1()){
                switch(SymbolicStringMatcher.matches(scope,path)){
                    case MATCH:
                        canUseConfig = true;
                        continue;
                    case NEGATIVE_MATCH:
                        canUseConfig = false;
                        break;
                }
            }
            if(canUseConfig) configs.add(tuple.getVar2());
        });

        // sort so lower indexes are at the top (see next)
        configs.sort( // the constructor asserts that a valid map 'default' exists, #getInteger with default is safe
            Comparator.comparingInt(o -> Objects.requireNonNull(o.get(Vars.Renderer.Default.defaultKey))
               .getInteger(Vars.Renderer.Default.indexKey, Vars.Renderer.Default.defaultIndex)));

        if(locale != null)
            logger.finest(locale.getString("pageRenderer.debug.default.getDefaultFrontMatter.sort",path,configs));

        // populate configuration by lower index first so higher ones override
        final ConfigurationSection def = new ConfigurationSectionImpl();
        configs.forEach(def::setDefault);

        @SuppressWarnings("rawtypes")
        final Map map = def.toMapWithDefaults();
        map.remove(Vars.Renderer.Default.defaultKey);

        final ConfigurationSection config = new ConfigurationSectionImpl(map);
        return config.toMap().isEmpty() ? null : config;
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("DefaultFrontMatterLoader")
            .addObject("sourcesDir",sourcesDir.getAbsolutePath())
            .addObject("defaultConfiguration",defaultConfigurations)
            .toString();
    }

}
