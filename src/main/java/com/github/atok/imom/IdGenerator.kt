package com.github.atok.imom

import java.math.BigInteger
import java.security.SecureRandom

public object IdGenerator {
    public val DEFAULT_LENGTH = 26
    private val random = SecureRandom();

    public @Synchronized fun generate(length: Int = DEFAULT_LENGTH): String {
        return BigInteger(length * 5, random).toString(32);
    }
}