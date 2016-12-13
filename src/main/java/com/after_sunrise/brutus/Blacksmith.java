package com.after_sunrise.brutus;

import java.util.function.Supplier;

/**
 * <p>
 * Supply a stream of passwords.
 * </p>
 * <p>
 * An unique password is supplied for each {@link Blacksmith#get()} method invocation,
 * and a same password is given only once throughout the lifetime of this instance,
 * meaning there is no undo-ing the method invocation.
 * </p>
 * <p>
 * <i>'The pen is mightier than the sword.'</i>
 * </p>
 *
 * @author takanori.takase
 */
public interface Blacksmith extends Supplier<char[]> {

    /**
     * Criteria for composing the password character combination.
     *
     * @author takanori.takase
     */
    interface BlacksmithContext {

        /**
         * @return Minimum length of the password.  (inclusive)
         */
        int getMinLength();

        /**
         * @return Maximum length of the password. (inclusive)
         */
        int getMaxLength();

        /**
         * @return Include lower-case alphabets.  [a-z]
         */
        boolean isIncludeLower();

        /**
         * @return Include upper-case alphabets. [A-Z]
         */
        boolean isIncludeUpper();

        /**
         * @return Include numbers. [0-9]
         */
        boolean isIncludeNumber();

        /**
         * @return Include printable symbols, such as sharp(#) and plus(+).
         */
        boolean isIncludeSymbol();


        /**
         * @return Include non-printable control characters, such as NUL and DEL.
         */
        boolean isIncludeControl();

    }

    /**
     * Retrieve the next password.
     *
     * @return Password text, or {@code null} if no more passwords are available.
     */
    @Override
    char[] get();

}
