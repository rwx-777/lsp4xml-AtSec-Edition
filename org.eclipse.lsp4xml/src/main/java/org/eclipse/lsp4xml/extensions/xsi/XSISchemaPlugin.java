/**
 *  Copyright (c) 2019 Red Hat, Inc. and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v20.html
 *
 *  Contributors:
 *  Red Hat Inc. - initial API and implementation
 */

package org.eclipse.lsp4xml.extensions.xsi;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4xml.services.extensions.ICompletionParticipant;
import org.eclipse.lsp4xml.services.extensions.IHoverParticipant;
import org.eclipse.lsp4xml.services.extensions.IXMLExtension;
import org.eclipse.lsp4xml.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lsp4xml.services.extensions.save.ISaveContext;

/**
 * Plugin to handle the xml prolog:
 *  {@code <?xml ... ?>}.  
 * 
 * 
 * Loaded by service loader in 'resources' folder.
 */
public class XSISchemaPlugin implements IXMLExtension {

	ICompletionParticipant completionParticipant = new XSICompletionParticipant();
	IHoverParticipant hoverParticipant = new XSIHoverParticipant();

	@Override
	public void start(InitializeParams params, XMLExtensionsRegistry registry) {
		registry.registerCompletionParticipant(completionParticipant);
		registry.registerHoverParticipant(hoverParticipant);
	}

	@Override
	public void stop(XMLExtensionsRegistry registry) {
		registry.unregisterCompletionParticipant(completionParticipant);
		registry.unregisterHoverParticipant(hoverParticipant);
	}

	@Override
	public void doSave(ISaveContext context) {

	}
}