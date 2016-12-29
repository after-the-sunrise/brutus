package com.after_sunrise.brutus.impl;

import com.after_sunrise.brutus.Blacksmith;
import com.after_sunrise.brutus.Blacksmith.BlacksmithContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.mockito.Mockito.when;

/**
 * @author takanori.takase
 */
public class BlacksmithImplTest {

    @Test(timeout = 5000L)
    public void testPrepareCharacters() {

        Function<BlacksmithContext, Set<Character>> f = (c) -> {

            BlacksmithImpl target = new BlacksmithImpl(c);

            Set<Character> values = new HashSet<>();

            char[] next;

            while ((next = target.get()) != null) {

                Assert.assertEquals(1, next.length);

                values.add(next[0]);

            }

            return values;

        };

        BlacksmithContext context = Mockito.mock(BlacksmithContext.class);
        when(context.getMinLength()).thenReturn(1);
        when(context.getMaxLength()).thenReturn(1);

        // All
        when(context.isIncludeLower()).thenReturn(true);
        when(context.isIncludeUpper()).thenReturn(true);
        when(context.isIncludeNumber()).thenReturn(true);
        when(context.isIncludeSymbol()).thenReturn(true);
        when(context.isIncludeControl()).thenReturn(true);
        Set<Character> all = f.apply(context);
        Assert.assertEquals((0x7f) + 1, all.size());

        // Lower only
        when(context.isIncludeLower()).thenReturn(true);
        when(context.isIncludeUpper()).thenReturn(false);
        when(context.isIncludeNumber()).thenReturn(false);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(false);
        Set<Character> lower = f.apply(context);
        Assert.assertEquals(26, lower.size());

        // Upper only
        when(context.isIncludeLower()).thenReturn(false);
        when(context.isIncludeUpper()).thenReturn(true);
        when(context.isIncludeNumber()).thenReturn(false);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(false);
        Set<Character> upper = f.apply(context);
        Assert.assertEquals(26, upper.size());

        // Number only
        when(context.isIncludeLower()).thenReturn(false);
        when(context.isIncludeUpper()).thenReturn(false);
        when(context.isIncludeNumber()).thenReturn(true);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(false);
        Set<Character> number = f.apply(context);
        Assert.assertEquals(10, number.size());

        // Symbol only
        when(context.isIncludeLower()).thenReturn(false);
        when(context.isIncludeUpper()).thenReturn(false);
        when(context.isIncludeNumber()).thenReturn(false);
        when(context.isIncludeSymbol()).thenReturn(true);
        when(context.isIncludeControl()).thenReturn(false);
        Set<Character> symbol = f.apply(context);
        Assert.assertEquals(33, symbol.size());

        // Control only
        when(context.isIncludeLower()).thenReturn(false);
        when(context.isIncludeUpper()).thenReturn(false);
        when(context.isIncludeNumber()).thenReturn(false);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(true);
        Set<Character> control = f.apply(context);
        Assert.assertEquals(33, control.size());

        // Make sure the characters are 'Mutually Exclusive and Collectively Exhaustive'.
        Set<Character> sum = new HashSet<>();
        sum.addAll(lower);
        sum.addAll(upper);
        sum.addAll(number);
        sum.addAll(symbol);
        sum.addAll(control);
        Assert.assertEquals(all.size(), sum.size());

        // Corner case : No characters
        when(context.isIncludeLower()).thenReturn(false);
        when(context.isIncludeUpper()).thenReturn(false);
        when(context.isIncludeNumber()).thenReturn(false);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(false);
        Blacksmith target = new BlacksmithImpl(context);
        Assert.assertEquals("", String.valueOf(target.get()));
        Assert.assertNull(target.get());

    }

