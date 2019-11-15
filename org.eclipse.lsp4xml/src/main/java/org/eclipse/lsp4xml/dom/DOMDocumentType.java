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
package org.eclipse.lsp4xml.dom;

import org.w3c.dom.NamedNodeMap;

/**
 * A doctype node.
 *
 */
public class DOMDocumentType extends DTDDeclNode implements org.w3c.dom.DocumentType {

	public enum DocumentTypeKind {
		PUBLIC, SYSTEM, INVALID
	}

	DTDDeclParameter kind; // SYSTEM || PUBLIC
	DTDDeclParameter publicId;
	DTDDeclParameter systemId;
	DTDDeclParameter internalSubset;

	private String content; // |<!DOCTYPE ... >|

	public DOMDocumentType(int start, int end) {
		super(start, end);
	}
	
	@Override
	public DOMDocumentType getOwnerDocType() {
		return this;
	}

	public String getContent() {
		if (content == null) {
			content = getOwnerDocument().getText().substring(getStart(), getEnd());
		}
		return content;
	}

	/**
	 * Returns the document type kind (PUBLIC or SYSTEM)
	 * 
	 * @return the document type kind (PUBLIC or SYSTEM)
	 */
	public String getKind() {
		return kind != null ? kind.getParameter() : null;
	}

	/**
	 * Returns the node where document type kind (PUBLIC or SYSTEM) is declared
	 * 
	 * @return the node where document type kind (PUBLIC or SYSTEM) is declared
	 */
	public DTDDeclParameter getKindNode() {
		return kind;
	}

	/**
	 * @param kind the DocumentTypeKind to set
	 */
	void setKind(int start, int end) {
		kind = addNewParameter(start, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#getNodeName()
	 */
	@Override
	public String getNodeName() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#getNodeType()
	 */
	@Override
	public short getNodeType() {
		return DOMNode.DOCUMENT_TYPE_NODE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.DocumentType#getEntities()
	 */
	@Override
	public NamedNodeMap getEntities() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.DocumentType#getInternalSubset()
	 */
	@Override
	public String getInternalSubset() {
		String subset;
		if (internalSubset != null) {
			subset = internalSubset.getParameter();
			subset = subset.substring(1, subset.length() - 1);
			internalSubset.parameter = subset; // Set parameter to a value without '[' and ']'
			return subset;

		}
		return null;
	}

	public void setStartInternalSubset(int start) {
		internalSubset = addNewParameter(start, start + 1);
	}

	public void setEndInternalSubset(int end) {
		updateLastParameterEnd(end);
	}

	public boolean isInternalSubset(DTDDeclParameter parameter) {
		if (this.internalSubset != null) {
			return this.internalSubset.equals(parameter);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.DocumentType#getNotations()
	 */
	@Override
	public NamedNodeMap getNotations() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.DocumentType#getPublicId()
	 */
	@Override
	public String getPublicId() {
		return publicId != null ? publicId.getParameter() : null;
	}

	public String getPublicIdWithoutQuotes() {
		return publicId != null ? publicId.getParameterWithoutFirstAndLastChar() : null;
	}

	/**
	 * @param publicId the publicId to set
	 */
	void setPublicId(int start, int end) {
		publicId = addNewParameter(start, end);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.DocumentType#getSystemId()
	 */
	@Override
	public String getSystemId() {
		return systemId != null ? systemId.getParameter() : null;
	}

	public DTDDeclParameter getSystemIdNode() {
		return systemId;
	}

	public String getSystemIdWithoutQuotes() {
		return systemId != null ? systemId.getParameterWithoutFirstAndLastChar() : null;
	}

	/**
	 * @param systemId the systemId to set
	 */
	void setSystemId(int start, int end) {
		systemId = addNewParameter(start, end);
	}

	/**
	 * Returns a substring of the whole document.
	 *
	 * 
	 * Since offset values are relative to 'this.start' we need to subtract
	 * getStart() to make them relative to 'content'
	 */
	public String getSubstring(int start, int end) {
		return getContent().substring(start - getStart(), end - getStart());
	}

}
