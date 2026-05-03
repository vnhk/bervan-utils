package com.bervan.core.model;

public interface PostMapper<FROM, TO> {
    void map(FROM from, TO to);

    Class<FROM> getFromType();

    Class<TO> getToType();

    default boolean isApplicable(Object from, Object to) {
        if (from == null || to == null) {
            return false;
        }

        // Check if objects match expected types
        return getFromType().isInstance(from)
                && getToType().isInstance(to);

    }
}
