package info.asdev.fadcr.utils;

@FunctionalInterface
public interface ParameterizedRunnable<T> {
    void run(T value);
}
