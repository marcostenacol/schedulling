package com.scheduling.base.service;

public interface BaseService<I, O> {
    O execute(I input);
}
