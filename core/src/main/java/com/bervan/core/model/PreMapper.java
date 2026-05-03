package com.bervan.core.model;

public interface PreMapper<FROM, TO> {
    void map(FROM from, TO to);

    default boolean isApplicable(Object from, Object to) {
        if (from == null || to == null) {
            return false;
        }

        try {
            FROM test1 = (FROM) from;
            TO test2 = (TO) to;
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
