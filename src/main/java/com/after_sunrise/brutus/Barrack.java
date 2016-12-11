package com.after_sunrise.brutus;

import java.util.function.Function;

/**
 * <p>
 * Factory for creating {@link Attacker} instances.
 * </p>
 * <p>
 * <i>'Conspiracy? It all sounds greek to me.'</i>
 * </p>
 *
 * @author takanori.takase
 */
public interface Barrack extends Function<String, Attacker> {


    /**
     * Criteria for preparing {@link Attacker} instance.
     *
     * @author takanori.takase
     */
    interface BarrackContext {

        /**
         * @return Use native library if available.
         */
        boolean useNative();

    }

    /**
     * Generate an attacker instance for the given file.
     *
     * @param path Path of the file for that attacker to attack.
     * @return New attacker instance.
     */
    @Override
    Attacker apply(String path);

}
