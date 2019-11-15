/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4xml.extensions.dtd.participants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4xml.client.CodeLensKind;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.dom.DTDDeclNode;
import org.eclipse.lsp4xml.extensions.dtd.utils.DTDUtils;
import org.eclipse.lsp4xml.services.extensions.codelens.ICodeLensParticipant;
import org.eclipse.lsp4xml.services.extensions.codelens.ICodeLensRequest;
import org.eclipse.lsp4xml.services.extensions.codelens.ReferenceCommand;
import org.eclipse.lsp4xml.utils.DOMUtils;
import org.eclipse.lsp4xml.utils.XMLPositionUtility;

/**
 * DTD CodeLens to show references count for referenced <!ELEMENT
 * 
 * @author Angelo ZERR
 *
 */
public class DTDCodeLensParticipant implements ICodeLensParticipant {

	@Override
	public void doCodeLens(ICodeLensRequest request, List<CodeLens> lenses, CancelChecker cancelChecker) {
		DOMDocument xmlDocument = request.getDocument();
		// DTD CodeLens is applicable only for DTD or XML which defines a DOCTYPE
		if (!(DOMUtils.isDTD(xmlDocument.getDocumentURI()) || xmlDocument.hasDTD())) {
			return;
		}
		boolean supportedByClient = request.isSupportedByClient(CodeLensKind.References);
		// Add references CodeLens for <!ELEMENT
		Map<DTDDeclNode, CodeLens> cache = new HashMap<>();
		DTDUtils.searchDTDOriginElementDecls(xmlDocument.getDoctype(), (origin, target) -> {
			// Increment references count Codelens for the given target element <!ELEMENT
			DTDDeclNode targetElement = target.getOwnerNode();
			if (targetElement.isDTDElementDecl()) {
				CodeLens codeLens = cache.get(targetElement);
				if (codeLens == null) {
					Range range = XMLPositionUtility.createRange(target);
					codeLens = new CodeLens(range);
					codeLens.setCommand(
							new ReferenceCommand(xmlDocument.getDocumentURI(), range.getStart(), supportedByClient));
					cache.put(targetElement, codeLens);
					lenses.add(codeLens);
				} else {
					((ReferenceCommand) codeLens.getCommand()).increment();
				}
			}
		}, cancelChecker);
	}

}
