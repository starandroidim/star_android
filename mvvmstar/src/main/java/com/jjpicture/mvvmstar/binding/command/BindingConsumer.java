package com.jjpicture.mvvmstar.binding.command;

public interface BindingConsumer<T> {
    void call(T t);
}
