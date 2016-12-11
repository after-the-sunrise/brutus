package com.after_sunrise.brutus.impl.zip;

import com.after_sunrise.brutus.Attacker;
import com.google.common.annotations.VisibleForTesting;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * {@link Attacker} implementation for Zip files using Zip4j.
 *
 * @author takanori.takase
 */
public class Zip4jAttacker implements Attacker {

    private static final String TEMPDIR = "java.io.tmpdir";

    private final ZipFile input;

    private final String output;

    public Zip4jAttacker(String input) throws ZipException, IOException {

        this.input = toInput(input);

        this.output = toOutput(input);

    }

    @VisibleForTesting
    private ZipFile toInput(String path) throws ZipException, IOException {

        ZipFile zipFile = new ZipFile(path);

        if (!zipFile.isValidZipFile()) {

            throw new IOException("Not a zip file.");

        }

        return zipFile;

    }

    @VisibleForTesting
    private String toOutput(String path) {

        String name = Paths.get(path).getFileName().toString();

        String temp = System.getProperty(TEMPDIR);

        Path output = Paths.get(temp, getClass().getSimpleName(), name);

        return output.toAbsolutePath().toString();

    }

    @Override
    public String toString() {
        return String.format("%s{input=%s, output=%s}", //
                getClass().getSimpleName(), input.getFile(), output);
    }

    @Override
    public boolean test(char[] knife) {

        try {

            input.setPassword(knife);

            input.extractAll(output);

            return true;

        } catch (ZipException e) {

            return false;

        }

    }

}
