package com.example.concurrency.deadlock;

public class Amount {
    private final Integer amount;

    public Amount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}
