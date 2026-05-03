package com.bervan.core.c3;

public enum TaskRelationType {

    CHILD_IS_PART_OF("Parent of", "Is part of"),

    BLOCKS("Blocks", "Is blocked by"),

    REQUIRES("Requires", "Is required by"),

    RELATES_TO("Relates to", "Relates to"),

    CHILD_SOLVES("Solved by", "Solves");

    private final String displayName;
    private final String inverseDisplayName;

    TaskRelationType(String displayName, String inverseDisplayName) {
        this.displayName = displayName;
        this.inverseDisplayName = inverseDisplayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInverseDisplayName() {
        return inverseDisplayName;
    }

    public String getInternalName() {
        return name();
    }
}
