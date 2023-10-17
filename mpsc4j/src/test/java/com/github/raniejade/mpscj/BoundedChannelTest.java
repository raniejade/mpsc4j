package com.github.raniejade.mpscj;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BoundedChannelTest {
    @ParameterizedTest
    @MethodSource("provideExecutors")
    public void blockingSend(ExecutorService executor) throws Exception {
        var channel = Channels.<String>create(1);
        var semaphore = new Semaphore(0);
        executor.submit(() -> {
            channel.sender().send("hello world");
            semaphore.release();
        });
        assertFalse(semaphore.tryAcquire());
        try (var receiver = channel.receiver()) {
            assertEquals("hello world", receiver.receive());
        }
        assertTrue(semaphore.tryAcquire(100, TimeUnit.MILLISECONDS));
    }

    @ParameterizedTest
    @MethodSource("provideExecutors")
    public void blockingReceive(ExecutorService executor) throws Exception {
        var channel = Channels.<String>create(1);
        var semaphore = new Semaphore(0);
        var value = executor.submit(() -> {
            try (var receiver = channel.receiver()) {
                return receiver.receive();
            } finally {
                semaphore.release();
            }
        });
        assertFalse(semaphore.tryAcquire());
        channel.sender().send("hello world");
        assertTrue(semaphore.tryAcquire(100, TimeUnit.MILLISECONDS));
        assertEquals("hello world", value.get(100, TimeUnit.MILLISECONDS));
    }

    private static Stream<Arguments> provideExecutors() {
        return Stream.of(
                Arguments.of(Executors.newSingleThreadExecutor()),
                Arguments.of(Executors.newVirtualThreadPerTaskExecutor())
        );
    }
}
