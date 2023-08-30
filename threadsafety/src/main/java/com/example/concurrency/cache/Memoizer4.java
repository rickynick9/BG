package com.example.concurrency.cache;

import java.util.Map;
import java.util.concurrent.*;

import static com.example.concurrency.cache.LaunderThrowable.launderThrowable;

/*

Caching a Future instead of a value creates the possibility of cache pollution: if a computation is cancelled or fails,
future attempts to compute the result will also indicate cancellation or failure. To avoid this, Memoizer removes the
Future from the cache if it detects that the computation was cancelled; it might also be desirable to remove the Future
upon detecting a RuntimeException if the computation might succeed on a future attempt.

Future Improvements :
Memoizer also does not address cache expiration, but this could be accomplished by using a subclass of FutureTask
that associates an expiration time with each result and periodically scanning the cache for expired entries.

Similarly, it does not address cache eviction, where old entries are removed to make room for new ones so that the
cache does not consume too much memory.

 */
public class Memoizer4<A, V> implements Computable<A, V> {

    private final Map<A, Future<V>> cache = new ConcurrentHashMap<>();
    private final Computable<A, V> c;

    public Memoizer4(Computable<A, V> c) {
        this.c = c;
    }
    @Override
    public V compute(final A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if(f == null) {
            Callable<V> eval = () -> c.compute(arg);
            FutureTask<V> ft = new FutureTask<>(eval);
            f = cache.putIfAbsent(arg, ft);
            // By definition putIfAbsent return old value and not new value (old value for absent is always null).
            if(f == null) {
                f = ft; ft.run();
            }
        }
        try {
            return f.get();
        } catch (CancellationException e) {
            cache.remove(arg, f);
            throw launderThrowable(e.getCause());
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

}
