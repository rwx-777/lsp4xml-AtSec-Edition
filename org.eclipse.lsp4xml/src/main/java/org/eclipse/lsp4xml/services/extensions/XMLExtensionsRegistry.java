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
package org.eclipse.lsp4xml.services.extensions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4xml.services.IXMLDocumentProvider;
import org.eclipse.lsp4xml.services.extensions.codelens.ICodeLensParticipant;
import org.eclipse.lsp4xml.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lsp4xml.services.extensions.save.ISaveContext;
import org.eclipse.lsp4xml.uriresolver.URIResolverExtensionManager;

/**
 * XML extensions registry.
 *
 */
public class XMLExtensionsRegistry implements IComponentProvider {

	private static final Logger LOGGER = Logger.getLogger(XMLExtensionsRegistry.class.getName());

	private final URIResolverExtensionManager resolverExtensionManager;
	private final Collection<IXMLExtension> extensions;
	private final List<ICompletionParticipant> completionParticipants;
	private final List<IHoverParticipant> hoverParticipants;
	private final List<IDiagnosticsParticipant> diagnosticsParticipants;
	private final List<ICodeActionParticipant> codeActionsParticipants;
	private final List<IDocumentLinkParticipant> documentLinkParticipants;
	private final List<IDefinitionParticipant> definitionParticipants;
	private final List<ITypeDefinitionParticipant> typeDefinitionParticipants;
	private final List<IReferenceParticipant> referenceParticipants;
	private final List<ICodeLensParticipant> codeLensParticipants;
	private final List<IHighlightingParticipant> highlightingParticipants;
	private final List<IRenameParticipant> renameParticipants;

	private IXMLDocumentProvider documentProvider;

	private InitializeParams params;

	private ISaveContext initialSaveContext;

	private boolean initialized;

	private final Map<Class, Object> components;

	public XMLExtensionsRegistry() {
		extensions = new ArrayList<>();
		completionParticipants = new ArrayList<>();
		hoverParticipants = new ArrayList<>();
		diagnosticsParticipants = new ArrayList<>();
		codeActionsParticipants = new ArrayList<>();
		documentLinkParticipants = new ArrayList<>();
		definitionParticipants = new ArrayList<>();
		typeDefinitionParticipants = new ArrayList<>();
		referenceParticipants = new ArrayList<>();
		codeLensParticipants = new ArrayList<>();
		highlightingParticipants = new ArrayList<>();
		renameParticipants = new ArrayList<>();
		resolverExtensionManager = new URIResolverExtensionManager();
		components = new HashMap<>();
		registerComponent(resolverExtensionManager);
	}

	public void registerComponent(Object component) {
		this.components.put(component.getClass(), component);
	}

	@Override
	public <T> T getComponent(Class clazz) {
		return (T) components.get(clazz);
	}

	public void initializeParams(InitializeParams params) {
		if (initialized) {
			extensions.stream().forEach(extension -> extension.start(params, this));
		} else {
			this.params = params;
		}
	}

	public void doSave(ISaveContext saveContext) {
		if (initialized) {
			extensions.stream().forEach(extension -> extension.doSave(saveContext));
		} else {
			this.initialSaveContext = saveContext;
		}
	}

	public Collection<IXMLExtension> getExtensions() {
		initializeIfNeeded();
		return extensions;
	}

	public Collection<ICompletionParticipant> getCompletionParticipants() {
		initializeIfNeeded();
		return completionParticipants;
	}

	public Collection<IHoverParticipant> getHoverParticipants() {
		initializeIfNeeded();
		return hoverParticipants;
	}

	public Collection<IDiagnosticsParticipant> getDiagnosticsParticipants() {
		initializeIfNeeded();
		return diagnosticsParticipants;
	}

	public List<ICodeActionParticipant> getCodeActionsParticipants() {
		initializeIfNeeded();
		return codeActionsParticipants;
	}

	public Collection<IDocumentLinkParticipant> getDocumentLinkParticipants() {
		initializeIfNeeded();
		return documentLinkParticipants;
	}

	public Collection<IDefinitionParticipant> getDefinitionParticipants() {
		initializeIfNeeded();
		return definitionParticipants;
	}

	public Collection<ITypeDefinitionParticipant> getTypeDefinitionParticipants() {
		initializeIfNeeded();
		return typeDefinitionParticipants;
	}

	public Collection<IReferenceParticipant> getReferenceParticipants() {
		initializeIfNeeded();
		return referenceParticipants;
	}

	public Collection<ICodeLensParticipant> getCodeLensParticipants() {
		initializeIfNeeded();
		return codeLensParticipants;
	}

	public Collection<IHighlightingParticipant> getHighlightingParticipants() {
		initializeIfNeeded();
		return highlightingParticipants;
	}

	public Collection<IRenameParticipant> getRenameParticipants() {
		initializeIfNeeded();
		return renameParticipants;
	}

