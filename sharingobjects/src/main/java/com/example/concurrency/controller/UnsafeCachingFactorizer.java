package com.example.concurrency.controller;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/factor")
public class UnsafeCachingFactorizer {

    private final AtomicReference<BigInteger> lastNumber = new AtomicReference<>();
    private final AtomicReference<BigInteger[]> lastFactors = new AtomicReference<>();

    @GetMapping("/test")
    public void test() {
        System.out.println("test");
    }

    @GetMapping("/")
    public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BigInteger i = extractFromRequest(req);
        if (i.equals(lastNumber.get()))
            encodeIntoResponse(resp, lastFactors.get());
        else {
            BigInteger[] factors = factor(i);
            lastNumber.set(i);
            lastFactors.set(factors);
            //remove null values from array
//            Arrays.stream(factors)
//                    .filter(Objects::nonNull).toArray(BigInteger[]::new);

            encodeIntoResponse(resp, Arrays.stream(factors).filter(Objects::nonNull).toArray(BigInteger[]::new));
        }
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
