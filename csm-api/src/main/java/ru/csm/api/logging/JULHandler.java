package ru.csm.api.logging;

import java.util.logging.Logger;

public class JULHandler implements LogHandler {

    private final Logger logger;

    public JULHandler(Logger logger){
        this.logger = logger;
    }

    @Override
    public void info(String message) {

    }

    @Override
    public void warning(String message) {

    }

    @Override
    public void severe(String message) {

    }
}
