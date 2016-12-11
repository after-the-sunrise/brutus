package com.after_sunrise.brutus;

import org.junit.Test;

/**
 * Attack the test files.
 *
 * @author takanori.takase
 */
public class BrutusTest {

    @Test(timeout = 60000L)
    public void testMain_Text() throws Exception {

        String[] args = { //
                "-in", "src/test/resources/test.txt" //
        };

        Brutus.main(args);

    }

    @Test(timeout = 60000L)
    public void testMain_Text_NotFound() throws Exception {

        String[] args = { //
                "-in", "src/test/resources/test.txt", //
                "-min", "2", //
                "-max", "3" //
        };

        Brutus.main(args);

    }

    @Test(timeout = 60000L)
    public void testMain_Zip() throws Exception {

        String[] args = { //
                "-in", "src/test/resources/hoge.zip", //
                "-lower", "true", //
                "-upper", "false", //
                "-number", "false", //
                "-symbol", "false", //
                "-control", "false", //
                "-min", "3", //
                "-max", "3" // Password is "foo"
        };

        Brutus.main(args);

    }

    @Test(timeout = 60000L)
    public void testMain_Zip_NotFound() throws Exception {

        String[] args = { //
                "-in", "src/test/resources/hoge.zip", //
                "-lower", "false", //
                "-upper", "false", //
                "-number", "true", //
                "-symbol", "false", //
                "-control", "false", //
                "-min", "1", //
                "-max", "1" // Password is "foo"
        };

        Brutus.main(args);

    }

    @Test(timeout = 60000L)
    public void testMain_NoFile() throws Exception {

        String[] args = {"-in", "src/test/resources/unknown.txt"};

        Brutus.main(args);

    }

    @Test(timeout = 5000L)
    public void testMain_Help() throws Exception {

        Brutus.main(new String[]{"--help"});

    }

}
