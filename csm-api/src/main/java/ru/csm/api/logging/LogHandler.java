package ru.csm.api.logging;

public interface LogHandler {

    void info(String message);

    void warning(String message);

    void severe(String message);

}
