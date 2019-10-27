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


import java.util.*;
import java.util.stream.*;

public class TeeingCollector {

	public static void main(String[] args) {

// getting sum and count as operation on primitives
		long sum = gimmeStream().mapToLong(l -> l).sum();
		long count = gimmeStream().count();

// reducing
		CountAndSum reduced = gimmeStream().reduce(
			new CountAndSum(0, 0),
			(cas, e) -> new CountAndSum(cas.count + 1, cas.sum + e),
			(cas1, cas2) -> new CountAndSum(cas1.count + cas2.count, cas1.sum + cas2.sum));

// teeing
		CountAndSum collected = gimmeStream().collect(
			Collectors.teeing(
				Collectors.counting(),
				Collectors.summingLong(l -> l),
				CountAndSum::new));

		System.out.println("Sum: " + sum);
		System.out.println("Count: " + count);
		System.out.println("Sum & count: " + reduced);
		System.out.println("Sum & count: " + collected);

// don't re-invent the wheel when not needed ;-)
		LongSummaryStatistics stats = gimmeStream().collect(Collectors.summarizingLong(e -> e));
		System.out.println(stats);

// using array in such case is not the best option; consider creating a tuplish class
		long[] by2AndBy3 = gimmeStream().collect(Collectors.teeing(
			Collectors.filtering(l -> l % 2 == 0, Collectors.counting()),
			Collectors.filtering(l -> l % 3 == 0, Collectors.counting()),
			(by2, by3) -> new long[]{by2, by3}
		));
		System.out.println(String.format("There are %d numbers divisible by 2 and %d numbers divisible by 3.", by2AndBy3[0], by2AndBy3[1]));


	}

	private static Stream<Long> gimmeStream() {
//		System.out.println("Creating stream...");
		return Stream.iterate(0L, i -> i < 20, i -> i + 1);
	}

	static class CountAndSum {
		public final long count;
		public final long sum;

		public CountAndSum(long count, long sum) {
			this.count = count;
			this.sum = sum;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", CountAndSum.class.getSimpleName() + "[", "]")
				.add("count=" + count)
				.add("sum=" + sum)
				.toString();
		}
	}
}
