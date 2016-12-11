package com.after_sunrise.brutus;

import com.after_sunrise.brutus.Blacksmith.BlacksmithContext;
import com.after_sunrise.brutus.impl.BarrackImpl;
import com.after_sunrise.brutus.impl.BlacksmithImpl;
import com.after_sunrise.brutus.impl.MastermindImpl;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Password cracker using brute force attack.
 * </p>
 * <p>
 * <i>'Brutus, you forgot your password too?'</i>
 * </p>
 *
 * @author takanori.takase
 */
public class Brutus extends AbstractModule implements BlacksmithContext, Mastermind.MastermindContext, Barrack.BarrackContext {

    @Parameter(names = "--help", help = true, hidden = true)
    boolean help = false;

    @Parameter(names = "-in", description = "Path of the input.", required = true)
    String inputPath;

    @Parameter(names = "-threads", description = "Number of concurrent threads to use. Defaults to number of processors.")
    int threads = Runtime.getRuntime().availableProcessors();

    @Parameter(names = "-timeout", description = "Timeout in millis to abort.")
    long timeoutInMillis = TimeUnit.DAYS.toMillis(7);

    @Parameter(names = "-min", description = "Minimum number of letters to attempt.")
    int minLength = 0;

    @Parameter(names = "-max", description = "Maximum number of letters to attempt.")
    int maxLength = 8;

    @Parameter(names = "-lower", arity = 1, description = "Flag to include lower-case characters. (a-z)")
    boolean includeLower = true;

    @Parameter(names = "-upper", arity = 1, description = "Flag to include upper-case characters. (A-Z)")
    boolean includeUpper = true;

    @Parameter(names = "-number", arity = 1, description = "Flag to include numeric characters. (0-9)")
    boolean includeNumber = true;

    @Parameter(names = "-symbol", arity = 1, description = "Flag to include symbol characters. (cf: !#$%&')")
    boolean includeSymbol = true;

    @Parameter(names = "-control", arity = 1, description = "Flag to include control characters. (cf: NUL SOH DEL)")
    boolean includeControl = false;

    @Parameter(names = "-native", arity = 1, description = "Use native library if available.")
    boolean useNative = false;

    @Override
    public String getInputPath() {
        return inputPath;
    }

    @Override
    public int getConcurrency() {
        return threads;
    }

    @Override
    public long getTimeoutInMillis() {
        return timeoutInMillis;
    }

    @Override
    public int getMinLength() {
        return minLength;
    }

    @Override
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public boolean isIncludeLower() {
        return includeLower;
    }

    @Override
    public boolean isIncludeUpper() {
        return includeUpper;
    }

    @Override
    public boolean isIncludeNumber() {
        return includeNumber;
    }

    @Override
    public boolean isIncludeSymbol() {
        return includeSymbol;
    }

    @Override
    public boolean isIncludeControl() {
        return includeControl;
    }

    @Override
    public boolean useNative() {
        return useNative;
    }

    @Override
    protected void configure() {

        bind(Barrack.BarrackContext.class).toInstance(this);

        bind(Barrack.class).to(BarrackImpl.class).asEagerSingleton();

        bind(BlacksmithContext.class).toInstance(this);

        bind(Blacksmith.class).to(BlacksmithImpl.class).asEagerSingleton();

        bind(Mastermind.MastermindContext.class).toInstance(this);

        bind(Mastermind.class).to(MastermindImpl.class).asEagerSingleton();

    }

    /**
     * Application entry point, parsing the command line arguments to be supplied as context objects.
     */
    public static void main(String[] args) {

        try {

            Brutus brutus = new Brutus();

            JCommander jc = new JCommander(brutus, args);

            if (brutus.help) {

                jc.setProgramName(Brutus.class.getName());

                jc.usage();

                return;

            }

            String result = Guice.createInjector(brutus).getInstance(Mastermind.class).call();

            LoggerFactory.getLogger(Brutus.class).info("Attack result. [{}]", result);

        } catch (Exception e) {

            LoggerFactory.getLogger(Brutus.class).error("Attack failed.", e);

        }

    }

}
