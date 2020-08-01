package com.kttdevelopment.webdir.server;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.toStringBuilder;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
import com.kttdevelopment.webdir.server.permissions.Permissions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public final class PermissionsService {

    private final String permissionsFile, defaultPermissionsResource;
    private final Permissions permissions;

    //

    public final Permissions getPermissions(){
        return permissions;
    }

    //

    PermissionsService(final File permissionsFile, final String defaultPermissionsResource) throws YamlException{
        Objects.requireNonNull(permissionsFile);
        this.permissionsFile = permissionsFile.getAbsolutePath();
        this.defaultPermissionsResource = defaultPermissionsResource;

        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger        = locale != null ? Main.getLoggerService().getLogger(locale.getString("permissions")) : Logger.getLogger("permissions");

        if(locale != null)
            logger.info(locale.getString("permissions.const"));

    // load default
        final Permissions def;
        {
            if(locale != null)
                logger.fine(locale.getString("permissions.debug.const.defPerm",defaultPermissionsResource));
            YamlReader IN = null;
            try{
                IN = new YamlReader(new InputStreamReader(getClass().getResourceAsStream(Objects.requireNonNull(defaultPermissionsResource))));
                def = new Permissions((Map) IN.read());
            }catch(final NullPointerException e){
                if(locale != null)
                    logger.severe(locale.getString("permissions.const.default.missing"));
                throw e;
            }catch(final ClassCastException | YamlException e){
                if(locale != null)
                    logger.severe(locale.getString("permissions.const.default.malformed") + '\n' + Exceptions.getStackTraceAsString(e));
                throw e;
            }finally{
                if(IN != null)
                    try{ IN.close();
                    }catch(final IOException e){
                        if(locale != null)
                            logger.warning(locale.getString("permissions.const.default.closeIO") + '\n' + Exceptions.getStackTraceAsString(e));
                    }
            }
        }
    // load perm
        Permissions perms = null;
        {
            if(locale != null)
                logger.fine(locale.getString("permissions.debug.const.permFile",permissionsFile.getAbsolutePath()));
            YamlReader IN = null;
            try{
                IN = new YamlReader(new FileReader(permissionsFile));
                perms = new Permissions((Map) IN.read());
            }catch(final FileNotFoundException ignored){
                if(locale != null)
                    logger.warning(locale.getString("permissions.const.load.missing"));
                if(!permissionsFile.exists())
                    try(final InputStream dpr = getClass().getResourceAsStream(defaultPermissionsResource)){
                        Files.copy(dpr, permissionsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        if(locale != null)
                            logger.info(locale.getString("permissions.const.load.default"));
                    }catch(final Throwable e){
                        if(locale != null)
                            logger.severe(locale.getString("permissions.const.load.failedCopy") + '\n' + Exceptions.getStackTraceAsString(e));
                    }
                else if(locale != null)
                    logger.warning(locale.getString("permissions.const.load.exists"));
            }catch(final ClassCastException | YamlException e){
                if(locale != null)
                    logger.severe(locale.getString("permissions.const.load.malformed") + '\n' + Exceptions.getStackTraceAsString(e));
            }finally{
                if(IN != null)
                    try{ IN.close();
                    }catch(final IOException e){
                        if(locale != null)
                            logger.warning(locale.getString("permissions.const.load.closeIO") + '\n' + Exceptions.getStackTraceAsString(e));
                    }
            }
        }

        this.permissions = perms == null ? def : perms;
        if(locale != null)
            logger.info(locale.getString("permissions.const.loaded"));
    }

    //

    @Override
    public String toString(){
        return new toStringBuilder("PermissionsService")
            .addObject("permissionsFile",permissionsFile)
            .addObject("defaultPermissionsResource",defaultPermissionsResource)
            .addObject("permissions",permissions)
            .toString();
    }

}
