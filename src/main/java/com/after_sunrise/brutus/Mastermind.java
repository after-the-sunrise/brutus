package com.after_sunrise.brutus;

import java.util.concurrent.Callable;

/**
 * <p>
 * Coordinate the brute-force attack.
 * </p>
 * <p>
 * <i>'Men at some time are masters of their fates.'</i>
 * </p>
 *
 * @author takanori.takase
 */
public interface Mastermind extends Callable<String> {

    /**
     * Preconditions for coordinating the attack.
     *
     * @author takanori.takase
     */
    interface MastermindContext {

        /**
         * @return Path of the input file.
         */
        String getInputPath();

        /**
         * @return Number of concurrent workers to use for concurrent attacks.
         */
        int getConcurrency();

        /**
         * @return Time-out threshold in millis to abort the attack.
         */
        long getTimeoutInMillis();

    }

    /**
     * Start the attack.
     *
     * @return Successful password if found, or {@code null} otherwise.
     * @throws Exception If the attack failed.
     */
    String call() throws Exception;

}
