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
package org.eclipse.lsp4xml;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DocumentHighlight;
import org.eclipse.lsp4j.DocumentHighlightKind;
import org.eclipse.lsp4j.DocumentLink;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverCapabilities;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.ReferenceContext;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.TextDocumentEdit;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.VersionedTextDocumentIdentifier;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4xml.client.CodeLensKind;
import org.eclipse.lsp4xml.client.CodeLensKindCapabilities;
import org.eclipse.lsp4xml.client.ExtendedCodeLensCapabilities;
import org.eclipse.lsp4xml.commons.BadLocationException;
import org.eclipse.lsp4xml.commons.TextDocument;
import org.eclipse.lsp4xml.customservice.AutoCloseTagResponse;
import org.eclipse.lsp4xml.dom.DOMDocument;
import org.eclipse.lsp4xml.dom.DOMParser;
import org.eclipse.lsp4xml.extensions.contentmodel.settings.ContentModelSettings;
import org.eclipse.lsp4xml.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lsp4xml.services.XMLLanguageService;
import org.eclipse.lsp4xml.services.extensions.diagnostics.IXMLErrorCode;
import org.eclipse.lsp4xml.services.extensions.save.AbstractSaveContext;
import org.eclipse.lsp4xml.settings.SharedSettings;
import org.eclipse.lsp4xml.settings.XMLCodeLensSettings;
import org.eclipse.lsp4xml.settings.XMLCompletionSettings;
import org.eclipse.lsp4xml.settings.XMLFormattingOptions;
import org.eclipse.lsp4xml.settings.XMLHoverSettings;
import org.eclipse.lsp4xml.utils.StringUtils;
import org.junit.Assert;

/**
 * XML assert.
 *
 */
public class XMLAssert {

	// ------------------- Completion assert

	private static final String FILE_URI = "test.xml";

	public static class SettingsSaveContext extends AbstractSaveContext {

		public SettingsSaveContext(Object settings) {
			super(settings);
		}

		@Override
		public DOMDocument getDocument(String uri) {
			return null;
		}

		@Override
		public void collectDocumentToValidate(Predicate<DOMDocument> validateDocumentPredicate) {

		}

	}

	public static void testCompletionFor(String value, CompletionItem... expectedItems) throws BadLocationException {
		testCompletionFor(value, null, expectedItems);
	}

	public static void testCompletionFor(String value, String catalogPath, CompletionItem... expectedItems)
			throws BadLocationException {
		testCompletionFor(value, catalogPath, null, null, expectedItems);
	}

	public static void testCompletionFor(String value, int expectedCount, CompletionItem... expectedItems)
			throws BadLocationException {
		testCompletionFor(value, null, null, expectedCount, expectedItems);
	}

	public static void testCompletionFor(String value, String catalogPath, String fileURI, Integer expectedCount,
			CompletionItem... expectedItems) throws BadLocationException {
		testCompletionFor(new XMLLanguageService(), value, catalogPath, null, fileURI, expectedCount, true,
				expectedItems);
	}

	public static void testCompletionFor(String value, boolean autoCloseTags, CompletionItem... expectedItems)
			throws BadLocationException {
		testCompletionFor(new XMLLanguageService(), value, null, null, null, null, autoCloseTags, expectedItems);
	}

	public static void testCompletionFor(XMLLanguageService xmlLanguageService, String value, String catalogPath,
			Consumer<XMLLanguageService> customConfiguration, String fileURI, Integer expectedCount,
			boolean autoCloseTags, CompletionItem... expectedItems) throws BadLocationException {
		testCompletionFor(xmlLanguageService, value, catalogPath, customConfiguration, fileURI, expectedCount,
				new XMLCompletionSettings(autoCloseTags), expectedItems);
	}

	public static void testCompletionFor(XMLLanguageService xmlLanguageService, String value, String catalogPath,
			Consumer<XMLLanguageService> customConfiguration, String fileURI, Integer expectedCount,
			XMLCompletionSettings completionSettings, CompletionItem... expectedItems) throws BadLocationException {
		testCompletionFor(xmlLanguageService, value, catalogPath, customConfiguration, fileURI, expectedCount,
				completionSettings, new XMLFormattingOptions(4, true), expectedItems);
	}