    /**
     * Examine the character composition with multiple letters.
     */
    @Test(timeout = 5000L)
    public void testGet() {

        BlacksmithContext context = Mockito.mock(BlacksmithContext.class);
        when(context.getMinLength()).thenReturn(1);
        when(context.getMaxLength()).thenReturn(3);
        when(context.isIncludeLower()).thenReturn(true);
        when(context.isIncludeUpper()).thenReturn(false);
        when(context.isIncludeNumber()).thenReturn(true);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(false);
        BlacksmithImpl target = new BlacksmithImpl(context);

        Assert.assertEquals("a", new String(target.get()));
        Assert.assertEquals("b", new String(target.get()));
        Assert.assertEquals("c", new String(target.get()));
        for (int i = 0; i < 22; i++) {
            Assert.assertNotNull(target.get());
        }
        Assert.assertEquals("z", new String(target.get()));
        Assert.assertEquals("0", new String(target.get()));
        Assert.assertEquals("1", new String(target.get()));
        for (int i = 0; i < 7; i++) {
            Assert.assertNotNull(target.get());
        }
        Assert.assertEquals("9", new String(target.get()));
        Assert.assertEquals("aa", new String(target.get()));
        Assert.assertEquals("ba", new String(target.get()));
        Assert.assertEquals("ca", new String(target.get()));
        for (int i = 0; i < 1292; i++) {
            Assert.assertNotNull(target.get());
        }
        Assert.assertEquals("99", new String(target.get()));
        Assert.assertEquals("aaa", new String(target.get()));
        Assert.assertEquals("baa", new String(target.get()));
        for (int i = 0; i < 46650; i++) {
            Assert.assertNotNull(target.get());
        }
        Assert.assertEquals("699", new String(target.get()));
        Assert.assertEquals("799", new String(target.get()));
        Assert.assertEquals("899", new String(target.get()));
        Assert.assertEquals("999", new String(target.get()));
        Assert.assertNull(target.get());
        Assert.assertNull(target.get());
        Assert.assertNull(target.get());

    }

    /**
     * Examine the character composition with multiple threads.
     */
    @Test(timeout = 5000L)
    public void testGet_Concurrent() throws InterruptedException {

        BlacksmithContext context = Mockito.mock(BlacksmithContext.class);
        when(context.getMinLength()).thenReturn(1);
        when(context.getMaxLength()).thenReturn(2);
        when(context.isIncludeLower()).thenReturn(true);
        when(context.isIncludeUpper()).thenReturn(true);
        when(context.isIncludeNumber()).thenReturn(false);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(false);
        BlacksmithImpl target = new BlacksmithImpl(context);

        Set<String> results = new ConcurrentSkipListSet<>();

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {

            executor.execute(() -> {

                char[] chars;

                while ((chars = target.get()) != null) {

                    results.add(String.valueOf(chars));

                }

            });

        }

        executor.shutdown();

        executor.awaitTermination(1L, TimeUnit.MINUTES);

        Assert.assertEquals((52) + (52 * 52), results.size());

    }

    /**
     * Measure the performance to fetch all values.
     */
    @Test
    public void testGet_Benchmark() throws InterruptedException {

        BlacksmithContext context = Mockito.mock(BlacksmithContext.class);
        when(context.getMinLength()).thenReturn(0);
        when(context.getMaxLength()).thenReturn(2);
        when(context.isIncludeLower()).thenReturn(true);
        when(context.isIncludeUpper()).thenReturn(true);
        when(context.isIncludeNumber()).thenReturn(true);
        when(context.isIncludeSymbol()).thenReturn(true);
        when(context.isIncludeControl()).thenReturn(false);
        BlacksmithImpl target = new BlacksmithImpl(context);

        final AtomicLong count = new AtomicLong();

        ExecutorService executor = Executors.newCachedThreadPool();

        long start = System.nanoTime();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            executor.execute(() -> {
                while (target.get() != null) {
                    count.incrementAndGet();
                }
            });
        }

        executor.shutdown();

        executor.awaitTermination(365L, TimeUnit.DAYS);

        long end = System.nanoTime();

        long elapsed = (end - start) / 1_000_000;

