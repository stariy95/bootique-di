package io.bootique.di.spi;

import io.bootique.di.DIRuntimeException;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

class FieldInjectingDecoratorProvider<T> implements DecoratorProvider<T> {

    private Class<? extends T> implementation;
    private DefaultInjector injector;
    private DecoratorProvider<T> delegate;

    FieldInjectingDecoratorProvider(Class<? extends T> implementation, DecoratorProvider<T> delegate,
            DefaultInjector injector) {
        this.delegate = delegate;
        this.injector = injector;
        this.implementation = implementation;
    }

    @Override
    public Provider<T> get(final Provider<T> undecorated) throws DIRuntimeException {
        return new FieldInjectingProvider<T>(delegate.get(undecorated), injector) {

            @Override
            protected Object value(Field field, Annotation bindingAnnotation) {
                Class<?> fieldType = field.getType();

                // delegate (possibly) injected as Provider
                if (Provider.class.equals(fieldType)) {

                    Class<?> objectClass = DIUtil.parameterClass(field.getGenericType());

                    if (objectClass == null) {
                        throw new DIRuntimeException("Provider field %s.%s of type %s must be "
                                + "parameterized to be usable for injection", field.getDeclaringClass().getName(),
                                field.getName(), fieldType.getName());
                    }

                    if(objectClass.isAssignableFrom(implementation)) {
                        return undecorated;
                    }
                } else if (fieldType.isAssignableFrom(implementation)) {
                    return undecorated.get();
                }

                return super.value(field, bindingAnnotation);
            }
        };
    }
}
