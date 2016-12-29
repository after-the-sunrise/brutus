package com.after_sunrise.brutus.impl;

import com.after_sunrise.brutus.Blacksmith;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * In-memory implementation for {@link Blacksmith}. Criteria for generating the passwords,
 * and the keeping track of already-consumed passwords are kept in memory locally.
 * </p>
 * <p>
 * Contexts are initialized within the instance construction,
 * and the calls to the {@code BlacksmithImpl#get()} are re-entrant (=thread-safe).
 * </p>
 * <p>
 * <i>'The pen is mightier than the sword.'</i>
 * </p>
 *
 * @author takanori.takase
 */
public class BlacksmithImpl implements Blacksmith, Runnable {

    private static final String FORMAT = "%,3d";

    private static final long INTERVAL = 100_000_000;

    private static final int BUFFER = 100;

    private static final char[] END = new char[0];

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final AtomicLong count = new AtomicLong();

    private final BlockingQueue<char[]> queue = new LinkedBlockingQueue<>(BUFFER);

    private final char[] chars;

    private final int min;

    private final int max;

    private final long total;

    private final long interval;

    private int[] current;

    @Inject
    public BlacksmithImpl(BlacksmithContext context) {

        chars = prepareCharacters(context);

        min = chars.length == 0 ? 0 : Math.max(context.getMinLength(), 0);

        max = chars.length == 0 ? 0 : Math.max(context.getMaxLength(), min);

        total = calculateTotal();

        interval = calculateInterval(total);

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.setName(getClass().getSimpleName());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        log.info("Initialized with {} candidates. (chars={}, min={}, max={})", //
                String.format(FORMAT, total), chars.length, min, max);

    }

    private char[] prepareCharacters(BlacksmithContext context) {

        List<Byte> bytes = new ArrayList<>(Byte.MAX_VALUE);

        if (context.isIncludeLower()) {

            for (byte b = 0x61; b <= 0x7a; b++) {
                bytes.add(b);
            }

            log.debug("Added lower-case chars.");

        }

        if (context.isIncludeUpper()) {

            for (byte b = 0x41; b <= 0x5a; b++) {
                bytes.add(b);
            }

            log.debug("Added upper-case chars.");

        }

        if (context.isIncludeNumber()) {

            for (byte b = 0x30; b <= 0x39; b++) {
                bytes.add(b);
            }

            log.debug("Added numeric chars.");

        }

        if (context.isIncludeSymbol()) {

            for (byte b = 0x20; b <= 0x2f; b++) {
                bytes.add(b);
            }
            for (byte b = 0x3a; b <= 0x40; b++) {
                bytes.add(b);
            }
            for (byte b = 0x5b; b <= 0x60; b++) {
                bytes.add(b);
            }
            for (byte b = 0x7b; b <= 0x7e; b++) {
                bytes.add(b);
            }

            log.debug("Added symbol chars.");

        }

        if (context.isIncludeControl()) {

            for (byte b = 0x00; b <= 0x1f; b++) {
                bytes.add(b);
            }

            bytes.add((byte) 0x7f);

            log.debug("Added control chars.");

        }

        char[] result = new char[bytes.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = (char) bytes.get(i).byteValue();
        }

        return result;

    }

    @VisibleForTesting
    long calculateTotal() {

        long total = 0L;

        if (min == 0) {
            total++;
        }

        for (int i = 1; i <= max; i++) {
            total += Math.pow(chars.length, i);
        }

        return total;

    }

    @VisibleForTesting
    long calculateInterval(long total) {

        long interval = 1L;

        while (interval * 10 < total) {

            interval *= 10;

            if (interval >= INTERVAL) {
                break;
            }

        }

        return interval;

    }

    private char[] next() {

        if (current == null) {

            current = new int[min];

        } else {

            if (current.length > max) {
                return null;
            }

            for (int i = 0; i <= current.length; i++) {

                if (i == current.length) {

                    current = new int[current.length + 1];

                    break;

                }

                if (current[i] < chars.length - 1) {

                    current[i] = current[i] + 1;

                    break;

                }

                current[i] = 0;

            }

            if (current.length > max) {
                return null;
            }

        }

        char[] result = new char[current.length];

        for (int i = 0; i < result.length; i++) {

            char c = chars[current[i]];

            result[i] = c;

        }

        return result;

    }

    @Override
    public void run() {

        try {

            char[] c;

            while ((c = next()) != null) {

                queue.put(c);

            }

            queue.put(END);

        } catch (InterruptedException e) {

            log.trace("Could not append an element.", e);

        }

    }

    @Override
    public char[] get() {

        char[] c;

        while (true) {

            c = queue.poll();

            if (c != null) {
                break;
            }

            Thread.yield();

        }

        if (c == END) {

            queue.add(END);

            return null;

        }

        long i = count.incrementAndGet();

        if (i == total || i % interval == 0) {

            log.info("Supplied {} / {} ({}%) passwords : {}", //
                    String.format(FORMAT, i), //
                    String.format(FORMAT, total), //
                    String.format(FORMAT, (i * 100) / total), //
                    Arrays.toString(c));

        }

        return c;

    }

}
