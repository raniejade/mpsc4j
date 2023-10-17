package com.github.raniejade.mpscj;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class Channel<T> {
    private final Sender<T> sender;
    private final Receiver<T> receiver;
    private final AtomicBoolean receiverBounded = new AtomicBoolean(false);

    public Channel(Sender<T> sender, Receiver<T> receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public Sender<T> sender() {
        return sender;
    }

    public ScopedReceiver<T> receiver() {
        if (receiverBounded.get() || !receiverBounded.compareAndSet(false, true)) {
            throw new IllegalStateException("Receiver already bound.");
        }
        return new ScopedReceiver<>() {
            @Override
            public T receive() {
                return receiver.receive();
            }

            @Override
            public Optional<T> tryReceive() {
                return receiver.tryReceive();
            }

            @Override
            public void close() {
                // unlikely, but in-case it does, just throw an exception to signify that it happened.
                if (!receiverBounded.compareAndSet(true, false)) {
                    // panic!("should not happen")
                    throw new IllegalStateException("Failed to close ScopedReceiver.");
                }
            }

            @Override
            public Iterator<T> iterator() {
                return receiver.iterator();
            }
        };
    }
}