package com.example.concurrency.cache;

/*
The Computable<A,V> interface describes a function with input of type A and result of type V.
 */
public interface Computable<A, V> {
    V compute(A arg) throws InterruptedException;
}
