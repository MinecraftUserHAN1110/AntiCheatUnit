package kr.mcv.lightfrog.anticheatunit.observable;

import java.util.Objects;

public class Observable<T> {
    private T value;
    public Observable(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Deprecated
    public boolean isNull() {
        return value == null;
    }

    public boolean isNotNull() {
        return !(value == null);
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj);
    }

    @Override
    public Observable clone() {
        return this.clone();
    }

    public String toStringValue() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