	public static void testCompletionFor(XMLLanguageService xmlLanguageService, String value, String catalogPath,
			Consumer<XMLLanguageService> customConfiguration, String fileURI, Integer expectedCount,
			XMLCompletionSettings completionSettings, XMLFormattingOptions formattingSettings,
			CompletionItem... expectedItems) throws BadLocationException {
		int offset = value.indexOf('|');
		value = value.substring(0, offset) + value.substring(offset + 1);

		TextDocument document = new TextDocument(value, fileURI != null ? fileURI : "test://test/test.xml");
		Position position = document.positionAt(offset);
		DOMDocument htmlDoc = DOMParser.getInstance().parse(document, xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> htmlDoc);

		ContentModelSettings cmSettings = new ContentModelSettings();
		cmSettings.setUseCache(false);
		// Configure XML catalog for XML schema
		if (catalogPath != null) {
			cmSettings.setCatalogs(new String[] { catalogPath });
		}
		xmlLanguageService.doSave(new SettingsSaveContext(cmSettings));
		xmlLanguageService.initializeIfNeeded();

		if (customConfiguration != null) {
			customConfiguration.accept(xmlLanguageService);
		}

		SharedSettings sharedSettings = new SharedSettings();
		sharedSettings.getFormattingSettings().merge(formattingSettings);
		sharedSettings.getCompletionSettings().merge(completionSettings);
		sharedSettings.getCompletionSettings().setCapabilities(completionSettings.getCompletionCapabilities());
		CompletionList list = xmlLanguageService.doComplete(htmlDoc, position, sharedSettings);

		// no duplicate labels
		List<String> labels = list.getItems().stream().map(i -> i.getLabel()).sorted().collect(Collectors.toList());
		String previous = null;
		for (String label : labels) {
			Assert.assertTrue(
					"Duplicate label " + label + " in " + labels.stream().collect(Collectors.joining(",")) + "}",
					previous != label);
			previous = label;
		}
		if (expectedCount != null) {
			Assert.assertEquals(expectedCount.intValue(), list.getItems().size());
		}
		if (expectedItems != null) {
			for (CompletionItem item : expectedItems) {
				assertCompletion(list, item, document, offset);
			}
		}
	}

	private static void assertCompletion(CompletionList completions, CompletionItem expected, TextDocument document,
			int offset) {
		List<CompletionItem> matches = completions.getItems().stream().filter(completion -> {
			return expected.getLabel().equals(completion.getLabel());
		}).collect(Collectors.toList());

		Assert.assertEquals(
				expected.getLabel() + " should only exist once: Actual: "
						+ completions.getItems().stream().map(c -> c.getLabel()).collect(Collectors.joining(",")),
				1, matches.size());

		CompletionItem match = matches.get(0);
		/*
		 * if (expected.documentation != null) {
		 * Assert.assertEquals(match.getDocumentation().getRight().getValue(),
		 * expected.getd); } if (expected.kind) { Assert.assertEquals(match.kind,
		 * expected.kind); }
		 */
		if (expected.getTextEdit() != null && match.getTextEdit() != null) {
			if (expected.getTextEdit().getNewText() != null) {
				Assert.assertEquals(expected.getTextEdit().getNewText(), match.getTextEdit().getNewText());
			}
			Range r = expected.getTextEdit().getRange();
			if (r != null && r.getStart() != null && r.getEnd() != null) {
				Assert.assertEquals(expected.getTextEdit().getRange(), match.getTextEdit().getRange());
			}
		}
		if (expected.getFilterText() != null && match.getFilterText() != null) {
			Assert.assertEquals(expected.getFilterText(), match.getFilterText());
		}

		if (expected.getDocumentation() != null) {
			Assert.assertEquals(expected.getDocumentation(), match.getDocumentation());
		}

	}

	public static CompletionItem c(String label, TextEdit textEdit, String filterText, String documentation) {
		return c(label, textEdit, filterText, documentation, null);
	}

	public static CompletionItem c(String label, TextEdit textEdit, String filterText, String documentation,
			String kind) {
		CompletionItem item = new CompletionItem();
		item.setLabel(label);
		item.setFilterText(filterText);
		item.setTextEdit(textEdit);
		if (kind == null) {
			item.setDocumentation(documentation);
		} else {
			item.setDocumentation(new MarkupContent(kind, documentation));
		}
		return item;
	}

