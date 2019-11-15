/**
 *  Copyright (c) 2018 Angelo ZERR
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lsp4xml.extensions.emmet.emmetio.extract;

import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Translate JS
 * https://github.com/emmetio/extract-abbreviation/blob/master/index.js to Java
 *
 */
public class ExtractAbbreviation {

	private static ScriptEngine engine;

	static {
		// load extract-abbreviation-full.js
		try {
			engine = new ScriptEngineManager().getEngineByName("nashorn");
			// https://cdnjs.cloudflare.com/ajax/libs/babel-polyfill/6.26.0/polyfill.min.js
			engine.eval(new InputStreamReader(ExtractAbbreviation.class.getResourceAsStream("polyfill.min.js")));
			// generated from extract-abbreviation github project with "no modules"
			engine.eval(new InputStreamReader(
					ExtractAbbreviation.class.getResourceAsStream("extract-abbreviation-full.js")));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extracts Emmet abbreviation from given string. The goal of this module is to
	 * extract abbreviation from current editor’s line, e.g. like this:
	 * `<span>.foo[title=bar|]</span>` -> `.foo[title=bar]`, where `|` is a current
	 * caret position.
	 * 
	 * @param {String} line A text line where abbreviation should be expanded
	 * @param {Number} [pos] Caret position in line. If not given, uses end-of-line
	 * @param {Object} [options]
	 * @param {Boolean} [options.lookAhead] Allow parser to look ahead of `pos`
	 *        index for searching of missing abbreviation parts. Most editors
	 *        automatically inserts closing braces for `[`, `{` and `(`, which will
	 *        most likely be right after current caret position. So in order to
	 *        properly expand abbreviation, user must explicitly move caret right
	 *        after auto-inserted braces. With this option enabled, parser will
	 *        search for closing braces right after `pos`. Default is `true`
	 * @param {String} [options.syntax] Name of context syntax of expanded
	 *        abbreviation. Either 'markup' (default) or 'stylesheet'. In
	 *        'stylesheet' syntax, braces `[]` and `{}` are not supported thus not
	 *        extracted.
	 * @param {String} [options.prefix] A string that should precede abbreviation in
	 *        order to make it successfully extracted. If given, the abbreviation
	 *        will be extracted from the nearest `prefix` occurrence.
	 * @return {Object} Object with `abbreviation` and its `location` in given line
	 *         if abbreviation can be extracted, `null` otherwise
	 */
	public static ExtractAbbreviationResult extractAbbreviation(String line, Integer pos, EmmetOptions options) {
		try {
			Invocable inv = (Invocable) engine;
			Bindings s = (Bindings) inv.invokeFunction("emmet", line, pos);
			if (s == null) {
				return null;
			}
			String abbreviation = (String) s.get("abbreviation");
			Number location = (Number) s.get("location");
			Number start = (Number) s.get("start");
			Number end = (Number) s.get("end");
			return new ExtractAbbreviationResult(abbreviation, location != null ? location.intValue() : 0,
					start != null ? start.intValue() : 0, end != null ? end.intValue() : 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
