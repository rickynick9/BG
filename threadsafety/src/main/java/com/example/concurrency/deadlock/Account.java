package com.example.concurrency.deadlock;

public class Account {

    final private Integer accountNo;
    private Integer accountBalance;

    public Account(Integer accountNo, Integer accountBalance) {
        this.accountNo = accountNo;
        this.accountBalance = accountBalance;
    }

    public Integer credit(Amount creditAmount) {
        this.accountBalance +=creditAmount.getAmount();
        return this.accountBalance;
    }

    public Integer debit(Amount debitAmount) {
        this.accountBalance -=debitAmount.getAmount();
        return this.accountBalance;
    }

    public Integer getAccountBalance() {
        return this.accountBalance;
    }

}
