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
package org.eclipse.lsp4xml.extensions.xsd.contentmodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4xml.dom.DOMAttr;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.dom.DOMElement;
import org.eclipse.lsp4xml.dom.DOMNode;
import org.eclipse.lsp4xml.extensions.contentmodel.model.CMDocument;
import org.eclipse.lsp4xml.extensions.contentmodel.model.CMElementDeclaration;
import org.eclipse.lsp4xml.extensions.contentmodel.model.FilesChangedTracker;
import org.eclipse.lsp4xml.extensions.xsd.utils.XSDUtils;
import org.eclipse.lsp4xml.utils.DOMUtils;
import org.eclipse.lsp4xml.utils.StringUtils;
import org.eclipse.lsp4xml.utils.URIUtils;
import org.eclipse.lsp4xml.utils.XMLPositionUtility;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XSD document implementation.
 *
 */
public class CMXSDDocument implements CMDocument, XSElementDeclHelper {

	private static final Logger LOGGER = Logger.getLogger(CMXSDDocument.class.getName());

	private final XSModel model;

	private final String uri;

	private final Map<XSElementDeclaration, CMXSDElementDeclaration> elementMappings;

	private Collection<CMElementDeclaration> elements;

	private final FilesChangedTracker tracker;

	public CMXSDDocument(XSModel model, String uri) {
		this.model = model;
		this.elementMappings = new HashMap<>();
		this.uri = uri;
		this.tracker = createFilesChangedTracker(model);
	}

	/**
	 * Create files tracker to track all XML Schema (root and imported) from the XS
	 * model.
	 * 
	 * @return a files tracker to track all XML Schema (root and imported) from the
	 *         XS model.
	 */
	private static FilesChangedTracker createFilesChangedTracker(XSModel model) {
		Set<SchemaGrammar> grammars = new HashSet<>();
		XSNamespaceItemList namespaces = model.getNamespaceItems();
		for (int i = 0; i < namespaces.getLength(); i++) {
			SchemaGrammar grammar = getSchemaGrammar(namespaces.item(i));
			if (grammar != null) {
				grammars.add(grammar);
			}
		}
		return XSDUtils.createFilesChangedTracker(grammars);
	}

