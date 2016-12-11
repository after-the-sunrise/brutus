package com.after_sunrise.brutus.impl.txt;

import com.after_sunrise.brutus.Attacker;
import com.after_sunrise.brutus.Barrack;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Factory to create {@link Attacker} instances for text files.
 * Note that only the first line of the text file will be used for comparison.
 *
 * @author takanori.takase
 */
public class TextBarrack implements Barrack {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Attacker apply(String path) {

        try {

            File file = new File(path);

            String line = Files.readFirstLine(file, StandardCharsets.UTF_8);

            return new TextAttacker(line);

        } catch (IOException e) {

            log.warn("Could not instantiate an attacker. Reason = {}", e.getMessage());

            throw new IllegalArgumentException(e);

        }

    }

}
