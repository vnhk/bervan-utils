package com.bervan.core.model;

public interface DefaultCustomMapper<FROM, TO> extends CustomMapper<FROM, TO> {

    Class<FROM> getFrom();

    Class<TO> getTo();
}
