package com.after_sunrise.brutus.impl.zip;

import com.after_sunrise.brutus.Attacker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

/**
 * @author takanori.takase
 */
public class ZipBarrackTest {

    private ZipBarrack target;

    private ZipBarrack.BarrackContext context;

    @Before
    public void setUp() throws Exception {

        context = Mockito.mock(ZipBarrack.BarrackContext.class);

        target = new ZipBarrack(context);

    }


    @Test
    public void testApply_Zip4j() throws Exception {

        Mockito.when(context.useNative()).thenReturn(false);

        Attacker attacker = target.apply("src/test/resources/hoge.zip");

        Assert.assertTrue(Zip4jAttacker.class.isInstance(attacker));

    }

    @Test
    public void testApply_Native() throws Exception {

        Mockito.when(context.useNative()).thenReturn(true);

        Attacker attacker = target.apply("src/test/resources/hoge.zip");

        if (SystemZipAttacker.isAvailable()) {

            Assert.assertTrue(SystemZipAttacker.class.isInstance(attacker));

        } else {

            Assert.assertTrue(Zip4jAttacker.class.isInstance(attacker));

        }

    }


    @Test
    public void testApply_NotFound() throws Exception {

        try {

            target.apply("src/test/resources/unknown.zip");

            Assert.fail();

        } catch (RuntimeException e) {

            Throwable t = e.getCause();

            Assert.assertEquals(t.toString(), IOException.class, t.getClass());

        }

    }

    @Test
    public void testApply_Directory() throws Exception {

        try {

            target.apply("target");

            Assert.fail();

        } catch (RuntimeException e) {

            Throwable t = e.getCause();

            Assert.assertEquals(t.toString(), IOException.class, t.getClass());

        }

    }

}