	public static CompletionItem c(String label, TextEdit textEdit, String filterText) {
		CompletionItem item = new CompletionItem();
		item.setLabel(label);
		item.setFilterText(filterText);
		item.setTextEdit(textEdit);
		return item;
	}

	public static CompletionItem c(String label, String newText) {
		return c(label, newText, null);
	}

	public static CompletionItem c(String label, String newText, String filterText) {
		return c(label, newText, new Range(), filterText);
	}

	public static CompletionItem c(String label, String newText, Range range, String filterText) {
		return c(label, new TextEdit(range, newText), filterText);
	}

	public static void testTagCompletion(String value, String expected) throws BadLocationException {
		int offset = value.indexOf('|');
		value = value.substring(0, offset) + value.substring(offset + 1);

		XMLLanguageService ls = new XMLLanguageService();

		TextDocument document = new TextDocument(value, "test://test/test.html");
		Position position = document.positionAt(offset);
		DOMDocument htmlDoc = DOMParser.getInstance().parse(document, ls.getResolverExtensionManager());

		AutoCloseTagResponse response = ls.doTagComplete(htmlDoc, position);
		if (expected == null) {
			Assert.assertNull(response);
			return;
		}
		String actual = response.snippet;
		Assert.assertEquals(expected, actual);
	}

	// ------------------- Diagnostics assert

	public static void testDiagnosticsFor(String xml, Diagnostic... expected) {
		testDiagnosticsFor(xml, null, expected);
	}

	public static void testDiagnosticsFor(String xml, String catalogPath, Diagnostic... expected) {
		testDiagnosticsFor(xml, catalogPath, null, null, expected);
	}

	public static void testDiagnosticsFor(String xml, String catalogPath, Consumer<XMLLanguageService> configuration,
			String fileURI, Diagnostic... expected) {
		testDiagnosticsFor(xml, catalogPath, configuration, fileURI, true, expected);
	}

	public static void testDiagnosticsFor(String xml, String catalogPath, Consumer<XMLLanguageService> configuration,
			String fileURI, boolean filter, Diagnostic... expected) {
		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		XMLValidationSettings problems = new XMLValidationSettings();
		problems.setNoGrammar("ignore");
		settings.setValidation(problems);
		if (catalogPath != null) {
			// Configure XML catalog for XML schema
			settings.setCatalogs(new String[] { catalogPath });
		}
		testDiagnosticsFor(xml, catalogPath, configuration, fileURI, filter, settings, expected);
	}

	public static void testDiagnosticsFor(String xml, String catalogPath, Consumer<XMLLanguageService> configuration,
			String fileURI, boolean filter, ContentModelSettings settings, Diagnostic... expected) {
		testDiagnosticsFor(new XMLLanguageService(), xml, catalogPath, configuration, fileURI, filter, settings,
				expected);
	}

	public static void testDiagnosticsFor(XMLLanguageService xmlLanguageService, String xml, String catalogPath,
			Consumer<XMLLanguageService> configuration, String fileURI, boolean filter, ContentModelSettings settings,
			Diagnostic... expected) {
		TextDocument document = new TextDocument(xml, fileURI != null ? fileURI : "test.xml");

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		xmlLanguageService.doSave(new SettingsSaveContext(settings));
		if (configuration != null) {
			xmlLanguageService.initializeIfNeeded();
			configuration.accept(xmlLanguageService);
		}

		List<Diagnostic> actual = xmlLanguageService.doDiagnostics(xmlDocument, () -> {
		}, settings.getValidation());
		if (expected == null) {
			assertTrue(actual.isEmpty());
			return;
		}
		assertDiagnostics(actual, Arrays.asList(expected), filter);
	}

	public static void assertDiagnostics(List<Diagnostic> actual, Diagnostic... expected) {
		assertDiagnostics(actual, Arrays.asList(expected), true);
	}

