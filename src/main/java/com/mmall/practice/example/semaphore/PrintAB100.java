package com.mmall.practice.example.semaphore;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

@Slf4j
public class PrintAB100 {
    private final static int N =100;
    private final static Semaphore semaphoreA = new Semaphore(0);
    private final static Semaphore semaphoreB = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i=0;i<N; i++){
                        semaphoreA.acquire(1);
                        if (i%2==0){
                            System.out.println("A"+i);
                        }
                        semaphoreB.release();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i=0;i<N; i++){
                        semaphoreB.acquire(1);
                        if (i%2==1){
                            System.out.println("B"+i);
                        }
                        semaphoreA.release();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        log.info("{}hello{}","test","word");
    }
}
