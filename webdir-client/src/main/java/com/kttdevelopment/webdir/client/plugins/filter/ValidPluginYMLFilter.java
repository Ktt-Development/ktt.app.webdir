package com.kttdevelopment.webdir.client.plugins.filter;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.api.PluginYml;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.client.function.IOFilter;
import com.kttdevelopment.webdir.client.plugins.PluginYmlImpl;

import java.io.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class ValidPluginYMLFilter implements IOFilter<Map<File,URL>,Map<File,PluginYml>> {

    private final LocaleService locale;
    private final Logger logger;

    public ValidPluginYMLFilter(){
        locale = Main.getLocaleService();
        logger = Main.getLoggerService().getLogger(locale.getString("pluginLoader"));
    }

    @Override
    public final Map<File,PluginYml> filter(final Map<File, URL> in){
        final Map<File,PluginYml> map = new LinkedHashMap<>();
        // remove any with missing keys
        in.forEach((file,url) -> {
            YamlReader IN = null;
            try{
                IN = new YamlReader(new InputStreamReader(url.openStream()));
                map.put(
                        file,
                        new PluginYmlImpl(new ConfigurationSectionImpl((Map<?, ?>) IN.read()))
                );
                logger.finest(locale.getString("pluginLoader.validYMLFilter.validFile", file));
            }catch(final ClassCastException | YamlException e){
                logger.severe(locale.getString("pluginLoader.validYMLFilter.invalidPluginYMLSyntax", file) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final NullPointerException e){
                logger.severe(locale.getString("pluginLoader.validYMLFilter.missingRequired",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }catch(final IOException e){
                logger.severe(locale.getString("pluginLoader.validYMLFilter.failedRead",file) + '\n' + LoggerService.getStackTraceAsString(e));
            }finally{
                if(IN != null)
                    try{
                        IN.close();
                    }catch(final IOException e){
                        logger.warning(locale.getString("pluginLoader.validYMLFilter.failedClose",file) + '\n' + LoggerService.getStackTraceAsString(e));
                    }
            }
        });
        return map;
    }

}
