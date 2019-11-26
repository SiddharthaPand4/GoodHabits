package io.synlabs.synvision.views.common;


import io.synlabs.synvision.util.LongObfuscator;

public interface Request {

    default Long unmask(final Long number) {
        return number != null ? LongObfuscator.INSTANCE.unobfuscate(number) : null;
    }
}