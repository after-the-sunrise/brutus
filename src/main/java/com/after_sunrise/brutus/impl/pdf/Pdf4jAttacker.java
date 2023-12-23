package com.after_sunrise.brutus.impl.pdf;

import com.after_sunrise.brutus.Attacker;
import com.google.common.annotations.VisibleForTesting;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

/**
 * {@link Attacker} implementation for Pdf files using PDFBox.
 *
 * @author takanori.takase
 */
public class Pdf4jAttacker implements Attacker {

    private final File input;

    public Pdf4jAttacker(String input) throws IOException {

        this.input = toInput(input);

    }

    @VisibleForTesting
    private File toInput(String path) throws IOException {

        return new File(path);

    }

    @Override
    public String toString() {
        return String.format("%s{input=%s}", getClass().getSimpleName(), input);
    }

    @Override
    public boolean test(char[] knife) {

        String password = new String(knife);

        try (PDDocument doc = Loader.loadPDF(input, password)) {

            doc.setAllSecurityToBeRemoved(true);

        } catch (IOException e) {

            return false;

        }

        return true;

    }

}
