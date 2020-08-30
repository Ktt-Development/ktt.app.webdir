package com.kttdevelopment.webdir.client.renderer;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.core.classes.ToStringBuilder;
import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.config.ConfigurationFile;
import com.kttdevelopment.webdir.client.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.client.function.SymbolicStringMatcher;
import com.kttdevelopment.webdir.client.object.Tuple2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

public final class DefaultFrontMatterLoader {

    // <scope,config>
    private final List<Tuple2<List<String>,ConfigurationSection>> defaultConfigurations = new ArrayList<>();

    private final File defaults, sources;

    private final LocaleService locale;
    private final Logger logger;

    public DefaultFrontMatterLoader(final File defaults, final File sources){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pageRenderer"));

        this.defaults = Objects.requireNonNull(defaults);
        this.sources  = Objects.requireNonNull(sources);

        for(final File file : Objects.requireNonNullElse(defaults.listFiles(File::isFile),new File[0])){
            logger.finer(locale.getString("pageRenderer.default.loadDefault",file));
            try{
                final ConfigurationFile config = new ConfigurationFile();
                config.load(file);
                if(config.contains("default"))
                    defaultConfigurations.add(new Tuple2<>(config.get("default").getList("scope",new ArrayList<>()),config));
            }catch(final FileNotFoundException e){
                logger.severe(locale.getString("pageRenderer.default.fileNotFound",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(ClassCastException | YamlException e){
                logger.severe(locale.getString("pageRenderer.default.invalidSyntax",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }
        }
    }

    //

    public final ConfigurationSection getDefaultFrontMatter(final File file){
        return getDefaultFrontMatter(
            ContextUtil.getContext(
                sources.getAbsoluteFile().toPath().relativize(
                    file.getAbsoluteFile().toPath()).toString(),true,false));
    }

    public final ConfigurationSection getDefaultFrontMatter(final String context){
        final String path = ContextUtil.getContext(context,true,false);

        logger.finest(locale.getString("pageRenderer.default.path"));

        final List<ConfigurationSection> configs = new ArrayList<>();
        for(final Tuple2<List<String>,ConfigurationSection> tuple : defaultConfigurations){
            boolean canUseConfig = false;
            for(final String scope : tuple.getVar1())
                switch(SymbolicStringMatcher.matches(scope,path)){
                    case MATCH:
                        canUseConfig = true;
                        continue;
                    case NEGATIVE_MATCH:
                        canUseConfig = false;
                        break;
                }

            if(canUseConfig) configs.add(tuple.getVar2());
        }

        // sort so lower indexes are at the top (see next)
        configs.sort( // the constructor asserts that a valid map 'default' exists, #getInteger with default is safe
            // it is already asserted that each config contains a valid 'default'
            Comparator.comparingInt(o -> o.get("default")
               .getInteger("index", 0))
        );

        logger.finest(locale.getString("pageRenderer.default.configs",configs,context));

        // lower configs are set first so higher ones override
        final ConfigurationSection def = new ConfigurationSectionImpl();
        configs.forEach(def::setDefault);

        final Map<?,?> map = def.toMapWithDefaults();
        map.remove("default");

        final ConfigurationSection config = new ConfigurationSectionImpl(map);
        return config.toMap().isEmpty() ? null : config;
    }

    //

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("defaultConfigurations",defaultConfigurations)
            .addObject("defaults",defaults)
            .addObject("sources",sources)
            .toString();
    }

}
