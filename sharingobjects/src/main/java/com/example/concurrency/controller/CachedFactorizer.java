package com.example.concurrency.controller;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import net.jcip.annotations.GuardedBy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/factor/v2")
public class CachedFactorizer {

    @GuardedBy("this")
    private BigInteger lastNumber;

    @GuardedBy("this")
    private BigInteger[] lastFactors;

    @GuardedBy("this")
    private long hits;

    @GuardedBy("this")
    private long cacheHits;

    public synchronized long getHits() {
        return hits;
    }

    public synchronized double getCacheHitRatio() {
        return (double) cacheHits / (double) hits;
    }

    @GetMapping("/")
    public void service(ServletRequest req, ServletResponse resp) throws IOException {
        BigInteger i = extractFromRequest(req);
        BigInteger[] factors = null;
        synchronized (this) {
            ++hits;
            if (i.equals(lastNumber)) {
                ++cacheHits;
                factors = lastFactors.clone();
            }
        }
        if (factors == null) {
            factors = factor(i);
            synchronized (this) {
                lastNumber = i;
                lastFactors = factors.clone(); }
        }
        encodeIntoResponse(resp, Arrays.stream(factors).filter(Objects::nonNull).toArray(BigInteger[]::new));
    }

    private BigInteger[] factor(BigInteger number) {
        System.out.println("Number int value :"+number.intValue()/2);
        BigInteger[] factor = new BigInteger[100];
        int index =0;

        for (int i = 1; i <= (number.intValue()/2); i++) {
            if(number.intValue() % i == 0) {
                factor[index++] = BigInteger.valueOf(i);
            }
        }
        return factor;
    }

    private void encodeIntoResponse(ServletResponse response, BigInteger[] factor) throws IOException {
        response.setContentType("text/plain");

        PrintWriter writer = response.getWriter();
        writer.write(Arrays.toString(factor));
        writer.flush();
        writer.close();
    }

    private BigInteger extractFromRequest(ServletRequest req) {
        String inputString = req.getParameter("number");
        System.out.println("Input String is : "+inputString);
        return new BigInteger(inputString);
    }

}
