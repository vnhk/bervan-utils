package com.bervan.dtocore.service;

public interface CustomMapper<FROM, TO> {

    TO map(FROM from);

    Class<FROM> getFrom();

    Class<TO> getTo();
}
