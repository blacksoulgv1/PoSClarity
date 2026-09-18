package com.gerardgv.posclarity.utils;

@FunctionalInterface
public interface ActiveToggleHandler {
    boolean update(int id, boolean active) throws Exception;
}
