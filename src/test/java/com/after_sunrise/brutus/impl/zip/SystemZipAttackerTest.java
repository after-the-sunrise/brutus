package com.after_sunrise.brutus.impl.zip;

import com.after_sunrise.brutus.Attacker;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author takanori.takase
 */
public class SystemZipAttackerTest {

    @Test(timeout = 5000L)
    public void testIsAvailable() throws Exception {

        // Either true or false, depending on the testing machine.
        SystemZipAttacker.isAvailable();

        // Command not finished. (Could fail due to 'sleep' command not available.)
        assertFalse(SystemZipAttacker.isAvailable(new String[]{"sleep", "10"}, 1L));

        // Command does not exist.
        assertFalse(SystemZipAttacker.isAvailable(new String[]{"unzip_test", "-v"}, 1000L));

    }

    @Test
    public void testToString() {

        String absolute = Paths.get("src/test/resources/hoge.zip").toAbsolutePath().toString();

        assertEquals("SystemZipAttacker{input=" + absolute + "}", //
                new SystemZipAttacker("src/test/resources/hoge.zip").toString());

    }

    @Test
    public void test() throws Exception {

        Attacker target = new SystemZipAttacker("src/test/resources/hoge.zip");

        assertTrue(target.test("foo".toCharArray()));

        assertFalse(target.test("bar".toCharArray()));

    }

}