        System.out.println(String.format("Count = %,3d, Time = %,3d", count.get(), elapsed));

    }

    @Test(timeout = 5000L)
    public void testCalculateTotal() {

        Function<Blacksmith, Long> f = (b) -> {

            long i = 0L;

            while (b.get() != null) {
                i++;
            }

            return i;

        };

        BlacksmithContext context = Mockito.mock(BlacksmithContext.class);
        when(context.isIncludeLower()).thenReturn(false);
        when(context.isIncludeUpper()).thenReturn(false);
        when(context.isIncludeNumber()).thenReturn(true);
        when(context.isIncludeSymbol()).thenReturn(false);
        when(context.isIncludeControl()).thenReturn(false);

        // 0 letter
        when(context.getMinLength()).thenReturn(0);
        when(context.getMaxLength()).thenReturn(0);
        BlacksmithImpl target_0 = new BlacksmithImpl(context);
        Assert.assertEquals((Long) 1L, (Long) target_0.calculateTotal());
        Assert.assertEquals((Long) 1L, f.apply(target_0));
        Assert.assertEquals((Long) 1L, (Long) target_0.calculateTotal());

        // 1 letter
        when(context.getMinLength()).thenReturn(1);
        when(context.getMaxLength()).thenReturn(1);
        BlacksmithImpl target_1 = new BlacksmithImpl(context);
        Assert.assertEquals((Long) 10L, (Long) target_1.calculateTotal());
        Assert.assertEquals((Long) 10L, f.apply(target_1));
        Assert.assertEquals((Long) 10L, (Long) target_1.calculateTotal());

        // 1 + 2 letter
        when(context.getMinLength()).thenReturn(1);
        when(context.getMaxLength()).thenReturn(2);
        BlacksmithImpl target_2 = new BlacksmithImpl(context);
        Assert.assertEquals((Long) ((10L) + (10L * 10L)), (Long) target_2.calculateTotal());
        Assert.assertEquals((Long) ((10L) + (10L * 10L)), f.apply(target_2));
        Assert.assertEquals((Long) ((10L) + (10L * 10L)), (Long) target_2.calculateTotal());

        // 0 + 1 + 2 letter
        when(context.getMinLength()).thenReturn(0);
        when(context.getMaxLength()).thenReturn(2);
        BlacksmithImpl target_3 = new BlacksmithImpl(context);
        Assert.assertEquals((Long) ((1L) + (10L) + (10L * 10L)), (Long) target_3.calculateTotal());
        Assert.assertEquals((Long) ((1L) + (10L) + (10L * 10L)), f.apply(target_3));
        Assert.assertEquals((Long) ((1L) + (10L) + (10L * 10L)), (Long) target_3.calculateTotal());

    }

    @Test(timeout = 5000L)
    public void testCalculateInterval() {

        BlacksmithContext context = Mockito.mock(BlacksmithContext.class);

        BlacksmithImpl target = new BlacksmithImpl(context);

        Assert.assertEquals(1L, target.calculateInterval(1L));
        Assert.assertEquals(1L, target.calculateInterval(10L));
        Assert.assertEquals(10L, target.calculateInterval(100L));
        Assert.assertEquals(100L, target.calculateInterval(1_000L));
        Assert.assertEquals(1_000L, target.calculateInterval(10_000L));
        Assert.assertEquals(10_000L, target.calculateInterval(100_000L));
        Assert.assertEquals(100_000L, target.calculateInterval(1_000_000L));
        Assert.assertEquals(1_000_000L, target.calculateInterval(10_000_000L));
        Assert.assertEquals(10_000_000L, target.calculateInterval(100_000_000L));
        Assert.assertEquals(100_000_000L, target.calculateInterval(1_000_000_000L));
        Assert.assertEquals(100_000_000L, target.calculateInterval(10_000_000_000L));
        Assert.assertEquals(100_000_000L, target.calculateInterval(100_000_000_000L));

    }

}