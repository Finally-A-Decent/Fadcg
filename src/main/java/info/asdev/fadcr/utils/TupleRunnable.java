package info.asdev.fadcr.utils;

@FunctionalInterface
public interface TupleRunnable<K, V> {
    void run(K key, V value);
}
