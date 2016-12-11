package com.after_sunrise.brutus.impl.zip;

import com.after_sunrise.brutus.Attacker;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * {@link Attacker} implementation for Zip files using the system's 'unzip' command.
 *
 * @author takanori.takase
 */
public class SystemZipAttacker implements Attacker {

    static final String EXEC = "unzip";

    static final String ARG_VERSION = "-v";

    static final String ARG_PASSWORD = "-P";

    static final String ARG_TEST = "-t";

    static final int SUCCESS = 0;

    static final long WAIT_MILLIS = 5000L;

    static boolean isAvailable() {
        return isAvailable(new String[]{EXEC, ARG_VERSION}, WAIT_MILLIS);
    }

    @VisibleForTesting
    static boolean isAvailable(String[] commands, long wait) {

        try {

            Process process = Runtime.getRuntime().exec(commands);

            boolean terminated = process.waitFor(wait, TimeUnit.MILLISECONDS);

            return terminated && process.exitValue() == SUCCESS;

        } catch (Exception e) {

            String msg = "System library not available : {}";

            LoggerFactory.getLogger(SystemZipAttacker.class).debug(msg, e.getMessage());

            return false;

        }

    }

    private final String input;

    public SystemZipAttacker(String input) {
        this.input = Paths.get(input).toAbsolutePath().toString();
    }

    @Override
    public String toString() {
        return String.format("%s{input=%s}", getClass().getSimpleName(), input);
    }

    @Override
    public boolean test(char[] knife) {

        try {

            String p = String.valueOf(knife);

            // unzip -t -P $PASSWORD $INPUT
            Process process = Runtime.getRuntime().exec( //
                    new String[]{EXEC, ARG_TEST, ARG_PASSWORD, p, input});

            process.waitFor();

            return process.exitValue() == SUCCESS;

        } catch (Exception e) {

            return false;

        }

    }

}

