package com.kttdevelopment.webdir.client.renderer;

import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.client.LoggerService;
import com.kttdevelopment.webdir.client.Main;
import com.kttdevelopment.webdir.client.utility.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class DefaultFrontMatterLoader {

    private static final String
        DEFAULT = "default",
        INDEX   = "index",
        SCOPE   = "scope",
        FILE    = "file";

    // List<String?boolean>
    private final Map<List<? super Object>,Map<String,? super Object>> defaultConfigurations = new HashMap<>();

    private final File defaults, sources, output;
    private final String sabs, oabs;
    private final Path sath, oath;

    public DefaultFrontMatterLoader(final File defaults, final File sources, final File output){
        this.defaults = defaults;
        this.sources  = sources;
        this.output   = output;

        this.sabs = ExceptionUtility.requireNonExceptionElse(sources::getCanonicalPath, sources.getAbsoluteFile().getAbsolutePath());
        this.oabs = ExceptionUtility.requireNonExceptionElse(output::getCanonicalPath, output.getAbsoluteFile().getAbsolutePath());
        this.sath = ExceptionUtility.requireNonExceptionElse(() -> sources.getCanonicalFile().toPath(), sources.getAbsoluteFile().toPath());
        this.oath = ExceptionUtility.requireNonExceptionElse(() -> output.getCanonicalFile().toPath(), output.getAbsoluteFile().toPath());

        for(final File file : Objects.requireNonNullElse(defaults.listFiles(File::isFile), new File[0])){
            try(final FileInputStream IN = new FileInputStream(file)){
                final Map<String,? super Object> map = MapUtility.asStringObjectMap(new Yaml().load(IN));
                final List<? super Object> scopes = new ArrayList<>();
                if(map.containsKey(DEFAULT) && ((Map<?,?>) map.get(DEFAULT)).containsKey(SCOPE) && ((Map<?,?>) map.get(DEFAULT)).get(SCOPE) instanceof List){
                    final List<?> list = (List<?>) ((Map<?,?>) map.get(DEFAULT)).get(SCOPE);
                    for(final Object iterator : list){
                        if(iterator instanceof Map && ((Map<?, ?>) iterator).containsKey(FILE)){
                            scopes.add(Boolean.parseBoolean(((Map<?, ?>) iterator).get(FILE).toString()));
                        }else{
                            final String s = iterator.toString();
                            scopes.add((s.startsWith("!") ?  "!" : "") + ContextUtil.getContext(s.startsWith("!") ? s.substring(1) : s, true, false));
                        }
                    }
                    defaultConfigurations.put(scopes, map);
                }else
                    Main.getLogger(Main.getLocale().getString("page-renderer.name")).warning(Main.getLocale().getString("page-renderer.default.scope", file.getPath()));
            }catch(final FileNotFoundException e){
                Main.getLogger(Main.getLocale().getString("page-renderer.name")).severe(Main.getLocale().getString("page-renderer.default.missing", file.getPath()) + LoggerService.getStackTraceAsString(e));
            }catch(final ClassCastException | YAMLException | IOException e){
                Main.getLogger(Main.getLocale().getString("page-renderer.name")).warning(Main.getLocale().getString("page-renderer.default.read", file.getPath()) + LoggerService.getStackTraceAsString(e));
            }
        }

    }

    // if online then fromOutput is true
    public final Map<String,? super Object> getDefaultFrontMatter(final File file){
        Objects.requireNonNull(file);
        final String path = ExceptionUtility.requireNonExceptionElse(file::getCanonicalPath, file.getAbsoluteFile().getAbsolutePath());
        return getDefaultFrontMatter(
            ContextUtil.getContext(
                path.startsWith(sabs) || path.startsWith(oabs)
                ? (path.startsWith(sabs) ? sath : oath).relativize(ExceptionUtility.requireNonExceptionElse(() -> file.getCanonicalFile().toPath(), file.getAbsoluteFile().toPath())).toString()
                : path,
                true,
                false
            ),
            file
        );
    }

    public final Map<String,? super Object> getDefaultFrontMatter(final String context){
        return getDefaultFrontMatter(context, null);
    }

    public final Map<String,? super Object> getDefaultFrontMatter(final String context, final File file){
        final String path = ContextUtil.getContext(context, true, false);

        // find scope matched configs
        final List<Map<String,? super Object>> configs = new ArrayList<>();
        defaultConfigurations.forEach((scopes, config) -> {
            boolean canUse = false;
            for(final Object scope : scopes)
                if(scope instanceof String) // constructor asserts its either a String or boolean
                    switch(SymbolicStringMatcher.matches((String) scope, path)){
                        case MATCH:
                            canUse = true;
                            continue;
                        case NEGATIVE_MATCH:
                            canUse = false;
                            break;
                    }
                else if(file != null && (boolean) scope != file.isFile()){ // if scope file does not match file state
                    canUse = false;
                    break;
                }

            if(canUse)
                configs.add(config);
        });

        // sort so lower indexes are at the top (see next)
        configs.sort(Comparator.comparingInt(map -> {
            try{
                //noinspection rawtypes
                return Integer.parseInt(((Map) map.get(DEFAULT)).get(INDEX).toString());
            }catch(final ClassCastException | NullPointerException ignored){ // field not required, def -1
            }catch(final NumberFormatException e){
                Main.getLogger(Main.getLocale().getString("page-renderer.name")).warning(Main.getLocale().getString("page-renderer.default.index", context) + LoggerService.getStackTraceAsString(e));
            }
            return -1;
        }));

        // lower configs set first so higher can override
        final Map<String,? super Object> config = new HashMap<>();
        for(final Map<String,? super Object> map : configs)
            config.putAll(map);
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
