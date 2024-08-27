package com.bervan.core.model;

public interface BervanLogger {
    void error(String message);

    void info(String message);

    void debug(String message);

    void warn(String message);

    void error(String message, Throwable throwable);

    void warn(String message, Throwable throwable);

    void error(Throwable throwable);

    void warn(Throwable throwable);
}