	public void initializeIfNeeded() {
		if (initialized) {
			return;
		}
		initialize();
	}

	private synchronized void initialize() {
		
		if (initialized) {
			return;
		}

		ServiceLoader<IXMLExtension> extensions = ServiceLoader.load(IXMLExtension.class);
		extensions.forEach(extension -> {
			registerExtension(extension);
		});
		initialized = true;
	}

	void registerExtension(IXMLExtension extension) {
		try {
			extensions.add(extension);
			extension.start(params, this);
			if (initialSaveContext != null) {
				extension.doSave(initialSaveContext);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while initializing extension <" + extension.getClass().getName() + ">", e);
		}
	}

	void unregisterExtension(IXMLExtension extension) {
		extensions.remove(extension);
		extension.stop(this);
	}

	public void registerCompletionParticipant(ICompletionParticipant completionParticipant) {
		completionParticipants.add(completionParticipant);
	}

	public void unregisterCompletionParticipant(ICompletionParticipant completionParticipant) {
		completionParticipants.add(completionParticipant);
	}

	public void registerHoverParticipant(IHoverParticipant hoverParticipant) {
		hoverParticipants.add(hoverParticipant);
	}

	public void unregisterHoverParticipant(IHoverParticipant hoverParticipant) {
		hoverParticipants.add(hoverParticipant);
	}

	public void registerDiagnosticsParticipant(IDiagnosticsParticipant diagnosticsParticipant) {
		diagnosticsParticipants.add(diagnosticsParticipant);
	}

	public void unregisterDiagnosticsParticipant(IDiagnosticsParticipant diagnosticsParticipant) {
		diagnosticsParticipants.add(diagnosticsParticipant);
	}

	public void registerCodeActionParticipant(ICodeActionParticipant codeActionsParticipant) {
		codeActionsParticipants.add(codeActionsParticipant);
	}

	public void unregisterCodeActionParticipant(ICodeActionParticipant codeActionsParticipant) {
		codeActionsParticipants.add(codeActionsParticipant);
	}

	public void registerDocumentLinkParticipant(IDocumentLinkParticipant documentLinkParticipant) {
		documentLinkParticipants.add(documentLinkParticipant);
	}

	public void unregisterDocumentLinkParticipant(IDocumentLinkParticipant documentLinkParticipant) {
		documentLinkParticipants.add(documentLinkParticipant);
	}

	public void registerDefinitionParticipant(IDefinitionParticipant definitionParticipant) {
		definitionParticipants.add(definitionParticipant);
	}

	public void unregisterDefinitionParticipant(IDefinitionParticipant definitionParticipant) {
		definitionParticipants.add(definitionParticipant);
	}

	public void registerTypeDefinitionParticipant(ITypeDefinitionParticipant typeDefinitionParticipant) {
		typeDefinitionParticipants.add(typeDefinitionParticipant);
	}

	public void unregisterTypeDefinitionParticipant(ITypeDefinitionParticipant typeDefinitionParticipant) {
		typeDefinitionParticipants.add(typeDefinitionParticipant);
	}

	public void registerReferenceParticipant(IReferenceParticipant referenceParticipant) {
		referenceParticipants.add(referenceParticipant);
	}

	public void unregisterReferenceParticipant(IReferenceParticipant referenceParticipant) {
		referenceParticipants.add(referenceParticipant);
	}

	public void registerCodeLensParticipant(ICodeLensParticipant codeLensParticipant) {
		codeLensParticipants.add(codeLensParticipant);
	}

	public void unregisterCodeLensParticipant(ICodeLensParticipant codeLensParticipant) {
		codeLensParticipants.add(codeLensParticipant);
	}

	public void registerHighlightingParticipant(IHighlightingParticipant highlightingParticipant) {
		highlightingParticipants.add(highlightingParticipant);
	}

	public void unregisterHighlightingParticipant(IHighlightingParticipant highlightingParticipant) {
		highlightingParticipants.add(highlightingParticipant);
	}

	public void registerRenameParticipant(IRenameParticipant renameParticipant) {
		renameParticipants.add(renameParticipant);
	}

	public void unregisterRenameParticipant(IRenameParticipant renameParticipant) {
		renameParticipants.add(renameParticipant);
	}

	/**
	 * Returns the XML Document provider and null otherwise.
	 * 
	 * @return the XML Document provider and null otherwise.
	 */
	public IXMLDocumentProvider getDocumentProvider() {
		return documentProvider;
	}

	/**
	 * Set the XML Document provider
	 * 
	 * @param documentProvider XML Document provider
	 */
	public void setDocumentProvider(IXMLDocumentProvider documentProvider) {
		this.documentProvider = documentProvider;
	}

	public URIResolverExtensionManager getResolverExtensionManager() {
		return resolverExtensionManager;
	}

}