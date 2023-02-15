package kr.mcv.lightfrog.anticheatunit.utils.system;

import java.util.Collection;
import java.util.LinkedList;

public final class EvictingList<T> extends LinkedList<T> {
    private final int maxSize;

    public EvictingList(int maxSize) {
        this.maxSize = maxSize;
    }

    public EvictingList(Collection<? extends T> c, int maxSize) {
        super(c);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= getMaxSize()) removeFirst();
        return super.add(t);
    }

    public int getMaxSize() {
        return maxSize;
    }
}