package com.after_sunrise.brutus.impl;

import com.after_sunrise.brutus.Attacker;
import com.after_sunrise.brutus.Barrack;
import com.after_sunrise.brutus.impl.pdf.PdfBarrack;
import com.after_sunrise.brutus.impl.txt.TextBarrack;
import com.after_sunrise.brutus.impl.zip.ZipBarrack;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Delegate method invocation to other {@link Barrack} by the path suffix.
 *
 * @author takanori.takase
 */
public class BarrackImpl implements Barrack {

    private static final char DELIMITER = '.';

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<String, Barrack> delegates;

    @Inject
    public BarrackImpl(BarrackContext context) {

        this.delegates = new HashMap<>();

        this.delegates.put("txt", new TextBarrack());

        this.delegates.put("zip", new ZipBarrack(context));

        this.delegates.put("pdf", new PdfBarrack(context));

    }

    @Override
    public Attacker apply(String path) {

        String suffix = extractSuffix(path);

        Barrack barrack = delegates.get(suffix);

        if (barrack == null) {

            String msg = "No implementation found for suffix : " + suffix;

            throw new IllegalArgumentException(msg);

        }

        log.debug("Delegating for suffix '{}' : {}", suffix, barrack);

        return barrack.apply(path);

    }

    @VisibleForTesting
    String extractSuffix(String path) {

        if (path == null) {
            return null;
        }

        int index = path.lastIndexOf(DELIMITER);

        if (index < 0) {
            return null;
        }

        if (index == path.length() - 1) {
            return null;
        }

        return path.substring(index + 1).toLowerCase();

    }

}
