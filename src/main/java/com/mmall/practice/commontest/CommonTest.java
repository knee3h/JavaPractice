package com.mmall.practice.commontest;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
public class CommonTest {

    private static int threadNumTotal = 200;
    private static int clientNum = 5000;

    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        final Semaphore semp = new Semaphore(threadNumTotal);
        for (int index = 0; index < clientNum; index++) {
            final int threadNum = index;
            exec.execute(() -> {
                try {
                    semp.acquire();
                    func(threadNum);
                    semp.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
            });
        }
        exec.shutdown();
    }

    public static void func(int threadNum) {
        log.info("Thread:{}", threadNum);
    }
}
