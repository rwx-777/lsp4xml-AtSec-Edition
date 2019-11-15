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
package org.eclipse.lsp4xml.services;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentLink;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.FoldingRange;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ReferenceContext;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4xml.commons.BadLocationException;
import org.eclipse.lsp4xml.commons.TextDocument;
import org.eclipse.lsp4xml.customservice.AutoCloseTagResponse;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.dom.DOMElement;
import org.eclipse.lsp4xml.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lsp4xml.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lsp4xml.settings.SharedSettings;
import org.eclipse.lsp4xml.settings.XMLCodeLensSettings;
import org.eclipse.lsp4xml.settings.XMLFoldingSettings;
import org.eclipse.lsp4xml.settings.XMLFormattingOptions;
import org.eclipse.lsp4xml.settings.XMLHoverSettings;
import org.eclipse.lsp4xml.uriresolver.CacheResourceDownloadingException;
import org.eclipse.lsp4xml.utils.XMLPositionUtility;

/**
 * XML Language service.
 *
 */
public class XMLLanguageService extends XMLExtensionsRegistry {

	private static final CancelChecker NULL_CHECKER = new CancelChecker() {

		@Override
		public void checkCanceled() {
			// Do nothing
		}
	};

	private final XMLFormatter formatter;
	private final XMLHighlighting highlighting;
	private final XMLSymbolsProvider symbolsProvider;
	private final XMLCompletions completions;
	private final XMLHover hover;
	private final XMLDiagnostics diagnostics;
	private final XMLFoldings foldings;
	private final XMLDocumentLink documentLink;
	private final XMLDefinition definition;
	private final XMLTypeDefinition typeDefinition;
	private final XMLReference reference;
	private final XMLCodeLens codelens;
	private final XMLCodeActions codeActions;
	private final XMLRename rename;

	public XMLLanguageService() {
		this.formatter = new XMLFormatter(this);
		this.highlighting = new XMLHighlighting(this);
		this.symbolsProvider = new XMLSymbolsProvider(this);
		this.completions = new XMLCompletions(this);
		this.hover = new XMLHover(this);
		this.diagnostics = new XMLDiagnostics(this);
		this.foldings = new XMLFoldings(this);
		this.documentLink = new XMLDocumentLink(this);
		this.definition = new XMLDefinition(this);
		this.typeDefinition = new XMLTypeDefinition(this);
		this.reference = new XMLReference(this);
		this.codelens = new XMLCodeLens(this);
		this.codeActions = new XMLCodeActions(this);
		this.rename = new XMLRename(this);
	}

	public List<? extends TextEdit> format(TextDocument document, Range range, XMLFormattingOptions options) {
		return formatter.format(document, range, options);
	}

	public List<DocumentHighlight> findDocumentHighlights(DOMDocument xmlDocument, Position position) {
		return findDocumentHighlights(xmlDocument, position, NULL_CHECKER);
	}

	public List<DocumentHighlight> findDocumentHighlights(DOMDocument xmlDocument, Position position,
			CancelChecker cancelChecker) {
		return highlighting.findDocumentHighlights(xmlDocument, position, cancelChecker);
	}

	public List<SymbolInformation> findSymbolInformations(DOMDocument xmlDocument) {
		return findSymbolInformations(xmlDocument, NULL_CHECKER);
	}

	public List<SymbolInformation> findSymbolInformations(DOMDocument xmlDocument, CancelChecker cancelChecker) {
		return symbolsProvider.findSymbolInformations(xmlDocument, cancelChecker);
	}

	public List<DocumentSymbol> findDocumentSymbols(DOMDocument xmlDocument) {
		return findDocumentSymbols(xmlDocument, NULL_CHECKER);
	}

	public List<DocumentSymbol> findDocumentSymbols(DOMDocument xmlDocument, CancelChecker cancelChecker) {
		return symbolsProvider.findDocumentSymbols(xmlDocument, cancelChecker);
	}

	public CompletionList doComplete(DOMDocument xmlDocument, Position position, SharedSettings settings) {
		return doComplete(xmlDocument, position, settings, NULL_CHECKER);
	}

	public CompletionList doComplete(DOMDocument xmlDocument, Position position, SharedSettings settings,
			CancelChecker cancelChecker) {
		return completions.doComplete(xmlDocument, position, settings, cancelChecker);
	}

	public Hover doHover(DOMDocument xmlDocument, Position position, XMLHoverSettings settings) {
		return doHover(xmlDocument, position, settings, NULL_CHECKER);
	}

	public Hover doHover(DOMDocument xmlDocument, Position position, XMLHoverSettings settings,
			CancelChecker cancelChecker) {
		return hover.doHover(xmlDocument, position, settings, cancelChecker);
	}

	public List<Diagnostic> doDiagnostics(DOMDocument xmlDocument, CancelChecker monitor,
			XMLValidationSettings validationSettings) {
		return diagnostics.doDiagnostics(xmlDocument, monitor, validationSettings);
	}

