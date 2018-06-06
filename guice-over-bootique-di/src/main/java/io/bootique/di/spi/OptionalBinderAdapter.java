package io.bootique.di.spi;

import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;

public class OptionalBinderAdapter<T> {

    private final BinderAdapter guiceBinderAdapter;
    private final Key<T> key;

    public OptionalBinderAdapter(com.google.inject.Binder guiceBinder, Key<T> key) {
        this.key = key;
        this.guiceBinderAdapter = (BinderAdapter) guiceBinder;
    }

    public LinkedBindingBuilder<T> setDefault() {
        return new BindingBuilderAdapter<>(guiceBinderAdapter, ConversionUtils.toBootiqueKey(key));
    }

    public LinkedBindingBuilder<T> setBinding() {
        return new BindingBuilderAdapter<>(guiceBinderAdapter, ConversionUtils.toBootiqueKey(key));
    }
}
