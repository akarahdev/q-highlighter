package dev.akarah.qh.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Locked<T> {
    T value;

    private Locked(T value) {
        this.value = value;
    }

    public static <T> Locked<T> of(T value) {
        return new Locked<>(value);
    }

    public void with(Consumer<T> consumer) {
        synchronized (this) {
            consumer.accept(this.value);
        }
    }

    public void map(Function<T, T> mapping) {
        synchronized (this) {
            this.value = mapping.apply(this.value);
        }
    }

    public void set(T value) {
        synchronized (this) {
            this.value = value;
        }
    }
}
