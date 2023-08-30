package com.example.concurrency.deadlock;

import java.util.Random;

/*
jps command to find process ID :
jps
16512 RemoteMavenServer36
47782 Jps
16461
47773 Launcher
47774 DemonstrateDeadlock

jcmd command to print thread dump
jcmd 47774 Thread.print

47774:
2023-08-10 11:42:29
Full thread dump OpenJDK 64-Bit Server VM (17.0.8+0 mixed mode, sharing):

Threads class SMR info:
Found one Java-level deadlock:
=============================
"Thread-0":
  waiting to lock monitor 0x0000600002f180d0 (object 0x000000070fdd2db0, a com.example.concurrency.deadlock.Account),
  which is held by "Thread-1"

"Thread-1":
  waiting to lock monitor 0x0000600002f0c0d0 (object 0x000000070fdd2dd8, a com.example.concurrency.deadlock.Account),
  which is held by "Thread-0"

 */

public class DemonstrateDeadlock {

    private static final int NUM_THREADS = 20;
    private static final int NUM_ACCOUNTS = 5;
    private static final int NUM_ITERATIONS = 1000000;

    private static final Object tieLock = new Object();

    public static void main(String[] args) {
        final Random rnd = new Random();
        final Account[] accounts = new Account[NUM_ACCOUNTS];
        NotRepeatingRandom gen = new NotRepeatingRandom(10);

        for (int i = 0; i < accounts.length; i++)
            accounts[i] = new Account(gen.nextInt(), 1000000);

        class TransferThread extends Thread {
            public void run() {
                for (int i = 0; i < NUM_ITERATIONS; i++) {
                    int fromAcct = rnd.nextInt(NUM_ACCOUNTS);
                    int toAcct = rnd.nextInt(NUM_ACCOUNTS);
                    Amount amount = new Amount(rnd.nextInt(100));
                    try {
                        DemonstrateDeadlock.transferMoney(accounts[fromAcct], accounts[toAcct], amount);
                        //DemonstrateDeadlock.transferMoneyO(accounts[fromAcct], accounts[toAcct], amount);
                    } catch (InsufficientFundsException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        for (int i = 0; i < NUM_THREADS; i++)
            new TransferThread().start();

    }

    private static void transferMoney(Account fromAccount, Account toAcct, Amount amount) throws InsufficientFundsException {
        synchronized (fromAccount) {
            synchronized (toAcct) {
                if(fromAccount.getAccountBalance().compareTo(amount.getAmount()) < 0)
                    throw new InsufficientFundsException("Account Balance :"+fromAccount.getAccountBalance());
                else {
                    fromAccount.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }

    }

    public static void transferMoneyO(final Account fromAcct, final Account toAcct, Amount amount)
            throws InsufficientFundsException {
        class Helper {
            public void transfer() throws InsufficientFundsException {
                if (fromAcct.getAccountBalance().compareTo(amount.getAmount()) < 0)
                    throw new InsufficientFundsException("Account Balance :"+fromAcct.getAccountBalance());
                else {
                    fromAcct.debit(amount);
                    toAcct.credit(amount);
                }
            }
        }
        int fromHash = System.identityHashCode(fromAcct);
        int toHash = System.identityHashCode(toAcct);

        if (fromHash < toHash) {
            synchronized (fromAcct) {
                synchronized (toAcct) {
                    new Helper().transfer();
                }
            }
        } else if (fromHash > toHash) {
            synchronized (toAcct) {
                synchronized (fromAcct) {
                    new Helper().transfer();
                }
            }
        } else {
            synchronized (tieLock) {
                synchronized (fromAcct) {
                    synchronized (toAcct) {
                        new Helper().transfer();
                    }
                }
            }
        }
    }
}
