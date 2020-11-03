package com.kttdevelopment.webdir.client.renderer;

import com.amihaiemil.eoyaml.*;
import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.client.LoggerService;
import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.utility.*;

import java.io.*;
import java.util.*;

public class DefaultFrontMatterLoader {

    private static final String
        DEFAULT = "default",
        INDEX   = "index",
        SCOPE   = "scope";

    private final Map<List<String>,YamlMapping> defaultConfigurations = new HashMap<>();

    private final File defaults, sources, output;

    public DefaultFrontMatterLoader(final File defaults, final File sources, final File output){
        this.defaults = defaults;
        this.sources = sources;
        this.output = output;

        for(final File file : Objects.requireNonNullElse(defaults.listFiles(File::isFile), new File[0])){
            try{
                final YamlMapping map = Yaml.createYamlInput(file).readYamlMapping();
                final List<String> scopes = new ArrayList<>();
                if(
                    YamlUtility.containsKey(SCOPE, map.yamlMapping(DEFAULT)) &&
                    map.yamlMapping(DEFAULT).value(SCOPE).type() == Node.SEQUENCE
                ){
                    for(final YamlNode yamlNode : map.yamlMapping(DEFAULT).yamlSequence(SCOPE)){
                        final String s = YamlUtility.asString(yamlNode);
                        if(s != null)
                            scopes.add(s);
                    }
                    defaultConfigurations.put(scopes, map);
                }else
                    Main.getLogger(Main.getLocale().getString("page-renderer.name")).warning(Main.getLocale().getString("page-renderer.default.scope", file.getPath()));
            }catch(final FileNotFoundException e){
                Main.getLogger(Main.getLocale().getString("page-renderer.name")).severe(Main.getLocale().getString("page-renderer.default.missing", file.getPath()) + LoggerService.getStackTraceAsString(e));
            }catch(final IOException e){
                Main.getLogger(Main.getLocale().getString("page-renderer.name")).warning(Main.getLocale().getString("page-renderer.default.read", file.getPath()) + LoggerService.getStackTraceAsString(e));
            }
        }

    }

    // if online then fromOutput is true
    public final Map<String,? super Object> getDefaultFrontMatter(final File file, final boolean fromOutput){
        Objects.requireNonNull(file);
        return getDefaultFrontMatter(
            ContextUtil.getContext((!fromOutput ? sources : output).getAbsoluteFile().toPath().relativize(
                file.getAbsoluteFile().toPath()).toString(), true, false
            )
        );
    }

    public final Map<String,? super Object> getDefaultFrontMatter(final String context){
        final String path = ContextUtil.getContext(context, true, false);

        // find scope matched configs
        final List<YamlMapping> configs = new ArrayList<>();
        defaultConfigurations.forEach((scopes, config) -> {
            boolean canUse = false;
            for(final String scope : scopes)
                switch(SymbolicStringMatcher.matches(scope, path)){
                    case MATCH:
                        canUse = true;
                        continue;
                    case NEGATIVE_MATCH:
                        canUse = false;
                        break;
                }
            if(canUse)
                configs.add(config);
        });

        // sort so lower indexes are at the top (see next)
        configs.sort(Comparator.comparingInt(map -> {
            try{
                return map.yamlMapping(DEFAULT).integer(INDEX);
            }catch(final NullPointerException ignored){ // field not required, def -1
            }catch(final NumberFormatException e){
                Main.getLogger(Main.getLocale().getString("page-renderer.name")).warning(Main.getLocale().getString("page-renderer.default.index", context) + LoggerService.getStackTraceAsString(e));
            }
            return -1;
        }));

        // lower configs set first so higher can override
        final Map<String,? super Object> config = new HashMap<>();
        for(final YamlMapping map : configs)
            config.putAll(YamlUtility.asMap(map));
        config.remove(DEFAULT); // remove default key

        return config.isEmpty() ? null : config;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(getClass().getSimpleName())
            .addObject("defaultConfigurations", defaultConfigurations)
            .addObject("defaults", defaults)
            .addObject("sources", sources)
            .addObject("output", output)
            .toString();
    }

}
