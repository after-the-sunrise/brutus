package com.after_sunrise.brutus.impl.txt;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author takanori.takase
 */
public class TextAttackerTest {

    private TextAttacker target;

    @Before
    public void setUp() throws Exception {
        target = new TextAttacker("test");
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("TextAttacker{text=test}", target.toString());
    }

    @Test
    public void test() throws Exception {

        assertTrue(target.test("test".toCharArray()));

        assertFalse(target.test("hoge".toCharArray()));

    }

}