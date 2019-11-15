/**
 *  Copyright (c) 2018 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.lsp4xml.extensions.xsl;

import static org.eclipse.lsp4xml.XMLAssert.c;
import static org.eclipse.lsp4xml.XMLAssert.te;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4xml.XMLAssert;
import org.eclipse.lsp4xml.commons.BadLocationException;
import org.junit.Test;

/**
 * XSL completion tests which test the {@link XSLURIResolverExtension}.
 *
 */
public class XSLCompletionExtensionsTest {

	@Test
	public void completion() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=\"1.0\"?>\r\n" + //
				"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n" + //
				"|";
		testCompletionFor(xml,
				c("xsl:template", te(2, 0, 2, 0, "<xsl:template></xsl:template>"), "xsl:template"), // <-- coming from substition group of xsl:declaration
				c("xsl:output", te(2, 0, 2, 0, "<xsl:output></xsl:output>"), "xsl:output"), // <-- coming from substition group of xsl:declaration
				c("xsl:import", te(2, 0, 2, 0, "<xsl:import href=\"\" />"), "xsl:import")); // coming from stylesheet children
	}

	private void testCompletionFor(String xml, CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(xml, null, expectedItems);
	}
}
