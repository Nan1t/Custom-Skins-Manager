package ru.csm.velocity;

import org.slf4j.Logger;
import ru.csm.api.logging.LogHandler;

public class Slf4jHandler implements LogHandler {

    private final Logger logger;

    public Slf4jHandler(Logger logger){
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warning(String message) {
        logger.warn(message);
    }

    @Override
    public void severe(String message) {
        logger.error(message);
    }
}
