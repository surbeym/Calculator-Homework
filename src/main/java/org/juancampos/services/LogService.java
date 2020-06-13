package org.juancampos.services;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.text.MessageFormat;

public class LogService implements  ILogService{
    private static final Logger LOGGER = LogManager.getLogger(LogService.class.getName());
    public static final String DEBUG = "DEBUG";
    public static final String ERROR = "ERROR";

    @Override
    public Level getLogLevel(String logLevel){
        LOGGER.debug(MessageFormat.format("Log Level Received:{0}", logLevel));
        if (StringUtils.isNotEmpty(logLevel ) && StringUtils.isNotBlank(logLevel)){
            String changeLogLevel = logLevel.toUpperCase().trim();
            switch(changeLogLevel) {
                case ERROR:
                    return Level.ERROR;
                case DEBUG:
                        return Level.DEBUG;
                default:
                    return Level.INFO;
            }
        }
        return Level.INFO;
    }

    @Override
    public void setLogLevel(String loglevel) {
        Level customLogLevel = getLogLevel(loglevel);
        if (customLogLevel != Level.ERROR){
            LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
            Configuration conf = ctx.getConfiguration();
            LoggerConfig lconf = conf.getLoggerConfig(LOGGER.getName());
            lconf.setLevel(customLogLevel);
            ctx.updateLoggers(conf);
            LOGGER.debug(MessageFormat.format("Setting Custom Log Level to {0}", customLogLevel));
        }
    }
}
