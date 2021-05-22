package com.ksteindl.chemstore.service;

import com.ksteindl.chemstore.domain.input.Input;

public interface UniqueEntityInput<T extends Input> {

    default void throwExceptionIfNotUnique(T input) {
        throwExceptionIfNotUnique(input, -1l);
    }

    void throwExceptionIfNotUnique(T input, Long id);

}
