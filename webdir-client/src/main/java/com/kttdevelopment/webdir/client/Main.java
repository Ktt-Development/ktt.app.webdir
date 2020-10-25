package com.kttdevelopment.webdir.client;

import com.kttdevelopment.webdir.client.utility.ExceptionUtility;
import com.kttdevelopment.webdir.client.utility.FileUtility;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public abstract class Main {

    // root directory is one above binaries
    public static final File root = new File(".").getParentFile().getAbsoluteFile();

    static LoggerService logger;

    public static void main(String[] args){
        try{
            logger = new LoggerService();
        }catch(final Throwable e){
            final long time = System.currentTimeMillis();
            final String response = "---- WebDir Crash Log ----\n" +
                                    "Time: " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSaa zzz").format(time) + '\n' +
                                    "OS: " + System.getProperty("os.name").toLowerCase() + '\n' +
                                    "Java Version: " + System.getProperty("java.version") + '\n' +
                                    "Logger: " + logger + '\n' +
                                    "---- [ Stack Trace ] ----\n" +
                                    ExceptionUtility.getStackTraceAsString(e);
            try{
                logger.getLogger("WebDir").severe('\n' + response);
            }catch(final Throwable ignored){
                Logger.getGlobal().severe('\n' + response);
            }
            ExceptionUtility.runIgnoreException(
                () -> Files.write(FileUtility.getFreeFile(new File(root, "crash-log-" + time + ".log")).toPath(), response.getBytes(StandardCharsets.UTF_8))
            );
        }
    }

}
