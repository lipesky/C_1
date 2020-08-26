package com.example.c_1;

// The first type(T) is the return type, and the second type(A) is the args type
public interface Callback<T, A> {
    public T call(A args);
}
