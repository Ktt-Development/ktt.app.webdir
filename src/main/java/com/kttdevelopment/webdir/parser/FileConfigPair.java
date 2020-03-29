package com.kttdevelopment.webdir.parser;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.main.Locale;
import com.kttdevelopment.webdir.main.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.kttdevelopment.webdir.main.Logger.*;

public class FileConfigPair {

    private final File file;
    private final File config;

    public FileConfigPair(final File file){
        this.file = file;
        config = new File(FilenameUtils.removeExtension(file.getName()) + "yml");
    }

    public final boolean exists(){
        return file.exists();
    }

    public final boolean hasConfig(){
        return config.exists();
    }

    public final File getFile(){
        return file;
    }

    public final File getConfig(){
        return config;
    }

    public final String readFile(){
        try{
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        }catch(IOException e){
            e.printStackTrace();
            logger.severe(String.format(Locale.getString("page.failedRead"), file.getName()) + '\n' + Logger.getStackTraceAsString(e));
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public final Map readConfig(){
        try{
            final YamlReader IN = new YamlReader(new FileReader(config));
            return (Map) IN.read();
        }catch(final FileNotFoundException e){
            logger.severe(String.format(Locale.getString("page.configNotFound"), file.getName()));
        }catch(final YamlException e){
            logger.severe(String.format(Locale.getString("page.configMalformed"), file.getName()) + '\n' + Logger.getStackTraceAsString(e));
        }
        return null;
    }

}
