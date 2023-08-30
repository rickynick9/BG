package com.example.concurrency.controller;

import com.example.concurrency.cache.Computable;
import com.example.concurrency.cache.Memoizer4;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;

@RestController
@RequestMapping("/factor/v3")
public class Factorizer {

    private final Computable<BigInteger, BigInteger[]> c = (arg) -> factor(arg);
    final Computable<BigInteger, BigInteger[]> cache = new Memoizer4<>(c);

    @GetMapping("/")
    public void service(ServletRequest req, ServletResponse resp) {
        try {
            BigInteger i = extractFromRequest(req);
            encodeIntoResponse(resp, cache.compute(i));
        } catch (InterruptedException | IOException e) {
            //encodeError(resp, "factorization interrupted");
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
