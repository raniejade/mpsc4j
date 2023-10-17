# MPSC4J
Java implementation of Rust's [mpsc](https://doc.rust-lang.org/std/sync/mpsc/index.html) module.

This is just a glorified wrapper of [java.util.concurrent.LinkedBlockingQueue](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/LinkedBlockingQueue.html) with a more explicit API.


#### Unbounded channel
```java
var channel = Channels.<String>create();

// get the receiver
try (var receiver = channel.receiver()) {
    assertEquals("hello world", receiver.receive()); // blocks until an item is available
    // receiver.tryReceive() returns an Optional<T> which doesn't block.
}

// in another thread
var sender = channel.sender();
sender.send("hello world");
```

#### Bounded channel
```java
var channel = Channels.<String>create(1);

// thread 1
var sender = channel.sender();
sender.send("hello world"); // this will block!


// thread 2
try (var receiver = channel.receiver()) {
    assertEquals("hello world", receiver.receive()); // unblocks thread 1
}
```