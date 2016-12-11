package com.after_sunrise.brutus.impl.txt;

import com.after_sunrise.brutus.Attacker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author takanori.takase
 */
public class TextBarrackTest {

    private TextBarrack target;

    @Before
    public void setUp() {
        target = new TextBarrack();
    }

    @Test
    public void apply() throws Exception {

        Attacker attacker = target.apply("src/test/resources/test.txt");

        assertTrue(attacker instanceof TextAttacker);

    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_Exception() throws Exception {

        target.apply("src/test/resources/unknown.txt");

    }

}