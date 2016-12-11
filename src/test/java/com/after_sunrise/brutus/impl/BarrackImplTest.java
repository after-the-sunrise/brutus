package com.after_sunrise.brutus.impl;

import com.after_sunrise.brutus.Barrack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author takanori.takase
 */
public class BarrackImplTest {

    private BarrackImpl target;

    private Barrack.BarrackContext context;

    @Before
    public void setUp() throws Exception {

        context = Mockito.mock(Barrack.BarrackContext.class);

        target = new BarrackImpl(context);

    }

    @Test
    public void testApply() throws Exception {

        Assert.assertNotNull(target.apply("src/test/resources/test.txt"));

        Assert.assertNotNull(target.apply("src/test/resources/hoge.zip"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testApply_Exception() throws Exception {

        Assert.assertNotNull(target.apply("src/test/resources/test.dat"));

    }

    @Test
    public void testExtractSuffix() {

        Assert.assertEquals("txt", target.extractSuffix("foo/bar/hoge.txt"));
        Assert.assertEquals("txt", target.extractSuffix("foo/bar/hoge.TXT"));
        Assert.assertEquals("txt", target.extractSuffix("foo.bar.hoge.txt"));
        Assert.assertEquals("txt", target.extractSuffix("foo.bar.hoge.TXT"));

        Assert.assertEquals("zip", target.extractSuffix("foo/bar/hoge.zip"));
        Assert.assertEquals("zip", target.extractSuffix("foo/bar/hoge.ZIP"));
        Assert.assertEquals("zip", target.extractSuffix("foo.bar.hoge.zip"));
        Assert.assertEquals("zip", target.extractSuffix("foo.bar.hoge.ZIP"));

        Assert.assertEquals("dat", target.extractSuffix("foo/bar/hoge.dat"));
        Assert.assertEquals("dat", target.extractSuffix("foo/bar/hoge.DAT"));
        Assert.assertEquals("dat", target.extractSuffix("foo.bar.hoge.dat"));
        Assert.assertEquals("dat", target.extractSuffix("foo.bar.hoge.DAT"));

        Assert.assertNull(target.extractSuffix(null));
        Assert.assertNull(target.extractSuffix(""));
        Assert.assertNull(target.extractSuffix("."));
        Assert.assertNull(target.extractSuffix("foo/bar/hoge"));
        Assert.assertNull(target.extractSuffix("foo/bar/hoge."));

    }

}