package com.after_sunrise.brutus.impl;

import com.after_sunrise.brutus.Blacksmith;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

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
public class BlacksmithImpl implements Blacksmith {

    private final ReentrantLock lock = new ReentrantLock();

    private final char[] chars;

    private final int min;

    private final int max;

    private final AtomicReference<int[]> current;

    @Inject
    public BlacksmithImpl(BlacksmithContext context) {

        chars = prepareCharacters(context);

        min = chars.length == 0 ? 0 : Math.max(context.getMinLength(), 0);

        max = chars.length == 0 ? 0 : Math.max(context.getMaxLength(), min);

        current = new AtomicReference<>();

    }

    private char[] prepareCharacters(BlacksmithContext context) {

        List<Byte> bytes = new ArrayList<>(Byte.MAX_VALUE);

        if (context.isIncludeLower()) {
            for (byte b = 0x61; b <= 0x7a; b++) {
                bytes.add(b);
            }
        }

        if (context.isIncludeUpper()) {
            for (byte b = 0x41; b <= 0x5a; b++) {
                bytes.add(b);
            }
        }

        if (context.isIncludeNumber()) {
            for (byte b = 0x30; b <= 0x39; b++) {
                bytes.add(b);
            }
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
        }

        if (context.isIncludeControl()) {
            for (byte b = 0x00; b <= 0x1f; b++) {
                bytes.add(b);
            }
            bytes.add((byte) 0x7f);
        }

        char[] result = new char[bytes.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = (char) bytes.get(i).byteValue();
        }

        return result;

    }

    private char[] next() {

        int[] data = current.get();

        if (data == null) {

            data = new int[min];

            current.set(data);

        } else {

            if (data.length > max) {
                return null;
            }

            for (int i = 0; i <= data.length; i++) {

                if (i == data.length) {

                    data = new int[data.length + 1];

                    current.set(data);

                    break;

                }

                if (data[i] < chars.length - 1) {

                    data[i] = data[i] + 1;

                    break;

                }

                data[i] = 0;

            }

            if (data.length > max) {
                return null;
            }

        }

        char[] result = new char[data.length];

        for (int i = 0; i < result.length; i++) {

            char c = chars[data[i]];

            result[i] = c;

        }

        return result;

    }

    @Override
    public char[] get() {

        try {

            lock.lock();

            return next();

        } finally {
            lock.unlock();
        }

    }

    @Override
    public Long countTotal() {

        long total = 0L;

        if (min == 0) {
            total++;
        }

        for (int i = 1; i <= max; i++) {
            total += Math.pow(chars.length, i);
        }

        return total;

    }

}
