package com.mmall.practice.example.threadPool;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.IntStream;

/**
 * @Description 提交有返回值的任务
 */

public class ForkJoinPoolTest {

    /**
     * 最大计算数
     */
    private static final int MAX_THRESHOLD = 100;

    public static void main(String[] args) {
        //创建ForkJoinPool
        ForkJoinPool pool = new ForkJoinPool();
        //异步提交RecursiveTask任务
        ForkJoinTask<Integer> forkJoinTask = pool.submit(new CalculatedRecursiveTask(0, 1000));
        try {
            //根据返回类型获取返回值
            Integer result = forkJoinTask.get();
            System.out.println("执行结果为：" + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    private static class CalculatedRecursiveTask extends RecursiveTask<Integer> {
        private final int start;
        private final int end;

        public CalculatedRecursiveTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Integer compute() {
            //判断计算范围，如果小于等于5，那么一个线程计算即可，否则进行分割
            if ((end - start) <= MAX_THRESHOLD) {
                //返回[start,end]的总和
                return IntStream.rangeClosed(start, end).sum();
            } else {
                //任务分割
                int middle = (end + start) / 2;
                CalculatedRecursiveTask task1 = new CalculatedRecursiveTask(start, middle);
                CalculatedRecursiveTask task2 = new CalculatedRecursiveTask(middle + 1, end);
                //执行
                task1.fork();
                task2.fork();
                //等待返回结果
                return task1.join() + task2.join();
            }
        }
    }
}