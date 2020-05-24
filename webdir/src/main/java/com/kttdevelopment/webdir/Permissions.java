package com.kttdevelopment.webdir;

import com.esotericsoftware.yamlbeans.*;

import java.io.*;
import java.util.Map;

import static com.kttdevelopment.webdir.Application.*;
import static com.kttdevelopment.webdir.Logger.logger;

public final class Permissions {

    private final File permissionsFile;
    private Map permissions;

    private final Map defaultPermissions;

    //

    //

    Permissions(final File permissionsFile, final File defaultPermissionsFile){
        this.permissionsFile = permissionsFile;

        logger.info('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.init.start"));

        YamlReader IN = null;
        try{ // default
            IN = new YamlReader(new FileReader(defaultPermissionsFile));
            defaultPermissions = (Map) IN.read();
        }catch(final ClassCastException | FileNotFoundException | YamlException e){
            logger.severe('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.init.notFound"));
            throw new RuntimeException(e);
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.init.stream") + '\n' + Logger.getStackTraceAsString(e));
                }
        }

        read();
        logger.info('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.init.finished"));
    }

    @SuppressWarnings("UnusedReturnValue")
    public synchronized final boolean read(){
        logger.info('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.read.start"));

        YamlReader IN = null;
        try{
            IN = new YamlReader(new FileReader(permissionsFile));
            permissions = (Map) IN.read();
            logger.info('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.read.finished"));
            return true;
        }catch(final FileNotFoundException ignored){
            logger.warning('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.read.notFound"));
            permissions = defaultPermissions;
            if(!write())
                logger.severe('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.read.notCreate"));
            else
                logger.info('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.read.created"));
        }catch(final ClassCastException | YamlException e){
            logger.severe('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.read.badSyntax") + '\n' + Logger.getStackTraceAsString(e));
        }finally{
            if(IN != null)
                try{ IN.close();
                }catch(final IOException e){
                    logger.warning('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.read.stream"));
                }
        }
        return false;
    }

    public synchronized final boolean write(){
        logger.info('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.write.start"));

        YamlWriter OUT = null;
        try{
            OUT = new YamlWriter(new FileWriter(permissionsFile));
            OUT.write(permissions);
            logger.info('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.write.finished"));
            return true;
        }catch(final IOException e){
            logger.severe('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.write.failed") + '\n' + Logger.getStackTraceAsString(e));
        }finally{
            if(OUT != null)
                try{ OUT.close();
                }catch(final IOException e){
                    logger.severe('[' + locale.getString("permissions") + ']' + ' ' + locale.getString("permissions.write.stream") + '\n' + Logger.getStackTraceAsString(e));
                }
        }
        return false;
    }

}
