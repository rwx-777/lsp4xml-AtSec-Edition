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
package org.eclipse.lsp4xml.dom.parser;

/**
 * XML Token type.
 *
 */
public enum TokenType {

	StartCommentTag,
	Comment,
	EndCommentTag,
	CDATATagOpen,
	CDATAContent,
	CDATATagClose,
	StartTagOpen,
	StartTagClose,
	StartTagSelfClose,
	StartTag,
	EndTagOpen,
	EndTagClose,
	EndTag,
	DelimiterAssign,
	AttributeName,
	AttributeValue,
	StartPrologOrPI,
	PrologName,
	PIName,
	PIContent,
	PIEnd,
	PrologEnd,
	Content,
	Whitespace,
	Unknown,
	EOS, 
	// DTD
	DTDStartDoctypeTag, 
	DTDDoctypeName, 
	DTDDocTypeKindPUBLIC, 
	DTDDocTypeKindSYSTEM, 
	DTDDoctypePublicId, 
	DTDDoctypeSystemId,  
	DTDEndDoctypeTag, 
	DTDStartInternalSubset,
	DTDEndInternalSubset,
	// DTD Element declaration
	DTDStartElement,
	DTDElementDeclName,
	DTDElementCategory, 
	DTDStartElementContent, 
	DTDElementContent, 
	DTDEndElementContent, 
	
	// DTD AttrList declaration
	DTDStartAttlist,
	DTDAttlistElementName, 
	DTDAttlistAttributeValue, 
	DTDAttlistAttributeType, 
	DTDAttlistAttributeName,
	
	// DTD Entity
	DTDStartEntity, 
	DTDEntityPercent,
	DTDEntityKindPUBLIC, 
	DTDEntityKindSYSTEM,
	DTDEntityPublicId,
	DTDEntitySystemId,
	DTDEntityName, 
	DTDEntityValue, 

	// DTD Notation
	DTDStartNotation, 
	DTDNotationName, 
	DTDNotationKindPUBLIC,
	DTDNotationKindSYSTEM,
	DTDNotationPublicId,
	DTDNotationSystemId,
	
	//For any DTD Decl Tag that has an unrecognized parameter
	DTDUnrecognizedParameters,

	//End of any DTD Decl Tag
	DTDEndTag;

	
}