package com.after_sunrise.brutus.impl;

import com.after_sunrise.brutus.Attacker;
import com.after_sunrise.brutus.Barrack;
import com.after_sunrise.brutus.Blacksmith;
import com.after_sunrise.brutus.Mastermind;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Multi-threaded implementation of the {@link Mastermind}.
 * </p>
 * <p>
 * Threads are used for concurrently, where each thread consumes the password
 * from the {@link Blacksmith} individually until all passwords deplete.
 * </p>
 *
 * @author takanori.takase
 */
public class MastermindImpl implements Mastermind, ThreadFactory {

    static final int MULTIPLIER = 2;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AtomicInteger threadCount = new AtomicInteger();

    private final MastermindContext context;

    private final Blacksmith blacksmith;

    private final Barrack barrack;

    @Inject
    public MastermindImpl(MastermindContext context, Blacksmith blacksmith, Barrack barrack) {

        this.context = Objects.requireNonNull(context);

        this.blacksmith = Objects.requireNonNull(blacksmith);

        this.barrack = Objects.requireNonNull(barrack);

    }

    @Override
    public String call() throws Exception {

        int threads = adjustThreads(context.getConcurrency());

        log.info("Scheduling tasks with {} threads.", threads);

        CompletableFuture<String> future = new CompletableFuture<>();

        ExecutorService executor = Executors.newCachedThreadPool(this);

        for (int i = 0; i < threads; i++) {

            Runnable runnable = createTask(context.getInputPath(), future);

            executor.execute(runnable);

        }

        log.info("Scheduled tasks. Waiting for completion...");

        executor.shutdown();

        executor.awaitTermination(context.getTimeoutInMillis(), TimeUnit.MILLISECONDS);

        return future.getNow(null);

    }

    @VisibleForTesting
    int adjustThreads(int threads) {

        int max = Runtime.getRuntime().availableProcessors() * MULTIPLIER;

        return Math.min(max, Math.max(1, threads));

    }

    @Override
    public Thread newThread(Runnable r) {

        Thread t = new Thread(r);

        t.setDaemon(true);

        t.setName(getClass().getSimpleName() + "_" + threadCount.incrementAndGet());

        return t;

    }

    @VisibleForTesting
    Runnable createTask(String input, CompletableFuture<String> future) {
        return () -> {

            try {

                log.debug("Starting task.");

                Attacker attacker = barrack.apply(input);

                while (!future.isDone()) {

                    char[] attempt = blacksmith.get();

                    if (attempt == null) {
                        break;
                    }

                    boolean success = attacker.test(attempt);

                    if (success) {

                        String s = String.valueOf(attempt);

                        log.debug("Attack succeeded : '{}' - {}", s, attacker);

                        future.complete(s);

                    }

                }

                log.debug("Terminating task.");

            } catch (Exception e) {

                log.warn("Unexpected failure.", e.getMessage());

                future.completeExceptionally(e);

            }

        };

    }

}
