package com.piedpiper.goodhabits.view.common;

import com.piedpiper.goodhabits.util.LongObfuscator;

public interface Request {
    default Long unmask(final Long number) {
        return number != null ? LongObfuscator.INSTANCE.unobfuscate(number) : null;
    }
}
