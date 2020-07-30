package com.kttdevelopment.webdir.server;

import com.esotericsoftware.yamlbeans.*;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.server.permissions.Permissions;

import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public final class PermissionsService {

    private final File permissionsFile;
    private Permissions permissions;

    private final Permissions defaultPermissions;

    //

    public final Permissions getPermissions(){
        return permissions;
    }

    //

    PermissionsService(final File permissionsFile, final String defaultPermissionsResource) throws YamlException{
        this.permissionsFile = permissionsFile;

        final LocaleService locale = Main.getLocaleService();
        Logger logger = Main.getLoggerService().getLogger("Permissions");
        
        logger.info(locale.getString("permissions.init.start"));

        YamlReader IN = null;
        try{ // default
            IN = new YamlReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(defaultPermissionsResource))));
            defaultPermissions = new Permissions((Map) IN.read());
        }catch(final ClassCastException | NullPointerException | YamlException e){
            logger.severe(locale.getString("permissions.init.notFound"));
            throw e;
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(locale.getString("permissions.init.stream") + '\n' + Exceptions.getStackTraceAsString(e));
                }
        }

        read();
        logger = Main.getLoggerService().getLogger(locale.getString("permissions"));
        logger.info(locale.getString("permissions.init.finished"));
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized final boolean read(){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("permissions"));
        logger.info(locale.getString("permissions.read.start"));

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(permissionsFile));
            permissions = new Permissions((Map) IN.read());
            logger.info(locale.getString("permissions.read.finished"));
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning(locale.getString("permissions.read.notFound"));
            permissions = defaultPermissions;
            if(!write())
                logger.severe(locale.getString("permissions.read.notCreate"));
            else
                logger.info(locale.getString("permissions.read.created"));
        }catch(final ClassCastException | YamlException e){
            logger.severe(locale.getString("permissions.read.badSyntax") + '\n' + Exceptions.getStackTraceAsString(e));
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning(locale.getString("permissions.read.stream"));
                }
        }
        return false;
    }

    public synchronized final boolean write(){
        final LocaleService locale = Main.getLocaleService();
        final Logger logger = Main.getLoggerService().getLogger(locale.getString("permissions"));
        logger.info(locale.getString("permissions.write.start"));

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(permissionsFile));
            //OUT.write(permissions.toMap());
            logger.info(locale.getString("permissions.write.finished"));
            return true;
        }catch(final IOException e){
            logger.severe(locale.getString("permissions.write.failed") + '\n' + Exceptions.getStackTraceAsString(e));
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final IOException e){
                    logger.severe(locale.getString("permissions.write.stream") + '\n' + Exceptions.getStackTraceAsString(e));
                }
        }
        return false;
    }

}
