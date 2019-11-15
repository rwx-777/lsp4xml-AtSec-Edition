/**
 *  Copyright (c) 2019 Red Hat Inc. and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 *  Contributors:
 *  Red Hat Inc. - initial API and implementation
 */

package org.eclipse.lsp4xml.extensions.contentmodel.settings;

import org.eclipse.lsp4j.DiagnosticSeverity;

/**
 * XMLValidationSettings
 */
public class XMLValidationSettings {

	private Boolean schema;

	private Boolean enabled;

	private boolean disallowDocTypeDecl;

	private boolean resolveExternalEntities;

	/**
	 * This severity preference to mark the root element of XML document which is
	 * not bound to a XML Schema/DTD.
	 * 
	 * Values are {ignore, hint, info, warning, error}
	 */
	private String noGrammar;

	public XMLValidationSettings() {
		// set defaults
		setSchema(true);
		setEnabled(true);
		setDisallowDocTypeDecl(false);
		setResolveExternalEntities(false);
	}

	/**
	 * @return the syntax
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param syntax the syntax to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the schema
	 */
	public boolean isSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	public void setSchema(boolean schema) {
		this.schema = schema;
	}

	public void setNoGrammar(String noGrammar) {
		this.noGrammar = noGrammar;
	}

	public String getNoGrammar() {
		return noGrammar;
	}

	/**
	 * Returns true if a fatal error is thrown if the incoming document contains a
	 * DOCTYPE declaration and false otherwise.
	 * 
	 * @return true if a fatal error is thrown if the incoming document contains a
	 *         DOCTYPE declaration and false otherwise.
	 */
	public boolean isDisallowDocTypeDecl() {
		return disallowDocTypeDecl;
	}

	/**
	 * Set true if a fatal error is thrown if the incoming document contains a
	 * DOCTYPE declaration and false otherwise.
	 * 
	 * @param disallowDocTypeDecl disallow DOCTYPE declaration.
	 */
	public void setDisallowDocTypeDecl(boolean disallowDocTypeDecl) {
		this.disallowDocTypeDecl = disallowDocTypeDecl;
	}

	/**
	 * Returns true if external entities must be resolved and false otherwise.
	 * 
	 * @return true if external entities must be resolved and false otherwise.
	 */
	public boolean isResolveExternalEntities() {
		return resolveExternalEntities;
	}

	/**
	 * Set true if external entities must be resolved and false otherwise.
	 * 
	 * @param resolveExternalEntities resolve extrenal entities
	 */
	public void setResolveExternalEntities(boolean resolveExternalEntities) {
		this.resolveExternalEntities = resolveExternalEntities;
	}

	/**
	 * Returns the <code>noGrammar</code> severity according the given settings and
	 * {@link DiagnosticSeverity#Hint} otherwise.
	 * 
	 * @param settings the settings
	 * @return the <code>noGrammar</code> severity according the given settings and
	 *         {@link DiagnosticSeverity#Hint} otherwise.
	 */
	public static DiagnosticSeverity getNoGrammarSeverity(ContentModelSettings settings) {
		DiagnosticSeverity defaultSeverity = DiagnosticSeverity.Hint;
		if (settings == null || settings.getValidation() == null) {
			return defaultSeverity;
		}
		XMLValidationSettings problems = settings.getValidation();
		String noGrammar = problems.getNoGrammar();
		if ("ignore".equalsIgnoreCase(noGrammar)) {
			// Ignore "noGrammar", return null.
			return null;
		} else if ("info".equalsIgnoreCase(noGrammar)) {
			return DiagnosticSeverity.Information;
		} else if ("warning".equalsIgnoreCase(noGrammar)) {
			return DiagnosticSeverity.Warning;
		} else if ("error".equalsIgnoreCase(noGrammar)) {
			return DiagnosticSeverity.Error;
		}
		return defaultSeverity;
	}

	public XMLValidationSettings merge(XMLValidationSettings settings) {
		if (settings != null) {
			this.schema = settings.schema;
			this.enabled = settings.enabled;
			this.disallowDocTypeDecl = settings.disallowDocTypeDecl;
			this.resolveExternalEntities = settings.resolveExternalEntities;
		}
		return this;
	}

}