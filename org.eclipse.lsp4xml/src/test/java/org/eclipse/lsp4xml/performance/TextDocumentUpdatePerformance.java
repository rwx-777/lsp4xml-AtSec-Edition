/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4xml.performance;

import static org.eclipse.lsp4xml.utils.IOUtils.convertStreamToString;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4xml.commons.TextDocument;

/**
 * This utility class is used to check the performance of
 * {@link TextDocument#update(List)}, updating the large nasa.xml file
 * 
 * @author Angelo ZERR
 *
 */
public class TextDocumentUpdatePerformance {

	public static void main(String[] args) {
		InputStream in = TextDocumentUpdatePerformance.class.getResourceAsStream("/xml/nasa.xml");
		String text = convertStreamToString(in);
		TextDocument document = new TextDocument(text, "nasa.xml");
		document.setIncremental(true);
		// Continuously parses the large nasa.xml file with the DOM parser.
		while (true) {
			long start = System.currentTimeMillis();
			// Insert a space
			List<TextDocumentContentChangeEvent> changes = new ArrayList<>();
			TextDocumentContentChangeEvent change = new TextDocumentContentChangeEvent(
					new Range(new Position(14, 13), new Position(14, 13)), 0, " ");
			changes.add(change);
			document.update(changes);
			System.err.println("Update 'nasa.xml' text document in " + (System.currentTimeMillis() - start) + " ms.");
		}

	}

}
