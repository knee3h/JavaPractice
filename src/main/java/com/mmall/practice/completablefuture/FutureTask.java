package com.mmall.practice.completablefuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class FutureTask {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();
        // complete() 方法只能调用一次，后续调用将被忽略。
        future.complete("end..");
        future.isDone();//是否完成

        CompletableFuture<String> future0= CompletableFuture.completedFuture("future0hello!");
        System.out.println(future0.get());

        /**
         * static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);
         * // 使用自定义线程池(推荐)
         * static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor);
         * static CompletableFuture<Void> runAsync(Runnable runnable);
         * // 使用自定义线程池(推荐)
         * static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor);
         */

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> System.out.println("future1hello!"));
        System.out.println(future1.get());
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "future2hello!");
        System.out.println(future2.get());

        /**
         * // 沿用上一个任务的线程池
         * public <U> CompletableFuture<U> thenApply(
         *     Function<? super T,? extends U> fn) {
         *     return uniApplyStage(null, fn);
         * }
         *
         * //使用默认的 ForkJoinPool 线程池（不推荐）
         * public <U> CompletableFuture<U> thenApplyAsync(
         *     Function<? super T,? extends U> fn) {
         *     return uniApplyStage(defaultExecutor(), fn);
         * }
         * // 使用自定义线程池(推荐)
         * public <U> CompletableFuture<U> thenApplyAsync(
         *     Function<? super T,? extends U> fn, Executor executor) {
         *     return uniApplyStage(screenExecutor(executor), fn);
         * }
          */
        //流式调用：
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("future3hello!")
                .thenApply(s -> s + "world!").thenApply(s -> s + "nice!");
        System.out.println(future3.get());

        CompletableFuture<String> future4 = CompletableFuture.supplyAsync(() -> "future4hello!")
                .whenComplete((res, ex) -> {
                    // res 代表返回的结果
                    // ex 的类型为 Throwable ，代表抛出的异常
                    System.out.println(res);
                    // 这里抛出异常
//                    assertNotNull(ex);
                });
        assertEquals("future4hello!", future4.get());

        // 处理异常结果
        CompletableFuture<String> future5
                = CompletableFuture.supplyAsync(() -> {
            if (true) {
                throw new RuntimeException("Computation error!");
            }
            return "future5hello!";
        }).handle((res, ex) -> {
            // res 代表返回的结果
            // ex 的类型为 Throwable ，代表抛出的异常
            return res != null ? res : "future5world!";
        });
        System.out.println(future5.get());

        CompletableFuture<String> future6
                = CompletableFuture.supplyAsync(() -> {
            if (true) {
                throw new RuntimeException("Computation error!");
            }
            return "future6hello!";
        }).exceptionally(ex -> {
            System.out.println(ex.toString());// CompletionException
            return "future6world!";
        });
        System.out.println(future6.get());

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.completeExceptionally(
                new RuntimeException("Calculation failed!"));
        completableFuture.get(); // ExecutionException
        /**
         * 使用 whenComplete 方法可以在任务完成时触发回调函数，并正确地处理异常，而不是让异常被吞噬或丢失。
         * 使用 exceptionally 方法可以处理异常并重新抛出，以便异常能够传播到后续阶段，而不是让异常被忽略或终止。
         * 使用 handle 方法可以处理正常的返回结果和异常，并返回一个新的结果，而不是让异常影响正常的业务逻辑。
         * 使用 CompletableFuture.allOf 方法可以组合多个 CompletableFuture，并统一处理所有任务的异常，而不是让异常处理过于冗长或重复。
         */

//------------------------------------------------------------------------
        //组合 CompletableFuture

        CompletableFuture<String> future7
                = CompletableFuture.supplyAsync(() -> "future7hello!")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + "world!"));
        System.out.println(future7.get());

        CompletableFuture<String> future8
                = CompletableFuture.supplyAsync(() -> "future8hello!")
                .thenCombine(CompletableFuture.supplyAsync(
                        () -> "world!"), (s1, s2) -> s1 + s2)
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + "nice!"));
        System.out.println(future8.get());

        /**
         * 正确使用 thenCompose() 、 thenCombine() 、acceptEither()、allOf()、anyOf()
         * 等方法来组合多个异步任务，以满足实际业务的需求，提高程序执行效率。
         *
         * 那 thenCompose() 和 thenCombine() 有什么区别呢？
         *
         * thenCompose() 可以链接两个 CompletableFuture 对象，并将前一个任务的返回结果作为下一个任务的参数，它们之间存在着先后顺序。
         * thenCombine() 会在两个任务都执行完成后，把两个任务的结果合并。两个任务是并行执行的，它们之间并没有先后依赖顺序。
         */
    }
}
