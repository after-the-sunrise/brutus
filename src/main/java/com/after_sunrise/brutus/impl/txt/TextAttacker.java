package com.after_sunrise.brutus.impl.txt;

import com.after_sunrise.brutus.Attacker;

import java.util.Arrays;

/**
 * {@link Attacker} implementation which simply compares the input with a predefined text.
 *
 * @author takanori.takase
 */
public class TextAttacker implements Attacker {

    private final char[] text;

    public TextAttacker(String text) {
        this.text = text.toCharArray();
    }

    @Override
    public String toString() {
        return String.format("%s{text=%s}", //
                getClass().getSimpleName(), String.valueOf(text));
    }

    @Override
    public boolean test(char[] knife) {
        return Arrays.equals(text, knife);
    }

}
