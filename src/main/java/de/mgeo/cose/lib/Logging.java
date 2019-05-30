package de.mgeo.cose.lib;

import ch.qos.logback.classic.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging {
    String classname;
    ch.qos.logback.classic.Logger logger;
    public Logging (String classname){
        this.classname=classname;
        logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(this.classname);
        String loglvl = "ERROR";
        if (System.getenv("LOG_LEVEL") != null) {
            loglvl=System.getenv("LOG_LEVEL");
        }
        else if (System.getProperty("LOG_LEVEL") != null) {
            loglvl = System.getProperty("LOG_LEVEL");
        }

        if (loglvl.equals("ERROR")) {
            logger.setLevel(Level.ERROR);
        }
        else if (loglvl.equals("INFO")) {
            logger.setLevel(Level.INFO);
        }
        else if (loglvl.equals("DEBUG")) {
            logger.setLevel(Level.DEBUG);
        }
        else if (loglvl.equals("TRACE")) {
            logger.setLevel(Level.TRACE);
        }
        else if (loglvl.equals("WARN")) {
            logger.setLevel(Level.WARN);
        }
        else {
            logger.setLevel(Level.ERROR);
        }
    }
    public Logger getLogger() {
        return LoggerFactory.getLogger(this.classname);
    }

    public void info (String msg) {
        logger.info(msg);
    }
    public void error (String msg) {
        logger.error(msg);
    }
    public void debug (String msg) {
        logger.debug(msg);
    }
    public void trace (String msg) {
        logger.trace(msg);
    }
    public void warn (String msg) {
        logger.warn(msg);
    }
}
