package org.juancampos.services;

import org.apache.logging.log4j.Level;

public interface ILogService {
    Level getLogLevel(String logLevel);

    void setLogLevel(String loglevel);
}
