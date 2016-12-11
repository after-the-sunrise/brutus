package com.after_sunrise.brutus.impl.zip;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author takanori.takase
 */
public class Zip4jAttackerTest {

    @Test
    public void testToString() throws Exception {

        Zip4jAttacker target = new Zip4jAttacker("src/test/resources/hoge.zip");

        assertNotNull(target.toString());

    }

    @Test
    public void test() throws Exception {

        Zip4jAttacker target = new Zip4jAttacker("src/test/resources/hoge.zip");

        assertTrue(target.test("foo".toCharArray()));

        assertFalse(target.test("bar".toCharArray()));

    }

    @Test
    public void test_NoFile() throws Exception {

        try {

            new Zip4jAttacker("src/test/resources/unknown.zip");

            fail();

        } catch (IOException e) {

            assertEquals("Not a zip file.", e.getMessage());

        }


    }

}