package com.piedpiper.goodhabits.view.common;

import com.piedpiper.goodhabits.util.LongObfuscator;

public interface Response {
    default Long mask(final Long number) {
        return number != null ? LongObfuscator.INSTANCE.obfuscate(number) : null;
    }
}
