package com.after_sunrise.brutus;

import java.util.function.Predicate;

/**
 * <p>
 * Execute the actual attack with the given passwords.
 * </p>
 * <p>
 * <i>'Cowards die many times before their deaths.'</i>
 * </p>
 *
 * @author takanori.takase
 */
public interface Attacker extends Predicate<char[]> {

    /**
     * Execute the actual attack with the given password.
     *
     * @param knife Password used for the attack.
     * @return {@code true} if the attack succeeded, otherwise {@code false}.
     */
    @Override
    boolean test(char[] knife);

}
