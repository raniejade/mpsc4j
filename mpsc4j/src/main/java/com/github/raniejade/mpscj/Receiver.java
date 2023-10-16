package com.github.raniejade.mpscj;

import java.util.Optional;

public interface Receiver<T> extends Iterable<T> {
    T receive();

    Optional<T> tryReceive();
}
