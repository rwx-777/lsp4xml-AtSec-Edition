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
package org.eclipse.lsp4xml.services;

import org.eclipse.lsp4xml.dom.DOMDocument;

/**
 * {@link DOMDocument} provider.
 * 
 * @author Angelo ZERR
 *
 */
public interface IXMLDocumentProvider {

	/**
	 * Returns the {@link DOMDocument} instance from the given <code>uri</code> and
	 * null otherwise.
	 * 
	 * @param uri the document URI.
	 * @return the {@link DOMDocument} instance from the given <code>uri</code> and
	 *         null otherwise.
	 */
	DOMDocument getDocument(String uri);
}
