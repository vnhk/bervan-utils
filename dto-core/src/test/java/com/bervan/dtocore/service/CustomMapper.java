package com.bervan.dtocore.service;

import java.lang.reflect.Field;

public interface CustomMapper<FROM, TO> {

    TO map(FROM from, Field field);

    Class<FROM> getFrom();

    Class<TO> getTo();
}