	public static void assertDiagnostics(List<Diagnostic> actual, List<Diagnostic> expected, boolean filter) {
		List<Diagnostic> received = actual;
		final boolean filterMessage;
		if (expected != null && !expected.isEmpty() && !StringUtils.isEmpty(expected.get(0).getMessage())) {
			filterMessage = true;
		} else {
			filterMessage = false;
		}
		if (filter) {
			received = actual.stream().map(d -> {
				Diagnostic simpler = new Diagnostic(d.getRange(), "");
				simpler.setCode(d.getCode());
				if (filterMessage) {
					simpler.setMessage(d.getMessage());
				}
				return simpler;
			}).collect(Collectors.toList());
		}
		Assert.assertEquals("Unexpected diagnostics:\n" + actual, expected, received);
	}

	public static Diagnostic d(int startLine, int startCharacter, int endLine, int endCharacter, IXMLErrorCode code) {
		return d(startLine, startCharacter, endLine, endCharacter, code, "");
	}

	public static Diagnostic d(int startLine, int startCharacter, int endCharacter, IXMLErrorCode code) {
		// Diagnostic on 1 line
		return d(startLine, startCharacter, startLine, endCharacter, code);
	}

	public static Diagnostic d(int startLine, int startCharacter, int endLine, int endCharacter, IXMLErrorCode code,
			String message) {
		// Diagnostic on 1 line
		return new Diagnostic(r(startLine, startCharacter, endLine, endCharacter), message, null, null, code.getCode());
	}

	public static Range r(int startLine, int startCharacter, int endLine, int endCharacter) {
		return new Range(new Position(startLine, startCharacter), new Position(endLine, endCharacter));
	}

	public static ContentModelSettings getContentModelSettings(boolean isEnabled, boolean isSchema) {
		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		XMLValidationSettings problems = new XMLValidationSettings();
		problems.setNoGrammar("ignore");
		settings.setValidation(problems);
		XMLValidationSettings diagnostics = new XMLValidationSettings();
		diagnostics.setEnabled(isEnabled);
		diagnostics.setSchema(isSchema);
		settings.setValidation(diagnostics);
		return settings;
	}

	// ------------------- Publish Diagnostics assert

