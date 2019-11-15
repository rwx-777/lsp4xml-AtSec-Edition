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

import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.TypeInfo;

/**
 * An attribute node.
 *
 */
public class DOMAttr extends DOMNode implements org.w3c.dom.Attr {

	private final String name;

	private final DOMNode nodeAttrName;

	private DOMNode nodeAttrValue;

	private String quotelessValue;// Value without quotes

	private String originalValue;// Exact value from document

	private final DOMNode ownerElement;

	private boolean hasDelimiter; // has '='

	public static final String XMLNS_ATTR = "xmlns";
	public static final String XMLNS_NO_DEFAULT_ATTR = "xmlns:";

	class AttrNameOrValue extends DOMNode {

		public AttrNameOrValue(int start, int end) {
			super(start, end);
		}

		@Override
		public String getNodeName() {
			return null;
		}

		@Override
		public short getNodeType() {
			return -1;
		}

		public DOMAttr getOwnerAttr() {
			return DOMAttr.this;
		}

		@Override
		public DOMDocument getOwnerDocument() {
			return getOwnerAttr().getOwnerDocument();
		}
	}

	public DOMAttr(String name, DOMNode ownerElement) {
		this(name, -1, -1, ownerElement);
	}

	public DOMAttr(String name, int start, int end, DOMNode ownerElement) {
		super(-1, -1);
		this.name = name;
		this.nodeAttrName = start != -1 ? new AttrNameOrValue(start, end) : null;
		this.ownerElement = ownerElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#getNodeType()
	 */
	@Override
	public short getNodeType() {
		return DOMNode.ATTRIBUTE_NODE;
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
	 * @see org.w3c.dom.Attr#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getNodeValue() throws DOMException {
		return getValue();
	}

	@Override
	public String getLocalName() {
		int colonIndex = name.indexOf(":");
		if (colonIndex > 0) {
			return name.substring(colonIndex + 1);
		}
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Attr#getOwnerElement()
	 */
	public DOMElement getOwnerElement() {
		return ownerElement.isElement() ? (DOMElement) ownerElement : null;
	}

	@Override
	public DOMDocument getOwnerDocument() {
		return ownerElement != null ? ownerElement.getOwnerDocument() : null;
	}

	/*
	 * 
	 * Returns the attribute's value without quotes.
	 */
	@Override
	public String getValue() {
		return quotelessValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Attr#getSchemaTypeInfo()
	 */
	@Override
	public TypeInfo getSchemaTypeInfo() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Attr#getSpecified()
	 */
	@Override
	public boolean getSpecified() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Attr#isId()
	 */
	@Override
	public boolean isId() {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Attr#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) throws DOMException {
		setValue(value, -1, -1);
	}

	public DOMNode getNodeAttrName() {
		return nodeAttrName;
	}

	public void setDelimiter(boolean hasDelimiter) {
		this.hasDelimiter = hasDelimiter;
	}

	public boolean hasDelimiter() {
		return this.hasDelimiter;
	}

	/**
	 * Get original attribute value from the document.
	 * 
	 * This will include quotations (", ').
	 * 
	 * @return attribute value with quotations if it had them.
	 */
	public String getOriginalValue() {
		return originalValue;
	}

	public void setValue(String value, int start, int end) {
		this.originalValue = value;
		this.quotelessValue = convertToQuotelessValue(value);
		this.nodeAttrValue = start != -1 ? new AttrNameOrValue(start, end) : null;
	}

	/**
	 * Returns a String of 'value' without surrounding quotes if it had them.
	 * 
	 * @param value
	 * @return
	 */
	public static String convertToQuotelessValue(String value) {
		if (value == null) {
			return null;
		}
		if (value.isEmpty()) {
			return value;
		}
		char quoteValue = value.charAt(0);
		int start = quoteValue == '\"' || quoteValue == '\'' ? 1 : 0;
		quoteValue = value.charAt(value.length() - 1);
		int end = quoteValue == '\"' || quoteValue == '\'' ? value.length() - 1 : value.length();
		return value.substring(start, end);
	}

	/**
	 * Checks if 'value' has matching surrounding quotations.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isQuoted(String value) {
		if (value == null) {
			return false;
		}
		if (value.isEmpty()) {
			return false;
		}
		char quoteValueStart = value.charAt(0);
		boolean start = quoteValueStart == '\"' || quoteValueStart == '\'' ? true : false;
		if (start == false) {
			return false;
		}
		char quoteValueEnd = value.charAt(value.length() - 1);
		boolean end = (quoteValueEnd == '\"' || quoteValueEnd == '\'') && quoteValueEnd == quoteValueStart ? true
				: false;
		return end;
	}

	public DOMNode getNodeAttrValue() {
		return nodeAttrValue;
	}

	public void setNodeAttrValue(DOMNode nodeAttrValue) {
		this.nodeAttrValue = nodeAttrValue;
	}

	public boolean valueContainsOffset(int offset) {
		return nodeAttrValue != null && offset >= nodeAttrValue.getStart() && offset < nodeAttrValue.getEnd();
	}

	/**
	 * Returns true if attribute name is a xmlns attribute and false otherwise.
	 * 
	 * @param attributeName
	 * @return true if attribute name is a xmlns attribute and false otherwise.
	 */
	public boolean isXmlns() {
		return isXmlns(name);
	}

	public static boolean isXmlns(String attributeName) {
		return attributeName.startsWith(XMLNS_ATTR);
	}

	/**
	 * Returns true if attribute name is the default xmlns attribute and false
	 * otherwise.
	 * 
	 * @param attributeName
	 * @return true if attribute name is the default xmlns attribute and false
	 *         otherwise.
	 */
	public boolean isDefaultXmlns() {
		return isDefaultXmlns(name);
	}

	public static boolean isDefaultXmlns(String attributeName) {
		return attributeName.equals(XMLNS_ATTR);
	}

	public String extractPrefixFromXmlns() {
		if (isDefaultXmlns()) {
			return name.substring(XMLNS_ATTR.length(), name.length());
		}
		return name.substring(XMLNS_NO_DEFAULT_ATTR.length(), name.length());
	}

	/**
	 * Returns the prefix if the given URI matches this attributes value.
	 * 
	 * If the URI doesnt match, null is returned.
	 * 
	 * @param uri
	 * @return
	 */
	public String getPrefixIfMatchesURI(String uri) {
		if (isXmlns()) {
			if (quotelessValue != null && quotelessValue.equals(uri)) {
				if (isDefaultXmlns()) {
					// xmlns="http://"
					return null;
				}
				// xmlns:xxx="http://"
				return extractPrefixFromXmlns();
			}
		}
		return null;
	}

	/**
	 * Returns true if attribute name is the no default xmlns attribute and false
	 * otherwise.
	 * 
	 * @param attributeName
	 * @return true if attribute name is the no default xmlns attribute and false
	 *         otherwise.
	 */
	public boolean isNoDefaultXmlns() {
		return isNoDefaultXmlns(name);
	}

	public static boolean isNoDefaultXmlns(String attributeName) {
		return attributeName.startsWith(XMLNS_NO_DEFAULT_ATTR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.Node#getNextSibling()
	 */
	@Override
	public DOMNode getNextSibling() {
		DOMNode parentNode = getOwnerElement();
		if (parentNode == null) {
			return null;
		}
		List<DOMAttr> children = parentNode.getAttributeNodes();
		int nextIndex = children.indexOf(this) + 1;
		return nextIndex < children.size() ? children.get(nextIndex) : null;
	}

	public boolean isIncluded(int offset) {
		return DOMNode.isIncluded(getStart(), getEnd(), offset);
	}

	@Override
	public int getStart() {
		return nodeAttrName.start;
	}

	@Override
	public int getEnd() {
		return nodeAttrValue != null ? nodeAttrValue.end : nodeAttrName.end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((quotelessValue == null) ? 0 : quotelessValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DOMAttr other = (DOMAttr) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (quotelessValue == null) {
			if (other.quotelessValue != null)
				return false;
		} else if (!quotelessValue.equals(other.quotelessValue))
			return false;
		return true;
	}

}
