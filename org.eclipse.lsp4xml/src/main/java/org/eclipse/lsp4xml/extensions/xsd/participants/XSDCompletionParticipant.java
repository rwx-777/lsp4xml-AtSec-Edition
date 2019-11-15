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
package org.eclipse.lsp4xml.extensions.xsd.participants;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4xml.dom.DOMAttr;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.dom.DOMNode;
import org.eclipse.lsp4xml.extensions.xsd.DataType;
import org.eclipse.lsp4xml.extensions.xsd.utils.XSDUtils;
import org.eclipse.lsp4xml.extensions.xsd.utils.XSDUtils.BindingType;
import org.eclipse.lsp4xml.services.extensions.CompletionParticipantAdapter;
import org.eclipse.lsp4xml.services.extensions.ICompletionRequest;
import org.eclipse.lsp4xml.services.extensions.ICompletionResponse;
import org.eclipse.lsp4xml.utils.DOMUtils;

/**
 * XSD completion for
 * 
 * <ul>
 * <li>xs:/@type -> xs:complexType/@name</li>
 * <li>xs:/@base -> xs:complexType/@name</li>
 * </ul>
 *
 */
public class XSDCompletionParticipant extends CompletionParticipantAdapter {

	@Override
	public void onAttributeValue(String valuePrefix, ICompletionRequest request, ICompletionResponse response)
			throws Exception {
		DOMNode node = request.getNode();
		DOMDocument document = node.getOwnerDocument();
		if (!DOMUtils.isXSD(document)) {
			return;
		}
		Range fullRange = request.getReplaceRange();
		DOMAttr originAttr = node.findAttrAt(request.getOffset());
		BindingType bindingType = XSDUtils.getBindingType(originAttr);
		if (bindingType != BindingType.NONE) {
			// Completion on
			// - @type (ex : xs:element/@type)
			// - @base (ex : xs:extension/@base)
			// bound to complextTypes/@name
			XSDUtils.searchXSTargetAttributes(originAttr, bindingType, false, true,
					(targetNamespacePrefix, targetAttr) -> {
						CompletionItem item = new CompletionItem();
						item.setDocumentation(
								new MarkupContent(MarkupKind.MARKDOWN, DataType.getDocumentation(targetAttr)));
						String value = createComplexTypeValue(targetAttr, targetNamespacePrefix);
						String insertText = request.getInsertAttrValue(value);
						item.setLabel(value);
						item.setKind(CompletionItemKind.Value);
						item.setFilterText(insertText);
						item.setTextEdit(new TextEdit(fullRange, insertText));
						response.addCompletionItem(item);
					});
			if (bindingType.isSimple()) {
				// Completion on @type (ex : xs:element/@type) bound to Built-in types (ex:
				// xs:string) ->
				// https://www.w3.org/TR/xmlschema11-2/#built-in-datatypes
				String prefix = document.getSchemaPrefix();
				DataType.getDataTypes().forEach(dataType -> {
					CompletionItem item = new CompletionItem();
					item.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, dataType.getDocumentation()));
					String value = createDatatypeValue(dataType, prefix);
					String insertText = request.getInsertAttrValue(value);
					item.setLabel(value);
					item.setKind(CompletionItemKind.Value);
					item.setFilterText(insertText);
					item.setTextEdit(new TextEdit(fullRange, insertText));
					response.addCompletionItem(item);
				});
			}
		}
	}

	private static String createComplexTypeValue(DOMAttr targetAttr, String targetNamespacePrefix) {
		StringBuilder value = new StringBuilder();
		if (targetNamespacePrefix != null) {
			value.append(targetNamespacePrefix);
			value.append(":");
		}
		value.append(targetAttr.getValue());
		return value.toString();
	}

	private static String createDatatypeValue(DataType dataType, String prefix) {
		StringBuilder value = new StringBuilder();
		if (prefix != null) {
			value.append(prefix);
			value.append(":");
		}
		value.append(dataType.getName());
		return value.toString();
	}

}
