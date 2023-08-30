package com.example.concurrency.cache;

import java.util.Map;
import java.util.concurrent.*;

import static com.example.concurrency.cache.LaunderThrowable.launderThrowable;

/*
Memoizer3 redefines the backing Map for the value cache as a ConcurrentHashMap<A,Future<V>> instead of
a ConcurrentHashMap<A,V>.

The result might be available immediately or might be in the process of being computed—but this is transparent to the
caller of Future.get.

The Memoizer3 implementation is almost perfect: it exhibits very good concurrency (mostly derived from the excellent
concurrency of ConcurrentHashMap), the result is returned efficiently if it is already known, and if the computation
is in progress by another thread, newly arriving threads wait patiently for the result.

It has only one defect—there is still a small window of vulnerability in which two threads might compute the same value.
This window is far smaller than in Memoizer2, but because the if block in compute is still a nonatomic check-then-act
sequence, it is possible for two threads to call compute with the same value at roughly the same time, both see that
the cache does not contain the desired value, and both start the computation.
 */
public class Memoizer3<A, V> implements Computable<A, V> {

    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer3(Computable<A, V> c) {
        this.c = c;
    }
    @Override
    public V compute(final A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if(f == null) {
            Callable<V> eval = () -> c.compute(arg);
            FutureTask<V> ft = new FutureTask<>(eval);
            f = ft;
            cache.put(arg, ft);
            ft.run(); // call to c.compute happens here
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }
}
