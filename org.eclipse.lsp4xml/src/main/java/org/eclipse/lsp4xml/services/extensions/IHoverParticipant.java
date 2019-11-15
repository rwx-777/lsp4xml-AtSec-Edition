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
package org.eclipse.lsp4xml.services.extensions;

/**
 * Hover participant API.
 *
 */
public interface IHoverParticipant {

	/**
	 * onTag method
	 *
	 * @param hoverRequest the hover request.
	 * @return the Value of MarkupContent {@link String}
	 */
	String onTag(IHoverRequest request) throws Exception;

	/**
	 * onAttributeName method
	 *
	 * @param hoverRequest the hover request.
	 * @return the Value of MarkupContent {@link String}
	 */
	String onAttributeName(IHoverRequest request) throws Exception;

	/**
	 * onAttributeValue method
	 *
	 * @param hoverRequest the hover request.
	 * @return the Value of MarkupContent {@link String}
	 */
	String onAttributeValue(IHoverRequest request) throws Exception;

}
