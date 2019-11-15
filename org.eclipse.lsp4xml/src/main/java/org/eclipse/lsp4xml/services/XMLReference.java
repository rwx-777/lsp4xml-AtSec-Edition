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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.ReferenceContext;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.services.extensions.IReferenceParticipant;
import org.eclipse.lsp4xml.services.extensions.XMLExtensionsRegistry;

/**
 * XML reference support.
 *
 */
class XMLReference {

	private final XMLExtensionsRegistry extensionsRegistry;

	public XMLReference(XMLExtensionsRegistry extensionsRegistry) {
		this.extensionsRegistry = extensionsRegistry;
	}

	public List<? extends Location> findReferences(DOMDocument document, Position position, ReferenceContext context,
			CancelChecker cancelChecker) {
		List<Location> locations = new ArrayList<>();
		for (IReferenceParticipant participant : extensionsRegistry.getReferenceParticipants()) {
			participant.findReference(document, position, context, locations, cancelChecker);
		}
		return locations;
	}

}
