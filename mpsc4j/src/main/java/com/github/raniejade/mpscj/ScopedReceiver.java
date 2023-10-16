package com.github.raniejade.mpscj;

public interface ScopedReceiver<T> extends Receiver<T>, AutoCloseable {
    @Override
    void close();
}