	public static void testPublishDiagnosticsFor(String xml, String fileURI, Consumer<XMLLanguageService> configuration,
			PublishDiagnosticsParams... expected) {
		List<PublishDiagnosticsParams> actual = new ArrayList<>();
		XMLLanguageService xmlLanguageService = new XMLLanguageService();
		if (configuration != null) {
			xmlLanguageService.initializeIfNeeded();
			configuration.accept(xmlLanguageService);
		}
		DOMDocument xmlDocument = DOMParser.getInstance().parse(xml, fileURI,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		publishDiagnostics(xmlDocument, actual, xmlLanguageService);

		Assert.assertEquals(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(fileURI, actual.get(i).getUri());
			assertDiagnostics(actual.get(i).getDiagnostics(), expected[i].getDiagnostics(), false);
		}
	}

	public static void publishDiagnostics(DOMDocument xmlDocument, List<PublishDiagnosticsParams> actual,
			XMLLanguageService languageService) {
		CompletableFuture<Path> error = languageService.publishDiagnostics(xmlDocument, params -> {
			actual.add(params);
		}, (doc) -> {
			// Retrigger validation
			publishDiagnostics(xmlDocument, actual, languageService);
		}, null, () -> {
		});

		if (error != null) {
			try {
				error.join();
				// Wait for 500 ms to collect the last params
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static PublishDiagnosticsParams pd(String uri, Diagnostic... diagnostics) {
		return new PublishDiagnosticsParams(uri, Arrays.asList(diagnostics));
	}

	// ------------------- CodeAction assert

	public static void testCodeActionsFor(String xml, Diagnostic diagnostic,
			CodeAction... expected) throws BadLocationException {
		testCodeActionsFor(xml, diagnostic, null, expected);
	}

	public static void testCodeActionsFor(String xml, Diagnostic diagnostic, String catalogPath,
			CodeAction... expected) throws BadLocationException {
		SharedSettings settings = new SharedSettings();
		settings.getFormattingSettings().setTabSize(4);
		settings.getFormattingSettings().setInsertSpaces(false);
		testCodeActionsFor(xml, diagnostic, catalogPath, settings, expected);

	}

	public static void testCodeActionsFor(String xml, Diagnostic diagnostic, String catalogPath,
			SharedSettings sharedSettings, CodeAction... expected) throws BadLocationException {

		int offset = xml.indexOf('|');
		Range range = null;

		if (offset != -1) {
			xml = xml.substring(0, offset) + xml.substring(offset + 1);
		}
		TextDocument document = new TextDocument(xml.toString(), FILE_URI);

		if (offset != -1) {
			Position position = document.positionAt(offset);
			range = new Range(position, position);
		} else {
			range = diagnostic.getRange();
		}

		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		ContentModelSettings cmSettings = new ContentModelSettings();
		cmSettings.setUseCache(false);
		if (catalogPath != null) {
			// Configure XML catalog for XML schema
			cmSettings.setCatalogs(new String[] { catalogPath });
		}
		xmlLanguageService.doSave(new SettingsSaveContext(cmSettings));

		CodeActionContext context = new CodeActionContext();
		context.setDiagnostics(Arrays.asList(diagnostic));
		DOMDocument xmlDoc = DOMParser.getInstance().parse(document, xmlLanguageService.getResolverExtensionManager());

		XMLFormattingOptions formattingSettings;
		if (sharedSettings != null && sharedSettings.getFormattingSettings() != null) {
			formattingSettings = sharedSettings.getFormattingSettings();
		} else {
			formattingSettings = new XMLFormattingOptions(4, false);
		}
		List<CodeAction> actual = xmlLanguageService.doCodeActions(context, range, xmlDoc, formattingSettings);
		assertCodeActions(actual, expected);
	}

	public static void assertCodeActions(List<CodeAction> actual, CodeAction... expected) {
		actual.stream().forEach(ca -> {
			// we don't want to compare title, etc
			ca.setCommand(null);
			ca.setKind(null);
			ca.setTitle("");
			if (ca.getDiagnostics() != null) {
				ca.getDiagnostics().forEach(d -> {
					d.setSeverity(null);
					d.setMessage("");
					d.setSource(null);
				});
			}
		});

		Assert.assertEquals(expected.length, actual.size());
		Assert.assertArrayEquals(expected, actual.toArray());
	}

	public static CodeAction ca(Diagnostic d, TextEdit te) {
		CodeAction codeAction = new CodeAction();
		codeAction.setTitle("");
		codeAction.setDiagnostics(Arrays.asList(d));

		VersionedTextDocumentIdentifier versionedTextDocumentIdentifier = new VersionedTextDocumentIdentifier(FILE_URI,
				0);

		TextDocumentEdit textDocumentEdit = new TextDocumentEdit(versionedTextDocumentIdentifier,
				Collections.singletonList(te));
		WorkspaceEdit workspaceEdit = new WorkspaceEdit(Collections.singletonList(Either.forLeft(textDocumentEdit)));
		codeAction.setEdit(workspaceEdit);
		return codeAction;
	}

	public static TextEdit te(int startLine, int startCharacter, int endLine, int endCharacter, String newText) {
		TextEdit textEdit = new TextEdit();
		textEdit.setNewText(newText);
		textEdit.setRange(r(startLine, startCharacter, endLine, endCharacter));
		return textEdit;
	}

	// ------------------- Hover assert

	public static void assertHover(String value) throws BadLocationException {
		assertHover(value, null, null);
	}

	public static void assertHover(String value, String expectedHoverLabel, Integer expectedHoverOffset)
			throws BadLocationException {
		assertHover(new XMLLanguageService(), value, null, null, expectedHoverLabel, expectedHoverOffset);
	}

	public static void assertHover(XMLLanguageService xmlLanguageService, String value, String catalogPath,
			String fileURI, String expectedHoverLabel, Integer expectedHoverOffset) throws BadLocationException {
		int offset = value.indexOf("|");
		value = value.substring(0, offset) + value.substring(offset + 1);

		TextDocument document = new TextDocument(value, fileURI != null ? fileURI : "test://test/test.html");

		Position position = document.positionAt(offset);

		DOMDocument htmlDoc = DOMParser.getInstance().parse(document, xmlLanguageService.getResolverExtensionManager());
		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		// Configure XML catalog for XML schema
		if (catalogPath != null) {
			settings.setCatalogs(new String[] { catalogPath });
		}
		xmlLanguageService.doSave(new SettingsSaveContext(settings));

		XMLHoverSettings hoverSettings = new XMLHoverSettings();
		HoverCapabilities capabilities = new HoverCapabilities(Arrays.asList(MarkupKind.MARKDOWN), false);
		hoverSettings.setCapabilities(capabilities);
		Hover hover = xmlLanguageService.doHover(htmlDoc, position, hoverSettings);
		if (expectedHoverLabel == null) {
			Assert.assertNull(hover);
		} else {
			String actualHoverLabel = getHoverLabel(hover);
			Assert.assertEquals(expectedHoverLabel, actualHoverLabel);
			if (expectedHoverOffset != null) {
				Assert.assertNotNull(hover.getRange());
				Assert.assertNotNull(hover.getRange().getStart());
				Assert.assertEquals(expectedHoverOffset.intValue(), hover.getRange().getStart().getCharacter());
			}
		}
	}

	private static String getHoverLabel(Hover hover) {
		Either<List<Either<String, MarkedString>>, MarkupContent> contents = hover != null ? hover.getContents() : null;
		if (contents == null) {
			return null;
		}
		return contents.getRight().getValue();
	}

	// ------------------- Links assert

	public static void testDocumentLinkFor(String xml, String fileURI, DocumentLink... expected) {
		TextDocument document = new TextDocument(xml, fileURI != null ? fileURI : "test.xml");

		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		xmlLanguageService.doSave(new SettingsSaveContext(settings));

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		List<DocumentLink> actual = xmlLanguageService.findDocumentLinks(xmlDocument);
		assertDocumentLinks(actual, expected);

	}

	public static DocumentLink dl(Range range, String target) {
		return new DocumentLink(range, target);
	}

	public static void assertDocumentLinks(List<DocumentLink> actual, DocumentLink... expected) {
		Assert.assertEquals(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(" Range test '" + i + "' link", expected[i].getRange(), actual.get(i).getRange());
			Assert.assertEquals(" Target test '" + i + "' link", Paths.get(expected[i].getTarget()).toUri().toString(),
					actual.get(i).getTarget());
		}
	}

	// ------------------- DocumentSymbol assert

	public static void testDocumentSymbolsFor(String xml, DocumentSymbol... expected) {
		testDocumentSymbolsFor(xml, null, expected);
	}

	public static void testDocumentSymbolsFor(String xml, String fileURI, DocumentSymbol... expected) {
		TextDocument document = new TextDocument(xml, fileURI != null ? fileURI : "test.xml");

		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		xmlLanguageService.doSave(new SettingsSaveContext(settings));

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		List<DocumentSymbol> actual = xmlLanguageService.findDocumentSymbols(xmlDocument);
		assertDocumentSymbols(actual, expected);

	}

	public static DocumentSymbol ds(final String name, final SymbolKind kind, final Range range,
			final Range selectionRange, final String detail, final List<DocumentSymbol> children) {
		return new DocumentSymbol(name, kind, range, selectionRange, detail, children);
	}

	public static void assertDocumentSymbols(List<DocumentSymbol> actual, DocumentSymbol... expected) {
		Assert.assertEquals(expected.length, actual.size());
		Assert.assertArrayEquals(expected, actual.toArray());
	}

	// ------------------- Definition assert

	public static void testDefinitionFor(String xml, LocationLink... expected) throws BadLocationException {
		testDefinitionFor(xml, null, expected);
	}

	public static void testDefinitionFor(String value, String fileURI, LocationLink... expected)
			throws BadLocationException {
		int offset = value.indexOf('|');
		value = value.substring(0, offset) + value.substring(offset + 1);

		TextDocument document = new TextDocument(value, fileURI != null ? fileURI : "test://test/test.xml");
		Position position = document.positionAt(offset);

		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		xmlLanguageService.doSave(new SettingsSaveContext(settings));

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		List<? extends LocationLink> actual = xmlLanguageService.findDefinition(xmlDocument, position, () -> {
		});
		assertLocationLink(actual, expected);

	}

	public static LocationLink ll(final String uri, final Range originRange, Range targetRange) {
		return new LocationLink(uri, targetRange, targetRange, originRange);
	}

	public static void assertLocationLink(List<? extends LocationLink> actual, LocationLink... expected) {
		Assert.assertEquals(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			actual.get(i).setTargetUri(actual.get(i).getTargetUri().replaceAll("file:///", "file:/"));
			expected[i].setTargetUri(expected[i].getTargetUri().replaceAll("file:///", "file:/"));
		}
		Assert.assertArrayEquals(expected, actual.toArray());
	}

	// ------------------- Type Definition assert

	public static void testTypeDefinitionFor(XMLLanguageService xmlLanguageService, String value, String fileURI,
			LocationLink... expected) throws BadLocationException {
		testTypeDefinitionFor(xmlLanguageService, null, value, fileURI, expected);
	}

	public static void testTypeDefinitionFor(XMLLanguageService xmlLanguageService, String catalogPath, String value,
			String fileURI, LocationLink... expected) throws BadLocationException {
		testTypeDefinitionFor(xmlLanguageService, catalogPath, null, value, fileURI, expected);
	}

	public static void testTypeDefinitionFor(XMLLanguageService xmlLanguageService, String catalogPath,
			Consumer<XMLLanguageService> customConfiguration, String value, String fileURI, LocationLink... expected)
			throws BadLocationException {
		int offset = value.indexOf('|');
		value = value.substring(0, offset) + value.substring(offset + 1);

		TextDocument document = new TextDocument(value, fileURI != null ? fileURI : "test://test/test.xml");
		Position position = document.positionAt(offset);

		ContentModelSettings cmSettings = new ContentModelSettings();
		cmSettings.setUseCache(false);
		// Configure XML catalog for XML schema
		if (catalogPath != null) {
			cmSettings.setCatalogs(new String[] { catalogPath });
		}
		xmlLanguageService.doSave(new SettingsSaveContext(cmSettings));
		xmlLanguageService.initializeIfNeeded();

		if (customConfiguration != null) {
			customConfiguration.accept(xmlLanguageService);
		}

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		List<? extends LocationLink> actual = xmlLanguageService.findTypeDefinition(xmlDocument, position, () -> {
		});
		assertLocationLink(actual, expected);

	}

	// ------------------- Reference assert

	public static void testReferencesFor(String xml, Location... expected) throws BadLocationException {
		testReferencesFor(xml, null, expected);
	}

	public static void testReferencesFor(String value, String fileURI, Location... expected)
			throws BadLocationException {
		int offset = value.indexOf('|');
		value = value.substring(0, offset) + value.substring(offset + 1);

		TextDocument document = new TextDocument(value, fileURI != null ? fileURI : "test://test/test.xml");
		Position position = document.positionAt(offset);

		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		xmlLanguageService.doSave(new SettingsSaveContext(settings));

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		List<? extends Location> actual = xmlLanguageService.findReferences(xmlDocument, position,
				new ReferenceContext(), () -> {
				});
		assertLocation(actual, expected);

	}

	public static Location l(final String uri, final Range range) {
		return new Location(uri, range);
	}

	public static void assertLocation(List<? extends Location> actual, Location... expected) {
		Assert.assertEquals(expected.length, actual.size());
		Assert.assertArrayEquals(expected, actual.toArray());
	}

	// ------------------- CodeLens assert

	public static void testCodeLensFor(String xml, CodeLens... expected) throws BadLocationException {
		testCodeLensFor(xml, null, expected);
	}

	public static void testCodeLensFor(String value, String fileURI, CodeLens... expected) throws BadLocationException {

		TextDocument document = new TextDocument(value, fileURI != null ? fileURI : "test://test/test.xml");

		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		xmlLanguageService.doSave(new SettingsSaveContext(settings));

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		XMLCodeLensSettings codeLensSettings = new XMLCodeLensSettings();
		ExtendedCodeLensCapabilities codeLensCapabilities = new ExtendedCodeLensCapabilities(
				new CodeLensKindCapabilities(Arrays.asList(CodeLensKind.References)));
		codeLensSettings.setCodeLens(codeLensCapabilities);
		List<? extends CodeLens> actual = xmlLanguageService.getCodeLens(xmlDocument, codeLensSettings, () -> {
		});
		assertCodeLens(actual, expected);

	}

	public static CodeLens cl(Range range, String title, String command) {
		return new CodeLens(range, new Command(title, command), null);
	}

	public static void assertCodeLens(List<? extends CodeLens> actual, CodeLens... expected) {
		Assert.assertEquals(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(expected[i].getRange(), actual.get(i).getRange());
			Command expectedCommand = expected[i].getCommand();
			Command actualCommand = actual.get(i).getCommand();
			if (expectedCommand != null && actualCommand != null) {
				Assert.assertEquals(expectedCommand.getTitle(), actualCommand.getTitle());
				Assert.assertEquals(expectedCommand.getCommand(), actualCommand.getCommand());
			}
			Assert.assertEquals(expected[i].getData(), actual.get(i).getData());
		}
	}

	// ------------------- Highlights assert

	public static void testHighlightsFor(String xml, DocumentHighlight... expected) throws BadLocationException {
		testHighlightsFor(xml, null, expected);
	}

	public static void testHighlightsFor(String value, String fileURI, DocumentHighlight... expected)
			throws BadLocationException {
		int offset = value.indexOf('|');
		value = value.substring(0, offset) + value.substring(offset + 1);

		TextDocument document = new TextDocument(value, fileURI != null ? fileURI : "test://test/test.xml");
		Position position = document.positionAt(offset);

		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		ContentModelSettings settings = new ContentModelSettings();
		settings.setUseCache(false);
		xmlLanguageService.doSave(new SettingsSaveContext(settings));

		DOMDocument xmlDocument = DOMParser.getInstance().parse(document,
				xmlLanguageService.getResolverExtensionManager());
		xmlLanguageService.setDocumentProvider((uri) -> xmlDocument);

		List<? extends DocumentHighlight> actual = xmlLanguageService.findDocumentHighlights(xmlDocument, position,
				() -> {
				});
		assertDocumentHighlight(actual, expected);
	}

	public static void assertDocumentHighlight(List<? extends DocumentHighlight> actual,
			DocumentHighlight... expected) {
		Assert.assertEquals(expected.length, actual.size());
		Assert.assertArrayEquals(expected, actual.toArray());
	}

	public static DocumentHighlight hl(Range range) {
		return hl(range, DocumentHighlightKind.Read);
	}

	public static DocumentHighlight hl(Range range, DocumentHighlightKind kind) {
		return new DocumentHighlight(range, kind);
	}

	public static void assertHighlights(String value, int[] expectedMatches, String elementName)
			throws BadLocationException {
		int offset = value.indexOf("|");
		value = value.substring(0, offset) + value.substring(offset + 1);

		DOMDocument document = DOMParser.getInstance().parse(value, "test://test/test.html", null);

		Position position = document.positionAt(offset);

		XMLLanguageService languageService = new XMLLanguageService();
		List<DocumentHighlight> highlights = languageService.findDocumentHighlights(document, position);
		Assert.assertEquals(expectedMatches.length, highlights.size());
		for (int i = 0; i < highlights.size(); i++) {
			DocumentHighlight highlight = highlights.get(i);
			int actualStartOffset = document.offsetAt(highlight.getRange().getStart());
			Assert.assertEquals(expectedMatches[i], actualStartOffset);
			int actualEndOffset = document.offsetAt(highlight.getRange().getEnd());
			Assert.assertEquals(expectedMatches[i] + (elementName != null ? elementName.length() : 0), actualEndOffset);
			Assert.assertEquals(elementName,
					document.getText().substring(actualStartOffset, actualEndOffset).toLowerCase());
		}
	}

	// ------------------- Rename assert

	public static void assertRename(String value, String newText) throws BadLocationException {
		assertRename(value, newText, Collections.emptyList());
	}

	public static void assertRename(String value, String newText, List<TextEdit> expectedEdits)
			throws BadLocationException {
		int offset = value.indexOf("|");
		value = value.substring(0, offset) + value.substring(offset + 1);

		DOMDocument document = DOMParser.getInstance().parse(value, "test://test/test.html", null);

		Position position = document.positionAt(offset);

		XMLLanguageService languageService = new XMLLanguageService();
		WorkspaceEdit workspaceEdit = languageService.doRename(document, position, newText);
		List<TextEdit> actualEdits = workspaceEdit.getChanges().get("test://test/test.html");
		Assert.assertArrayEquals(expectedEdits.toArray(), actualEdits.toArray());
	}
}