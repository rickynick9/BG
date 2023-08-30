package com.example.concurrency.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
Memoizer2 in Listing improves on the awful concurrent behavior of Memoizer1 by replacing the HashMap with a
ConcurrentHashMap. Since ConcurrentHashMap is thread-safe, there is no need to synchronize when accessing the
backing Map.

It still has some defects as a cache there is a window of vulnerability in which two threads calling compute at the
same time could end up computing the same value. In the case of memoization, this is merely inefficient—the purpose of
a cache is to prevent the same data from being calculated multiple times.

The problem with Memoizer2 is that if one thread starts an expensive computation, other threads are not aware that
the computation is in progress and so may start the same computation.

Thread X is currently computing f (27)”, so that if another thread arrives looking for f (27), it knows that the most
efficient way to find it is to head over to Thread X’s house, hang out there until X is finished, and then
ask “Hey, what did you get for f (27)?”

A class that does almost exactly this: FutureTask. FutureTask represents a computational process that may or may not
already have completed. FutureTask.get returns the result of the computation immediately if it is available; otherwise
it blocks until the result has been computed and then returns it.

Memoizer3 in redefines the backing Map for the value cache as a ConcurrentHashMap<A,Future<V>> instead of
a ConcurrentHashMap<A,V>.

 */
public class Memoizer2<A, V> implements Computable<A, V> {
    private final Map<A, V> cache = new ConcurrentHashMap<A, V>();
    private final Computable<A, V> c;
    public Memoizer2(Computable<A, V> c) {
        this.c = c;
    }
    public V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
