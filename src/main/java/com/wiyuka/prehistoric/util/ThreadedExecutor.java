package com.wiyuka.prehistoric.util;

import com.wiyuka.prehistoric.Util;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.concurrent.*;
import java.util.function.*;

public class ThreadedExecutor implements Executor {
    /** Whether allows async executions. Temporarily hard-coded. */
    public static final boolean ALLOW_ASYNC = true; // TODO: replace this field with a config entry
    
    private final String name;

    @Contract(pure = true)
    private ThreadedExecutor(String name) {
        this.name = name;
    }

    public static @NotNull ThreadedExecutor newExecutor() {
        try {
            Class<?> clazz = Class.forName("com.wiyuka.prehistoric.util.ThreadedExecutor");
            Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
            return (ThreadedExecutor) constructor.newInstance(Thread.currentThread().getStackTrace()[0].getClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Execute an operation with a return value asyncly. If {@link #ALLOW_ASYNC} is set to {@code false}, this action
     * will be ignored and execute {@code supplier} synchronously.
     *
     * @param supplier The operation wrapped in a {@link Supplier}.
     * @param <T> The return type of the operation.
     * @return The returned value of the operation.
     * @exception RuntimeException If the {@link Future} instance of {@code supplier} was canceled, completed exceptionally,
     *                             and/or the current thread was interrupted while waiting.
     */
    public static <T> T supplyAsync(Supplier<T> supplier) {
        if (!ALLOW_ASYNC) {
            return supplier.get();
        }
        
        CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, newExecutor());
        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Execute an operation without a return value asyncly. If {@link #ALLOW_ASYNC} is set to {@code false}, this action
     * will be ignored and execute {@code supplier} synchronously.
     *
     * @param runnable The operation wrapped in a {@link Runnable}.
     * @exception RuntimeException If the {@link Future} instance of {@code runnable} was canceled, completed exceptionally,
     *                             and/or the current thread was interrupted while waiting.
     */
    public static void runAsync(Runnable runnable) {
        if (!ALLOW_ASYNC) {
            runnable.run();
        }
        
        CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, newExecutor());
        try {
            future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Runs the garbage collector in the JVM asyncly. If {@link #ALLOW_ASYNC} is set to {@code false}, this action will
     * be ignored and starts the garbage collector synchronously.
     *
     * @exception RuntimeException If the {@link Future} instance of garbage cleaner was canceled, completed exceptionally,
     *                             and/or the current thread was interrupted while waiting.
     */
    public static void gcAsync() {
        runAsync(System::gc);
    }

    @Override
    public void execute(@NotNull Runnable command) {
        String taskName = Util.ensureStringSecure(name + command);
        Thread thread = new Thread(command);
        thread.setName(taskName);
        thread.start();
    }
}