	public CompletableFuture<Path> publishDiagnostics(DOMDocument xmlDocument,
			Consumer<PublishDiagnosticsParams> publishDiagnostics, Consumer<TextDocument> triggerValidation,
			XMLValidationSettings validationSettings, CancelChecker monitor) {
		String uri = xmlDocument.getDocumentURI();
		TextDocument document = xmlDocument.getTextDocument();
		try {
			List<Diagnostic> diagnostics = this.doDiagnostics(xmlDocument, monitor, validationSettings);
			monitor.checkCanceled();
			publishDiagnostics.accept(new PublishDiagnosticsParams(uri, diagnostics));
			return null;
		} catch (CacheResourceDownloadingException e) {
			CompletableFuture<Path> future = e.getFuture();
			if (future == null) {
				// This case comes from when URL uses ../../ and resources is not included in
				// the cache path
				// To prevent from "Path Traversal leading to Remote Command Execution (RCE)"
				publishOneDiagnosticInRoot(xmlDocument, e.getMessage(), DiagnosticSeverity.Error, publishDiagnostics);
			} else {
				// An XML Schema or DTD is being downloaded by the cache manager, but it takes
				// too long.
				// In this case:
				// - 1) we add an information message to the document element to explain that
				// validation
				// cannot be performed because the XML Schema/DTD is downloading.
				publishOneDiagnosticInRoot(xmlDocument, e.getMessage(), DiagnosticSeverity.Information,
						publishDiagnostics);
				// - 2) we restart the validation only once the XML Schema/DTD is downloaded.
				future //
						.exceptionally(downloadException -> {
							// Error while downloading the XML Schema/DTD
							publishOneDiagnosticInRoot(xmlDocument, downloadException.getCause().getMessage(),
									DiagnosticSeverity.Error, publishDiagnostics);
							return null;
						}) //
						.thenAccept((path) -> {
							if (path != null) {
								triggerValidation.accept(document);
							}
						});
			}
			return future;
		}
	}

	private static void publishOneDiagnosticInRoot(DOMDocument document, String message, DiagnosticSeverity severity,
			Consumer<PublishDiagnosticsParams> publishDiagnostics) {
		String uri = document.getDocumentURI();
		DOMElement documentElement = document.getDocumentElement();
		Range range = XMLPositionUtility.selectStartTag(documentElement);
		List<Diagnostic> diagnostics = new ArrayList<>();
		diagnostics.add(new Diagnostic(range, message, severity, "XML"));
		publishDiagnostics.accept(new PublishDiagnosticsParams(uri, diagnostics));
	}

	public List<FoldingRange> getFoldingRanges(DOMDocument xmlDocument, XMLFoldingSettings context) {
		return getFoldingRanges(xmlDocument, context, NULL_CHECKER);
	}

	public List<FoldingRange> getFoldingRanges(DOMDocument xmlDocument, XMLFoldingSettings context,
			CancelChecker cancelChecker) {
		return foldings.getFoldingRanges(xmlDocument.getTextDocument(), context, cancelChecker);
	}

	public WorkspaceEdit doRename(DOMDocument xmlDocument, Position position, String newText) {
		return rename.doRename(xmlDocument, position, newText);
	}

	public List<DocumentLink> findDocumentLinks(DOMDocument document) {
		return documentLink.findDocumentLinks(document);
	}

	public List<? extends LocationLink> findDefinition(DOMDocument xmlDocument, Position position,
			CancelChecker cancelChecker) {
		return definition.findDefinition(xmlDocument, position, cancelChecker);
	}

	public List<? extends LocationLink> findTypeDefinition(DOMDocument xmlDocument, Position position,
			CancelChecker cancelChecker) {
		return typeDefinition.findTypeDefinition(xmlDocument, position, cancelChecker);
	}

	public List<? extends Location> findReferences(DOMDocument xmlDocument, Position position, ReferenceContext context,
			CancelChecker cancelChecker) {
		return reference.findReferences(xmlDocument, position, context, cancelChecker);
	}

	public List<? extends CodeLens> getCodeLens(DOMDocument xmlDocument, XMLCodeLensSettings settings,
			CancelChecker cancelChecker) {
		return codelens.getCodelens(xmlDocument, settings, cancelChecker);
	}

	public List<CodeAction> doCodeActions(CodeActionContext context, Range range, DOMDocument document,
			XMLFormattingOptions formattingSettings) {
		return codeActions.doCodeActions(context, range, document, formattingSettings);
	}

	public AutoCloseTagResponse doTagComplete(DOMDocument xmlDocument, Position position) {
		return doTagComplete(xmlDocument, position, NULL_CHECKER);
	}

	public AutoCloseTagResponse doTagComplete(DOMDocument xmlDocument, Position position, CancelChecker cancelChecker) {
		return completions.doTagComplete(xmlDocument, position, cancelChecker);
	}

	public AutoCloseTagResponse doAutoClose(DOMDocument xmlDocument, Position position, CancelChecker cancelChecker) {
		try {
			int offset = xmlDocument.offsetAt(position);
			String text = xmlDocument.getText();
			if (offset > 0) {
				char c = text.charAt(offset - 1);
				if (c == '>' || c == '/') {
					return doTagComplete(xmlDocument, position, cancelChecker);
				}
			}
			return null;
		} catch (BadLocationException e) {
			return null;
		}
	}

}
