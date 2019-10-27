/*
 * BSD 2-Clause License
 *
 * Copyright (c) 2019, Piotr Przybył
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.przybyl.ddj12;


import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class CompletionStageDemo {

	// This is demo only! Consider using Guava's ThreadFactoryBuilder.
	private static ExecutorService errorHandlingExecutorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("ErrorHandler"));

	public static void main(String[] args) throws InterruptedException {
		demoExceptionally();
//		demoExceptionallyAsync();
//		demoExceptionallyCompose();
//		demoExceptionallyComposeAsync();
	}

	public static void demoExceptionally() throws InterruptedException {
		CompletableFuture.supplyAsync(() -> {
			debug("going to blow");
			return 0 / (1 - 1);
		}).exceptionally(ex -> {
			debug("handling exception " + ex.getMessage());
			return 0;
		}).thenAccept(i -> {
			debug("accepting... " + i);
		});

		Thread.sleep(1000L);
	}

	public static void demoExceptionallyAsync() throws InterruptedException {
		CompletableFuture.supplyAsync(() -> {
			debug("going to blow");
			return 0 / (1 - 1);
		}).exceptionallyAsync(ex -> {
			debug("handling exception" + ex.getMessage());
			return 0;
		}, errorHandlingExecutorService).thenAccept(i -> {
			debug("accepting... " + i);
		});

		Thread.sleep(1000L);
	}

	public static void demoExceptionallyCompose() throws InterruptedException {
		var planA = CompletableFuture.supplyAsync(() -> {
			debug("going to blow");
			int[] ints = {1, 2, 3};
			return ints[ints[2]];
		});

		var finalPlan = planA.exceptionallyComposeAsync(ex -> {
			debug("Something went wrong... " + ex.getMessage() + ", time for plan B!");
			return getPlanB();
		}, errorHandlingExecutorService);

		finalPlan.thenApplyAsync(i -> {
			debug("let's double!");
			return i * 2;
		}, ForkJoinPool.commonPool())
			.thenAcceptAsync(i -> debug("Result of our plan is: " + i), ForkJoinPool.commonPool());

		Thread.sleep(1000L);
	}

	private static CompletableFuture<Integer> getPlanB() {
		return CompletableFuture.supplyAsync(() -> {
			debug("doing something sane");
			return -1;
		}, errorHandlingExecutorService);
	}

	public static void demoExceptionallyComposeAsync() throws InterruptedException {
		var planA = CompletableFuture.supplyAsync(() -> {
			debug("going to blow");
			int[] ints = {1, 2, 3};
			return ints[ints[2]];
		});

		var planB = CompletableFuture.supplyAsync(() -> {
			debug("doing something sane");
			return -1;
		});

		var finalPlan = planA.exceptionallyComposeAsync(ex -> {
			debug("Something went wrong... " + ex.getMessage() + ", time for plan B!");
			return planB;
		});

		finalPlan.thenApply(i -> {
			debug("let's double!");
			return i * 2;
		}).thenAccept(i -> debug("Result of our plan is: " + i));

		Thread.sleep(1000L);
	}


	private static void debug(String message) {
		System.out.println(String.format("[%d] tid[%d] tname [%s] message: [%s], ", System.currentTimeMillis(), Thread.currentThread().getId(), Thread.currentThread().getName(), message));
	}


}

class NamedThreadFactory implements ThreadFactory {
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	NamedThreadFactory(String prefix) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() :
			Thread.currentThread().getThreadGroup();
		namePrefix = "pool-" +
			poolNumber.getAndIncrement() +
			"-" + prefix + "-";
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r,
			namePrefix + threadNumber.getAndIncrement(),
			0);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.MIN_PRIORITY)
			t.setPriority(Thread.MIN_PRIORITY);
		return t;
	}
}
