package com.kttdevelopment.webdir.generator.render;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.*;
import com.kttdevelopment.webdir.generator.config.ConfigurationFile;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.object.Tuple2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DefaultFrontMatterLoader {

    private final List<Tuple2<List<String>,ConfigurationSection>> defaultConfigurations = new ArrayList<>();

    private final File sourcesDir;

    public DefaultFrontMatterLoader(final File defaultDir, final File sourcesDir){
        final LocaleService locale  = !Vars.Test.testmode ? Main.getLocaleService() : null;
        final Logger logger         = !Vars.Test.testmode ? Main.getLoggerService().getLogger(locale.getString("pageRenderer")) : Logger.getLogger("Page Renderer");

        this.sourcesDir = sourcesDir;
        for(final File file : Objects.requireNonNullElse(defaultDir.listFiles(File::isFile),new File[0])){
            try{
                final ConfigurationFile config = new ConfigurationFile();
                config.load(file);

                try{
                    defaultConfigurations.add(new Tuple2<>(
                        Objects.requireNonNull(config.get(Vars.Renderer.Default.defaultKey)).getList(Vars.Renderer.Default.scopeKey, new ArrayList<>()),
                        config
                    ));
                }catch(final NullPointerException ignored){
                    if(!Vars.Test.testmode)
                        // IntelliJ defect; locale will not be null while not in test mode
                        //noinspection ConstantConditions
                        logger.warning(locale.getString("pageRenderer.default.missingDefault", file.getPath()));
                }catch(final ClassCastException ignored){
                    if(!Vars.Test.testmode)
                        // IntelliJ defect; locale will not be null while not in test mode
                        //noinspection ConstantConditions
                        logger.warning(locale.getString("pageRenderer.default.invalidDefaultType", file.getPath()));
                }
            }catch(final FileNotFoundException ignored){
                if(!Vars.Test.testmode)
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.default.noDefaultFile", file.getPath()));
            }catch(final ClassCastException | YamlException e){
                if(!Vars.Test.testmode)
                    // IntelliJ defect; locale will not be null while not in test mode
                    //noinspection ConstantConditions
                    logger.warning(locale.getString("pageRenderer.default.malformedYML", file.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        }
    }

    public final ConfigurationSection getDefaultFrontMatter(final File file){
        final String path = ContextUtil.getContext(sourcesDir.getAbsoluteFile().toPath().relativize(file.getAbsoluteFile().toPath()).toString(),true,false);
        final List<ConfigurationSection> configs = new ArrayList<>();

        defaultConfigurations.forEach((tuple) -> {
            boolean canUseConfig = false;
            for(final String string : tuple.getVar1()){
                final boolean negative = !string.isEmpty() && string.charAt(0) == '!';
                final String context = ContextUtil.getContext(negative ? string.substring(1) : string,true,false);

                // make string literal but replace '*' with '.*' for regex
                final String regex = "^\\Q" + context.replace("*","\\E.*\\Q") + "\\E$";

                final Matcher matcher = Pattern.compile(regex).matcher(path);
                if(matcher.matches()){
                    canUseConfig = !negative;
                    if(negative) // negative ('!') overrides true state
                        break;
                }
            }
            if(canUseConfig) configs.add(tuple.getVar2());
        });

        // sort so lower indexes are at the top (see next)
        configs.sort( // the constructor asserts that a valid map 'default' exists, #getInteger with default is safe
            Comparator.comparingInt(o -> Objects.requireNonNull(o.get(Vars.Renderer.Default.defaultKey))
               .getInteger(Vars.Renderer.Default.indexKey, Vars.Renderer.Default.defaultIndex)));

        // populate configuration by lower index first so higher ones override
        final ConfigurationSection config = new ConfigurationSectionImpl();
        configs.forEach(config::setDefault);
        return config.toMap().isEmpty() ? null : config;
    }

}
