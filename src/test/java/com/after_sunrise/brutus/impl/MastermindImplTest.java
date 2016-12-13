package com.after_sunrise.brutus.impl;

import com.after_sunrise.brutus.Attacker;
import com.after_sunrise.brutus.Barrack;
import com.after_sunrise.brutus.Blacksmith;
import com.after_sunrise.brutus.Mastermind;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author takanori.takase
 */
public class MastermindImplTest {

    private MastermindImpl target;

    private Mastermind.MastermindContext context;

    private Blacksmith blacksmith;

    private Barrack barrack;

    private Attacker attacker;

    @Before
    public void setUp() throws Exception {

        context = Mockito.mock(Mastermind.MastermindContext.class);
        Mockito.when(context.getInputPath()).thenReturn("/tmp/test.dat");
        Mockito.when(context.getConcurrency()).thenReturn(2);
        Mockito.when(context.getTimeoutInMillis()).thenReturn(60L * 1000);

        blacksmith = Mockito.mock(Blacksmith.class);
        Mockito.when(blacksmith.get()) //
                .thenReturn("foo".toCharArray()) //
                .thenReturn("bar".toCharArray()) //
                .thenReturn("abc".toCharArray()) //
                .thenReturn("???".toCharArray()) //
                .thenReturn(null);

        attacker = Mockito.mock(Attacker.class);
        Mockito.when(attacker.test(Mockito.any(char[].class))).thenReturn(false);

        barrack = Mockito.mock(Barrack.class);
        Mockito.when(barrack.apply(Matchers.anyString())).thenReturn(attacker);

        target = new MastermindImpl(context, blacksmith, barrack);

    }

    @Test(timeout = 5000L)
    public void testCall_Found() throws Exception {

        Mockito.when(attacker.test("abc".toCharArray())).thenReturn(true);

        String result = target.call();

        Assert.assertEquals("abc", result);

    }

    @Test(timeout = 5000L)
    public void testCall_NotFound() throws Exception {

        String result = target.call();

        Assert.assertNull(result);

    }

    @Test(timeout = 5000L)
    public void testCall_Exception() throws Exception {

        Exception e = new IllegalArgumentException("unit test");

        Mockito.when(attacker.test("abc".toCharArray())).thenThrow(e);

        try {

            target.call();

            Assert.fail();

        } catch (CompletionException ce) {

            Assert.assertSame(e, ce.getCause());

        }


    }

    @Test(timeout = 5000L)
    public void testAdjustThreads() throws Exception {

        Assert.assertEquals(1, target.adjustThreads(-1));
        Assert.assertEquals(1, target.adjustThreads(0));
        Assert.assertEquals(1, target.adjustThreads(1));

        int processors = Runtime.getRuntime().availableProcessors();
        int max = processors * MastermindImpl.MULTIPLIER;
        Assert.assertEquals(processors, target.adjustThreads(processors));
        Assert.assertEquals(max, target.adjustThreads(max));
        Assert.assertEquals(max, target.adjustThreads(max + 1));

    }

    @Test(timeout = 5000L)
    public void testNewThread() throws Exception {

        Runnable r1 = Mockito.mock(Runnable.class);
        Thread t1 = target.newThread(r1);
        Assert.assertTrue(t1.isDaemon());
        Assert.assertEquals("MastermindImpl_1", t1.getName());

        Runnable r2 = Mockito.mock(Runnable.class);
        Thread t2 = target.newThread(r2);
        Assert.assertTrue(t2.isDaemon());
        Assert.assertEquals("MastermindImpl_2", t2.getName());

        Mockito.verifyZeroInteractions(r1, r2);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Mockito.verify(r1).run();
        Mockito.verify(r2).run();

    }

    @Test(timeout = 5000L)
    public void testCreateTask() throws Exception {

        Mockito.when(attacker.test("abc".toCharArray())).thenReturn(true);

        CompletableFuture<String> future = new CompletableFuture<>();

        Runnable task = target.createTask(context.getInputPath(), future);

        task.run();

        Assert.assertTrue(future.isDone());

        Assert.assertEquals("abc", future.get());

    }

    @Test(timeout = 5000L)
    public void testCreateTask_NotFound() throws Exception {

        CompletableFuture<String> future = new CompletableFuture<>();

        Runnable task = target.createTask(context.getInputPath(), future);

        task.run();

        Assert.assertFalse(future.isDone());

    }

    @Test(timeout = 5000L)
    public void testCreateTask_AlreadyDone() throws Exception {

        CompletableFuture<String> future = CompletableFuture.completedFuture("hoge");

        Runnable task = target.createTask(context.getInputPath(), future);

        task.run();

        Assert.assertTrue(future.isDone());

        Assert.assertEquals("hoge", future.get());

    }

}