	@Override
	public boolean hasNamespace(String namespaceURI) {
		if (namespaceURI == null || model.getNamespaces() == null) {
			return false;
		}
		return model.getNamespaces().contains(namespaceURI);
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public Collection<CMElementDeclaration> getElements() {
		if (elements == null) {
			elements = new ArrayList<>();
			XSNamedMap map = model.getComponents(XSConstants.ELEMENT_DECLARATION);
			for (int j = 0; j < map.getLength(); j++) {
				XSElementDeclaration elementDeclaration = (XSElementDeclaration) map.item(j);
				collectElement(elementDeclaration, elements);
			}
		}
		return elements;
	}

	/**
	 * Fill the given elements list from the given Xerces elementDeclaration
	 * 
	 * @param elementDeclaration
	 * @param elements
	 */
	void collectElement(XSElementDeclaration elementDeclaration, Collection<CMElementDeclaration> elements) {
		if (elementDeclaration.getAbstract()) {
			// element declaration is marked as abstract
			// ex with xsl: <xs:element name="declaration" type="xsl:generic-element-type"
			// abstract="true"/>
			XSObjectList list = getSubstitutionGroup(elementDeclaration);
			if (list != null) {
				// it exists elements list bind with this abstract declaration with
				// substitutionGroup
				// ex xsl : <xs:element name="template" substitutionGroup="xsl:declaration">
				for (int i = 0; i < list.getLength(); i++) {
					XSObject object = list.item(i);
					if (object.getType() == XSConstants.ELEMENT_DECLARATION) {
						XSElementDeclaration subElementDeclaration = (XSElementDeclaration) object;
						collectElement(subElementDeclaration, elements);
					}
				}
			}
		} else {
			CMElementDeclaration cmElement = getXSDElement(elementDeclaration);
			// check element declaration is not already added (ex: xs:annotation)
			if (!elements.contains(cmElement)) {
				elements.add(cmElement);
			}
		}
	}

	XSObjectList getSubstitutionGroup(XSElementDeclaration elementDeclaration) {
		return model.getSubstitutionGroup(elementDeclaration);
	}

	@Override
	public CMElementDeclaration findCMElement(DOMElement element, String namespace) {
		List<DOMElement> paths = new ArrayList<>();
		while (element != null && (namespace == null || namespace.equals(element.getNamespaceURI()))) {
			paths.add(0, element);
			element = element.getParentNode() instanceof DOMElement ? (DOMElement) element.getParentNode() : null;
		}
		CMElementDeclaration declaration = null;
		for (int i = 0; i < paths.size(); i++) {
			DOMElement elt = paths.get(i);
			if (i == 0) {
				declaration = findElementDeclaration(elt.getLocalName(), namespace);
			} else {
				declaration = declaration.findCMElement(elt.getLocalName(), namespace);
			}
			if (declaration == null) {
				break;
			}
		}
		return declaration;
	}

	private CMElementDeclaration findElementDeclaration(String tag, String namespace) {
		for (CMElementDeclaration cmElement : getElements()) {
			if (cmElement.getName().equals(tag)) {
				return cmElement;
			}
		}
		return null;
	}

	CMElementDeclaration getXSDElement(XSElementDeclaration elementDeclaration) {
		CMXSDElementDeclaration element = elementMappings.get(elementDeclaration);
		if (element == null) {
			element = new CMXSDElementDeclaration(this, elementDeclaration);
			elementMappings.put(elementDeclaration, element);
		}
		return element;
	}

	static Collection<String> getEnumerationValues(XSSimpleTypeDefinition typeDefinition) {
		if (typeDefinition != null) {
			if (isBooleanType(typeDefinition)) {
				return StringUtils.TRUE_FALSE_ARRAY;
			}
			StringList enumerations = typeDefinition.getLexicalEnumeration();
			if (enumerations != null) {
				return enumerations;
			}
		}
		return Collections.emptyList();
	}

	static boolean isBooleanType(XSSimpleTypeDefinition typeDefinition) {
		if (typeDefinition instanceof XSSimpleType) {
			return ((XSSimpleType) typeDefinition).getPrimitiveKind() == XSSimpleType.PRIMITIVE_BOOLEAN;
		}
		return false;
	}

	@Override
	public XSElementDecl getGlobalElementDecl(QName element) {
		return (XSElementDecl) model.getElementDeclaration(element.localpart, element.uri);
	}

	@Override
	public LocationLink findTypeLocation(DOMNode originNode) {
		DOMElement originElement = null;
		DOMAttr originAttribute = null;
		if (originNode.isElement()) {
			originElement = (DOMElement) originNode;
		} else if (originNode.isAttribute()) {
			originAttribute = (DOMAttr) originNode;
			originElement = originAttribute.getOwnerElement();
		}
		if (originElement == null) {
			return null;
		}
		// Try to retrieve XSD element declaration from the given element.
		CMXSDElementDeclaration elementDeclaration = (CMXSDElementDeclaration) findCMElement(originElement,
				originElement.getNamespaceURI());
		if (elementDeclaration == null) {
			return null;
		}
		SchemaGrammar schemaGrammar = getOwnerSchemaGrammar(elementDeclaration.getElementDeclaration());
		if (schemaGrammar == null) {
			return null;
		}

		String documentURI = getSchemaURI(schemaGrammar);
		if (URIUtils.isFileResource(documentURI)) {
			// Only XML Schema file is supported. In the case of XML file is bound with an
			// HTTP url and cache is enable, documentURI is a file uri from the cache
			// folder.

			// Xerces doesn't give the capability to know the location of xs:element,
			// xs:attribute.
			// To retrieve the proper location of xs:element, xs:attribute, we load the XML
			// Schema in the DOM Document of LSP4XML which stores location.
			DOMDocument targetSchema = DOMUtils.loadDocument(documentURI,
					originNode.getOwnerDocument().getResolverExtensionManager());
			if (targetSchema == null) {
				return null;
			}
			if (originAttribute != null) {
				// find location of xs:attribute declaration
				String attributeName = originAttribute.getName();
				CMXSDAttributeDeclaration attributeDeclaration = (CMXSDAttributeDeclaration) elementDeclaration
						.findCMAttribute(attributeName);
				if (attributeDeclaration != null) {
					XSAttributeDeclaration attributeDecl = attributeDeclaration.getAttrDeclaration();
					if (attributeDecl.getScope() == XSConstants.SCOPE_LOCAL) {
						return findLocalXSAttribute(originAttribute, targetSchema,
								attributeDecl.getEnclosingCTDefinition(), schemaGrammar);

					}
				}
			} else {
				// find location of xs:element declaration
				boolean globalElement = elementDeclaration.getElementDeclaration()
						.getScope() == XSElementDecl.SCOPE_GLOBAL;
				if (globalElement) {
					return findGlobalXSElement(originElement, targetSchema);
				} else {
					return findLocalXSElement(originElement, targetSchema,
							elementDeclaration.getElementDeclaration().getEnclosingCTDefinition(), schemaGrammar);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the schema URI of the given schema grammar.
	 * 
	 * @param schemaGrammar the Xerces schema grammar.
	 * @return the schema URI of the given schema grammar.
	 */
	static String getSchemaURI(SchemaGrammar schemaGrammar) {
		if (schemaGrammar == null) {
			return null;
		}
		return schemaGrammar.getDocumentLocations().item(0);
	}

	/**
	 * Returns the owner schema grammar of the given XSD element declaration and
	 * null otherwise.
	 * 
	 * @param elementDeclaration the XSD element declaration
	 * @return the owner schema grammar of the given XSD element declaration and
	 *         null otherwise.
	 */
	SchemaGrammar getOwnerSchemaGrammar(XSElementDeclaration elementDeclaration) {
		XSTypeDefinition enclosingType = elementDeclaration.getEnclosingCTDefinition();
		if (enclosingType == null && elementDeclaration.getScope() == XSConstants.SCOPE_ABSENT) {
			enclosingType = elementDeclaration.getTypeDefinition();
		}
		// 1) when XSD element declaration has namespace, it is bound with the grammar.
		XSNamespaceItem namespaceItem = enclosingType != null ? enclosingType.getNamespaceItem()
				: elementDeclaration.getNamespaceItem();
		SchemaGrammar grammar = getSchemaGrammar(namespaceItem);
		if (grammar != null) {
			return grammar;
		}
		// 2) XSD element declaration has none namespace. Here we loop for each grammar
		// to discover if the element type belong to this grammar.
		XSNamespaceItemList namespaces = model.getNamespaceItems();

		// 2.1) XSD global element, uses the SchemaGrammar#getElementDeclaration method
		if (elementDeclaration.getScope() == XSConstants.SCOPE_GLOBAL) {
			for (int i = 0; i < namespaces.getLength(); i++) {
				XSNamespaceItem namespace = namespaces.item(i);
				if (namespace instanceof SchemaGrammar && (elementDeclaration
						.equals(((SchemaGrammar) namespace).getElementDeclaration(elementDeclaration.getName())))) {
					return (SchemaGrammar) namespace;
				}
			}
		}

		// 2.2) XSD local element, get the parent xs:complexType of the XSD element and
		// loop for each xs:complexType of the SchemaGrammar
		if (enclosingType == null) {
			return null;
		}
		for (int i = 0; i < namespaces.getLength(); i++) {
			XSNamespaceItem namespace = namespaces.item(i);
			if (namespace instanceof SchemaGrammar) {
				XSComplexTypeDecl[] complexTypes = getXSComplexTypeDecls((SchemaGrammar) namespace);
				if (complexTypes != null) {
					for (int j = 0; j < complexTypes.length; j++) {
						if (enclosingType.equals(complexTypes[j])) {
							return (SchemaGrammar) namespace;
						}
					}
				}
			}
		}
		return null;

	}

	/**
	 * Returns the schema grammar from the given namespace and null otherwise.
	 * 
	 * @param namespaceItem the namespace
	 * @return the schema grammar from the given namespace and null otherwise.
	 */
	private static SchemaGrammar getSchemaGrammar(XSNamespaceItem namespaceItem) {
		return (namespaceItem != null && namespaceItem instanceof SchemaGrammar) ? (SchemaGrammar) namespaceItem : null;
	}

	/**
	 * Returns the location of the global xs:element declared in the given XML
	 * Schema <code>targetSchema</code> which matches the given XML element
	 * <code>originElement</code> and null otherwise.
	 * 
	 * @param originElement the XML element
	 * @param targetSchema  the XML Schema
	 * @return the location of the global xs:element declared in the given XML
	 *         Schema <code>targetSchema</code> which matches the given XML element
	 *         <code>originElement</code> and null otherwise.
	 */
	private static LocationLink findGlobalXSElement(DOMElement originElement, DOMDocument targetSchema) {
		// In global xs:element case, the xs:element are declared after the document
		// element xs:schema.
		// Here we just loop of children of xs:schema and return the location of
		// xs:element/@name which matches the tag name of the origin XML element
		NodeList children = targetSchema.getDocumentElement().getChildNodes();
		return findXSElement(originElement, children, false);
	}

	/**
	 * Returns the location of the local xs:element declared in the given XML Schema
	 * <code>targetSchema</code> which matches the given XML element
	 * <code>originElement</code> and null otherwise.
	 * 
	 * @param originElement the XML element
	 * @param targetSchema  the XML Schema
	 * @param enclosingType the enclosing type of the XS element declaration which
	 *                      matches the XML element
	 * @param grammar       the Xerces grammar
	 * @return the location of the global xs:element declared in the given XML
	 *         Schema <code>targetSchema</code> which matches the given XML element
	 *         <code>originElement</code> and null otherwise.
	 */
	private static LocationLink findLocalXSElement(DOMElement originElement, DOMDocument targetSchema,
			XSComplexTypeDefinition enclosingType, SchemaGrammar schemaGrammar) {
		// In local xs:element case, xs:element is declared inside a complex type
		// (enclosing tye).
		// Xerces stores in the SchemaGrammar the locator (offset) for each complex type
		// (XSComplexTypeDecl)
		// Here we get the offset of the local enclosing complex type xs:complexType.
		// After that
		// we just loop of children of local xs:complexType and return the
		// location of
		// xs:element/@name which matches the tag name of the origin XML element

		// Get the location of the local xs:complexType
		int complexTypeOffset = getComplexTypeOffset(enclosingType, schemaGrammar);
		if (complexTypeOffset != -1) {
			// location of xs:complexType is found, find the xs:element declared inside the
			// xs:complexType
			DOMNode node = targetSchema.findNodeAt(complexTypeOffset);
			if (node != null && node.isElement() && node.hasChildNodes()) {
				return findXSElement((DOMElement) originElement, node.getChildNodes(), true);
			}
		}
		return null;
	}

	/**
	 * Returns the offset where the local xs:complexType is declared and -1
	 * otherwise.
	 * 
	 * @param complexType the local complex type
	 * @param grammar     the grammar where local complex type is declared.
	 * @return the offset where the local xs:complexType is declared and -1
	 *         otherwise.
	 */
	private static int getComplexTypeOffset(XSComplexTypeDefinition complexType, SchemaGrammar grammar) {
		try {
			// Xerces stores location in 2 arrays:
			// - fCTLocators array of locator
			// - fComplexTypeDecls array of XSComplexTypeDecl

			// As it's not an API, we must use Java Reflection to get thoses 2 arrays
			Field f = SchemaGrammar.class.getDeclaredField("fCTLocators");
			f.setAccessible(true);
			SimpleLocator[] fCTLocators = (SimpleLocator[]) f.get(grammar);

			// Find the location offset of the given complexType
			XSComplexTypeDecl[] fComplexTypeDecls = getXSComplexTypeDecls(grammar);
			for (int i = 0; i < fComplexTypeDecls.length; i++) {
				if (complexType.equals(fComplexTypeDecls[i])) {
					XMLLocator locator = fCTLocators[i];
					return locator != null ? locator.getCharacterOffset() : -1;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"Error while retrieving offset of local xs:complexType'" + complexType.getName() + "'.", e);
		}
		// Offset where xs:complexType is declared cannot be found
		return -1;
	}

	/**
	 * Returns all xs:complexTypes (global and local) from the given schema grammar
	 * 
	 * @param grammar the grammar
	 * @return all xs:complexTypes (global and local) from the given schema grammar
	 */
	private static XSComplexTypeDecl[] getXSComplexTypeDecls(SchemaGrammar grammar) {
		try {
			Field fc = SchemaGrammar.class.getDeclaredField("fComplexTypeDecls");
			fc.setAccessible(true);
			return (XSComplexTypeDecl[]) fc.get(grammar);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while retrieving list of xs:complexType of the grammar '"
					+ grammar.getSchemaNamespace() + "'.", e);
			return null;
		}
	}

	/**
	 * Returns the location of the xs:element declared in the given XML Schema
	 * <code>targetSchema</code> which matches the given XML element
	 * <code>originElement</code> and null otherwise.
	 * 
	 * @param originElement the XML element
	 * @param children      the children where xs:element must be searched
	 * @param inAnyLevel    true if search must be done in any level and false
	 *                      otherwise.
	 * @return
	 */
	private static LocationLink findXSElement(DOMElement originElement, NodeList children, boolean inAnyLevel) {
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element elt = (Element) n;
				if (XSDUtils.isXSElement(elt)) {
					if (originElement.getLocalName().equals(elt.getAttribute("name"))) {
						DOMAttr targetAttr = (DOMAttr) elt.getAttributeNode("name");
						LocationLink location = XMLPositionUtility.createLocationLink(originElement,
								targetAttr.getNodeAttrValue());
						return location;
					}
				}
				if (inAnyLevel && elt.hasChildNodes()) {
					LocationLink location = findXSElement(originElement, elt.getChildNodes(), inAnyLevel);
					if (location != null) {
						return location;
					}
				}
			}
		}
		return null;
	}

	private static LocationLink findLocalXSAttribute(DOMAttr originAttribute, DOMDocument targetSchema,
			XSComplexTypeDefinition enclosingType, SchemaGrammar schemaGrammar) {
		int complexTypeOffset = getComplexTypeOffset(enclosingType, schemaGrammar);
		if (complexTypeOffset != -1) {
			// location of xs:complexType is found, find the xs:attribute declared inside
			// the
			// xs:complexType
			DOMNode node = targetSchema.findNodeAt(complexTypeOffset);
			if (node != null && node.isElement() && node.hasChildNodes()) {
				return findXSAttribute(originAttribute, node.getChildNodes(), true);
			}
		}
		return null;
	}

	private static LocationLink findXSAttribute(DOMAttr originAttribute, NodeList children, boolean inAnyLevel) {
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				DOMElement elt = (DOMElement) n;
				if (XSDUtils.isXSAttribute(elt)) {
					if (originAttribute.getName().equals(elt.getAttribute("name"))) {
						DOMAttr targetAttr = (DOMAttr) elt.getAttributeNode("name");
						LocationLink location = XMLPositionUtility.createLocationLink(originAttribute.getNodeAttrName(),
								targetAttr.getNodeAttrValue());
						return location;
					}
				}
				if (inAnyLevel && elt.hasChildNodes()) {
					LocationLink location = findXSAttribute(originAttribute, elt.getChildNodes(), inAnyLevel);
					if (location != null) {
						return location;
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isDirty() {
		return tracker.isDirty();
	}
}
