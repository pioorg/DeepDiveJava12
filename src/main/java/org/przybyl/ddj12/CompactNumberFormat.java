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

import java.text.*;
import java.util.*;


public class CompactNumberFormat {

	public static void main(String[] args) {
		var numbers = List.of(1_000, 1_000_000, 1_000_000_000);
		var locales = List.of(
			Locale.UK,
			new Locale("pl", "pl"),
			new Locale("uk", "ua"),
			new Locale("ru", "ru"),
			new Locale("fr", "be"),
			new Locale("nl", "be"),
			new Locale("de", "be"));

		locales.forEach(locale -> {
				var shortFormatter = NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.SHORT);
				var longFormatter = NumberFormat.getCompactNumberInstance(locale, NumberFormat.Style.LONG);
				numbers.forEach(number -> {
						String formatSample = String.format(
							"In %-14s speaking %-9s %10d is [%-6s] [%-11s]",
							locale.getDisplayCountry(),
							locale.getDisplayLanguage(),
							number,
							shortFormatter.format(number),
							longFormatter.format(number));
						System.out.println(formatSample);
					}
				);
			}
		);
	}

}
