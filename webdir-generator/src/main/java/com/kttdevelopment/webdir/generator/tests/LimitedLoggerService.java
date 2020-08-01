package com.kttdevelopment.webdir.generator.tests;

import com.kttdevelopment.webdir.generator.logger.ILoggerService;

import java.util.logging.Logger;

public class LimitedLoggerService implements ILoggerService {

    @Override
    public final Logger getLogger(final String loggerName){
        return loggerName != null ? Logger.getLogger(loggerName) : Logger.getGlobal();
    }

}
