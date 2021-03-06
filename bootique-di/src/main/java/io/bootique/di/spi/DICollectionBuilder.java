package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;
import io.bootique.di.Key;
import io.bootique.di.Scope;

import javax.inject.Provider;

/**
 * A superclass of DI List and Map builders.
 *
 * @param <K> DI key type.
 * @param <E> Collection element type.
 */
public abstract class DICollectionBuilder<K, E> {

    protected DefaultInjector injector;
    protected Key<K> bindingKey;

    public DICollectionBuilder(Key<K> bindingKey, DefaultInjector injector) {
        this.injector = injector;
        this.bindingKey = bindingKey;
    }

    protected Provider<E> createInstanceProvider(E value) {
        Provider<E> provider0 = new InstanceProvider<>(value);
        Provider<E> provider1 =  new FieldInjectingProvider<>(provider0, injector);
        if(!injector.isMethodInjectionEnabled()) {
            return provider1;
        }
        return new MethodInjectingProvider<>(provider1, injector);
    }

    protected <SubT extends E> Provider<SubT> createTypeProvider(final Class<SubT> interfaceType) throws DIRuntimeException {

        // Create deferred provider to prevent caching the intermediate provider from the Injector.
        // The actual provider may get overridden after list builder is created.
        return () -> findOrCreateBinding(interfaceType).getScoped().get();
    }

    protected <SubT extends E> Provider<SubT> getByKeyProvider(final Key<SubT> key) throws DIRuntimeException {
        // Create deferred provider to prevent caching the intermediate provider from the Injector.
        // The actual provider may get overridden after list builder is created.
        return () -> injector.getProvider(key).get();
    }

    protected <SubT extends E> Binding<SubT> findOrCreateBinding(Class<SubT> interfaceType) {

        Key<SubT> key = Key.get(interfaceType);
        Binding<SubT> binding = injector.getBinding(key);

        if (binding == null) {
            Provider<SubT> provider0 = new ConstructorInjectingProvider<>(interfaceType, injector);
            Provider<SubT> provider1 = new FieldInjectingProvider<>(provider0, injector);
            if(injector.isMethodInjectionEnabled()) {
                provider1 = new MethodInjectingProvider<>(provider1, injector);
            }
            injector.putBinding(key, provider1);

            binding = injector.getBinding(key);
        }

        return binding;
    }

    public void in(Scope scope) {
        injector.changeBindingScope(bindingKey, scope);
    }

    public void inSingleton() {
        in(injector.getSingletonScope());
    }
}
