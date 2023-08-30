package com.example.concurrency.cache;

import java.math.BigInteger;

/*
 ExpensiveFunction, which implements Computable, takes a long time to compute its result; we’d like to
 create a Computable wrapper that remembers the results of previous computations and encapsulates the
 caching process. (This technique is known as memoization.)
 */
public class ExpensiveFunction implements Computable<String, BigInteger> {

    @Override
    public BigInteger compute(String arg) {
        // after deep thought...
        return new BigInteger(arg);
    }
}
