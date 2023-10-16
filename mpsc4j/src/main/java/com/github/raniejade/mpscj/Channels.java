package com.github.raniejade.mpscj;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public final class Channels {


    public static <T> Channel<T> create() {
        return create(Integer.MAX_VALUE);
    }

    public static <T> Channel<T> create(int capacity) {
        var buffer = new LinkedBlockingQueue<T>(capacity);
        var sender = new SenderImpl<>(buffer);
        var receiver = new ReceiverImpl<>(buffer);
        return new Channel<>(sender, receiver);
    }

    private Channels() {
    }


    private record SenderImpl<T>(LinkedBlockingQueue<T> buffer) implements Sender<T> {
        @Override
        public void send(T data) {
            try {
                buffer.put(data);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private record ReceiverImpl<T>(LinkedBlockingQueue<T> buffer) implements Receiver<T> {
        @Override
        public T receive() {
            try {
                return buffer.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Optional<T> tryReceive() {
            T data = buffer.poll();
            return Optional.ofNullable(data);
        }

        @Override
        public Iterator<T> iterator() {
            return buffer.iterator();
        }
    }
}
