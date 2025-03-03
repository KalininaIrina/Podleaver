package com.podlive.tracker.common.service;

public interface GenericMapper<T, U> {
    U map (T entity);
}
