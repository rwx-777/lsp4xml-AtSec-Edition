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
package org.eclipse.lsp4xml.extensions.dtd;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4xml.extensions.contentmodel.model.ContentModelManager;
import org.eclipse.lsp4xml.extensions.contentmodel.model.ContentModelProvider;
import org.eclipse.lsp4xml.extensions.dtd.contentmodel.CMDTDContentModelProvider;
import org.eclipse.lsp4xml.extensions.dtd.participants.DTDCodeLensParticipant;
import org.eclipse.lsp4xml.extensions.dtd.participants.DTDDefinitionParticipant;
import org.eclipse.lsp4xml.extensions.dtd.participants.DTDHighlightingParticipant;
import org.eclipse.lsp4xml.extensions.dtd.participants.DTDReferenceParticipant;
import org.eclipse.lsp4xml.extensions.dtd.participants.diagnostics.DTDDiagnosticsParticipant;
import org.eclipse.lsp4xml.services.extensions.IDefinitionParticipant;
import org.eclipse.lsp4xml.services.extensions.IHighlightingParticipant;
import org.eclipse.lsp4xml.services.extensions.IReferenceParticipant;
import org.eclipse.lsp4xml.services.extensions.IXMLExtension;
import org.eclipse.lsp4xml.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lsp4xml.services.extensions.codelens.ICodeLensParticipant;
import org.eclipse.lsp4xml.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lsp4xml.services.extensions.save.ISaveContext;

/**
 * DTD plugin.
 */
public class DTDPlugin implements IXMLExtension {

	private final IDiagnosticsParticipant diagnosticsParticipant;
	private final IDefinitionParticipant definitionParticipant;
	private final IHighlightingParticipant highlightingParticipant;
	private final IReferenceParticipant referenceParticipant;
	private final ICodeLensParticipant codeLensParticipant;

	public DTDPlugin() {
		diagnosticsParticipant = new DTDDiagnosticsParticipant();
		definitionParticipant = new DTDDefinitionParticipant();
		highlightingParticipant = new DTDHighlightingParticipant();
		referenceParticipant = new DTDReferenceParticipant();
		codeLensParticipant = new DTDCodeLensParticipant();
	}

	@Override
	public void doSave(ISaveContext context) {

	}

	@Override
	public void start(InitializeParams params, XMLExtensionsRegistry registry) {
		// register DTD content model provider
		ContentModelProvider modelProvider = new CMDTDContentModelProvider(registry.getResolverExtensionManager());
		ContentModelManager modelManager = registry.getComponent(ContentModelManager.class);
		modelManager.registerModelProvider(modelProvider);
		// register diagnostic participant
		registry.registerDiagnosticsParticipant(diagnosticsParticipant);
		// register definition participant
		registry.registerDefinitionParticipant(definitionParticipant);
		// register highlighting participant
		registry.registerHighlightingParticipant(highlightingParticipant);
		// register reference participant
		registry.registerReferenceParticipant(referenceParticipant);
		// register codelens participant
		registry.registerCodeLensParticipant(codeLensParticipant);
	}

	@Override
	public void stop(XMLExtensionsRegistry registry) {
		// unregister diagnostic participant
		registry.unregisterDiagnosticsParticipant(diagnosticsParticipant);
		// unregister definition participant
		registry.unregisterDefinitionParticipant(definitionParticipant);
		// unregister highlighting participant
		registry.unregisterHighlightingParticipant(highlightingParticipant);
		// register reference participant
		registry.unregisterReferenceParticipant(referenceParticipant);
		// unregister codelens participant
		registry.unregisterCodeLensParticipant(codeLensParticipant);
	}
}
