package com.after_sunrise.brutus.impl.pdf;

import com.after_sunrise.brutus.Attacker;
import com.after_sunrise.brutus.Barrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Factory to create {@link Attacker} instances for Pdf files.
 *
 * @author takanori.takase
 */
public class PdfBarrack implements Barrack {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BarrackContext context;

    public PdfBarrack(BarrackContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public Attacker apply(String path) {

        try {

            checkPath(path);

            return new Pdf4jAttacker(path);

        } catch (Exception e) {

            log.warn("Could not instantiate an attacker. Reason = {}", e.getMessage());

            throw new IllegalArgumentException(e);

        }

    }

    private void checkPath(String path) throws IOException {

        File file = new File(path);

        if (!file.exists()) {
            throw new IOException("File does not exist.");
        }

        if (file.isDirectory()) {
            throw new IOException("File is directory.");
        }

    }

}
