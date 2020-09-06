package com.piedpiper.goodhabits.util;

import java.math.BigInteger;

public class LongObfuscator {
    public static final LongObfuscator INSTANCE = new LongObfuscator();

    private static final BigInteger OBFUSCATION_FACTOR = BigInteger.valueOf(387420489713793L);
    private static final BigInteger OBFUSCATION_INVERSE = BigInteger.valueOf(975344238966657L);
    private static final BigInteger OBFUSCATION_PIVOT = BigInteger.valueOf(1000000000000000L);

    private LongObfuscator() {
    }

    public final Long obfuscate(final Long number) {
        return modulus(BigInteger.valueOf(number).multiply(OBFUSCATION_FACTOR).remainder(OBFUSCATION_PIVOT), OBFUSCATION_PIVOT).longValue();
    }

    public final Long unobfuscate(final Long number) {
        return modulus(BigInteger.valueOf(number).multiply(OBFUSCATION_INVERSE).remainder(OBFUSCATION_PIVOT), OBFUSCATION_PIVOT).longValue();
    }

    private static BigInteger modulus(final BigInteger dividend, final BigInteger divisor) {
        final BigInteger m = dividend.remainder(divisor);

        return isNegative(m) ? m.add(divisor) : m;
    }

    private static boolean isNegative(final BigInteger a) {
        return a.compareTo(BigInteger.ZERO) < 0;
    }
}
