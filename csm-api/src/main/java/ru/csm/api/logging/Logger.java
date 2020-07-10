package ru.csm.api.logging;

public final class Logger {

    private static LogHandler handler;

    private Logger(){}

    public static void set(LogHandler h){
        handler = h;
    }

    public static void info(String message, Object... args){
        handler.info(String.format(message, args));
    }

    public static void warning(String message, Object... args){
        handler.warning(String.format(message, args));
    }

    public static void severe(String message, Object... args){
        handler.severe(String.format(message, args));
    }

}
