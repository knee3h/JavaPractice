package com.mmall.practice.example.threadPool;

import com.google.common.collect.Lists;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * https://www.cnblogs.com/thisiswhy/p/17994272
 */
public class ThreadPoolExample5 {
    // 1、不要把核心线程数和最大线程数设置成一样
    // 2、尽量把阻塞队列设大一些，保证最大线程数+队列长度大于单批次提交任务数量
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(64, 64,
            0, TimeUnit.MINUTES, new ArrayBlockingQueue<>(32)){
        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static class AbortPolicyWithReport implements RejectedExecutionHandler{

        /**
         * Method that may be invoked by a {@link ThreadPoolExecutor} when
         * {@link ThreadPoolExecutor#execute execute} cannot accept a
         * task.  This may occur when no more threads or queue slots are
         * available because their bounds would be exceeded, or upon
         * shutdown of the Executor.
         *
         * <p>In the absence of other alternatives, the method may throw
         * an unchecked {@link RejectedExecutionException}, which will be
         * propagated to the caller of {@code execute}.
         *
         * @param r        the runnable task requested to be executed
         * @param executor the executor attempting to execute this task
         * @throws RejectedExecutionException if there is no remedy
         */
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try(FileOutputStream jstack = new FileOutputStream(new File("D://","jstack0.log"))) {
                JVMUtil.jstack(jstack);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new RuntimeException("触发拒绝策略");
        }
    }

    public static void main(String[] args) {
        threadPoolExecutor.setRejectedExecutionHandler(new AbortPolicyWithReport());
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            list.add(i);
        }
        for (int i = 0; i < 100; i++) {
            List<List<Integer>> sublist = Lists.partition(list, 400 / 32);
            int n = sublist.size();
            CountDownLatch countDownLatch = new CountDownLatch(n);
            for (int j = 0; j < n; j++) {
                threadPoolExecutor.execute(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("===============>  详情任务 - 任务处理完成");
        }
        System.out.println("都执行完成了");
    }
}
