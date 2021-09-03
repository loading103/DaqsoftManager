package com.daqsoft.mvvmfoundation.binding.command;

public interface BindingConsumer<T> {
    void call(T t);
}
