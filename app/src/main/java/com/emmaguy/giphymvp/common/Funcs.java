package com.emmaguy.giphymvp.common;

import rx.functions.Func1;

public final class Funcs {
    private Funcs() {
        throw new AssertionError("No instances.");
    }

    public static <T> Func1<T, Boolean> not(final Func1<T, Boolean> func) {
        return value -> !func.call(value);
    }
}
