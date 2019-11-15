/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4xml.settings;

import org.eclipse.lsp4xml.client.ExtendedCodeLensCapabilities;

/**
 * XML CodeLens settings
 * 
 * @author Angelo ZERR
 *
 */
public class XMLCodeLensSettings {

	private boolean enabled = false;

	private ExtendedCodeLensCapabilities codeLens;

	/**
	 * Returns true if codelens service is enabled and false otherwise.
	 * 
	 * @return true if codelens service is enabled and false otherwise.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set true if codelens service is enabled and false otherwise.
	 * 
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Returns true if the given code lens kind is supported by the client and false
	 * otherwise.
	 * 
	 * @param kind code lens kind
	 * @return true if the given code lens kind is supported by the client and false
	 *         otherwise.
	 */
	public boolean isSupportedByClient(String kind) {
		return codeLens != null && codeLens.getCodeLensKind() != null
				&& codeLens.getCodeLensKind().getValueSet() != null
				&& codeLens.getCodeLensKind().getValueSet().contains(kind);
	}

	/**
	 * Update the codelens client capabilities.
	 * 
	 * @param codelens client capabilities
	 */
	public void setCodeLens(ExtendedCodeLensCapabilities codeLens) {
		this.codeLens = codeLens;
	}
}
