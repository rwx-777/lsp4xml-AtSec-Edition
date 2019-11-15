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
package org.eclipse.lsp4xml.extensions.prolog;

import static org.eclipse.lsp4xml.XMLAssert.c;
import static org.eclipse.lsp4xml.XMLAssert.te;

import org.eclipse.lsp4j.CompletionCapabilities;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemCapabilities;
import org.eclipse.lsp4xml.XMLAssert;
import org.eclipse.lsp4xml.commons.BadLocationException;
import org.eclipse.lsp4xml.services.XMLLanguageService;
import org.eclipse.lsp4xml.settings.XMLCompletionSettings;
import org.eclipse.lsp4xml.settings.XMLFormattingOptions;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.eclipse.lsp4xml.XMLAssert.r;

/**
 * XSL completion tests which test the {@link XSLURIResolverExtension}.
 *
 */
public class PrologCompletionExtensionsTest {

	public static XMLFormattingOptions formattingSettingsSingleQuotes = new XMLFormattingOptions(true);
	public static XMLFormattingOptions formattingSettings = new XMLFormattingOptions(true);

	@BeforeClass
	public static void runOnceBeforeClass() {
		formattingSettingsSingleQuotes.setQuotations(XMLFormattingOptions.SINGLE_QUOTES_VALUE);
	}

	@Test
	public void completionVersionWithV() throws BadLocationException {
		// completion on |
		String xml = "<?xml v|?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, 1, c("version", te(0, 6, 0, 7, "version=\"1.0\""), "version"));
	}

	@Test
	public void completionVersion() throws BadLocationException {
		// completion on |
		String xml = "<?xml |?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, c("version", te(0, 6, 0, 6, "version=\"1.0\""), "version"));
	}

	@Test
	public void completionEncodingAndStandalone() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=\"1.0\" |?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, 2, 
			c("encoding", te(0, 20, 0, 20, "encoding=\"UTF-8\""), "encoding"), 
			c("standalone", te(0, 20, 0, 20, "standalone=\"yes\""), "standalone"));
	}

	@Test
	public void completionStandalone() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" |?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, 1, c("standalone", te(0, 37, 0, 37, "standalone=\"yes\""), "standalone"));
	}

	@Test
	public void noCompletionsAfterStandalone() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=\"1.0\" standalone=\"yes\" |?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, 0, (CompletionItem []) null);
	}

	@Test
	public void completionEncodingBeforeStandalone() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=\"1.0\" | standalone=\"yes\" ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, 1, c("encoding", te(0, 20, 0, 20, "encoding=\"UTF-8\""), "encoding"));
	}

	@Test
	public void completionVersionValue() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=| ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml,
				c(PrologModel.VERSION_1, te(0, 14, 0, 14, "\"" + PrologModel.VERSION_1 + "\""),
						"\"" + PrologModel.VERSION_1 + "\""),
				c(PrologModel.VERSION_1_1, te(0, 14, 0, 14, "\"" + PrologModel.VERSION_1_1 + "\""),
						"\"" + PrologModel.VERSION_1_1 + "\""));
	}

	@Test
	public void completionVersionNoSpaceAfterEquals() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=|?>\r\n" + // <- no space after the '='
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml,
				c(PrologModel.VERSION_1, te(0, 14, 0, 14, "\"" + PrologModel.VERSION_1 + "\""),
						"\"" + PrologModel.VERSION_1 + "\""),
				c(PrologModel.VERSION_1_1, te(0, 14, 0, 14, "\"" + PrologModel.VERSION_1_1 + "\""),
						"\"" + PrologModel.VERSION_1_1 + "\""));
	}

	@Test
	public void completionEncodingValue() throws BadLocationException {
		// completion on |
		String xml = "<?xml encoding=| ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml,
				c(PrologModel.UTF_8, te(0, 15, 0, 15, "\"" + PrologModel.UTF_8 + "\""),
						"\"" + PrologModel.UTF_8 + "\""),
				c(PrologModel.EUC_KR, te(0, 15, 0, 15, "\"" + PrologModel.EUC_KR + "\""),
						"\"" + PrologModel.EUC_KR + "\""));
	}

	@Test
	public void completionStandaloneValue() throws BadLocationException {
		// completion on |
		String xml = "<?xml standalone=| ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml,
				c(PrologModel.YES, te(0, 17, 0, 17, "\"" + PrologModel.YES + "\""), "\"" + PrologModel.YES + "\""),
				c(PrologModel.NO, te(0, 17, 0, 17, "\"" + PrologModel.NO + "\""), "\"" + PrologModel.NO + "\""));
	}

	@Test
	public void completionVersionExists() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=\"1.0\" version=| ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, c(PrologModel.VERSION_1_1, te(0, 28, 0, 28, "\"" + PrologModel.VERSION_1_1 + "\""),
				"\"" + PrologModel.VERSION_1_1 + "\""));
	}

	@Test
	public void completionEncodingExists() throws BadLocationException {
		// completion on |
		String xml = "<?xml encoding=\"UTF-8\" encoding=\"Win|\" ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml,
				c(PrologModel.WINDOWS_1251, te(0, 33, 0, 36, PrologModel.WINDOWS_1251), PrologModel.WINDOWS_1251),
				c(PrologModel.WINDOWS_1252, te(0, 33, 0, 36, PrologModel.WINDOWS_1252), PrologModel.WINDOWS_1252));
	}

	@Test
	public void completionVersionSingleQuotes() throws BadLocationException {
		// completion on |
		String xml = "<?xml version=| ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, formattingSettingsSingleQuotes, createCompletionSettings(true, true),
				c(PrologModel.VERSION_1, te(0, 14, 0, 14, "\'" + PrologModel.VERSION_1 + "\'"),
						"\'" + PrologModel.VERSION_1 + "\'"),
				c(PrologModel.VERSION_1_1, te(0, 14, 0, 14, "\'" + PrologModel.VERSION_1_1 + "\'"),
						"\'" + PrologModel.VERSION_1_1 + "\'"));
	}

	@Test
	public void completionEncodingSingleQuotes() throws BadLocationException {
		// completion on |
		String xml = "<?xml encoding=| ?>\r\n" + //
				"<project xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
		testCompletionFor(xml, formattingSettingsSingleQuotes, createCompletionSettings(true, true),
				c(PrologModel.UTF_8, te(0, 15, 0, 15, "\'" + PrologModel.UTF_8 + "\'"),
						"\'" + PrologModel.UTF_8 + "\'"),
				c(PrologModel.SHIFT_JIS, te(0, 15, 0, 15, "\'" + PrologModel.SHIFT_JIS + "\'"),
						"\'" + PrologModel.SHIFT_JIS + "\'"));
	}

	@Test
	public void testAutoCompletionPrologWithXML() throws BadLocationException {
		// With 'xml' label
		testCompletionFor("<?xml|", false, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 5), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xml|>", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 6), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xml|?>", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 7), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}

	@Test
	public void testAutoCompletionPrologWithoutXML() throws BadLocationException {
		// No 'xml' label
		testCompletionFor("<?|", false, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 2), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?|", false, false, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>",
				r(0, 2, 0, 2), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?|>", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 3), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?|?>", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 4), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}

	@Test
	public void testAutoCompletionPrologWithPartialXML() throws BadLocationException {
		testCompletionFor("<?x|", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 3), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xm|", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 4), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?x|", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 3), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xm|?>", true, true, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0",
				r(0, 2, 0, 6), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xm|?>", true, false, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>",
				r(0, 2, 0, 6), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}

	@Test
	public void testAutoCompletionPrologDTDFileWithXML() throws BadLocationException {
		// With 'xml' label
		String dtdFileURI = "test://test/test.dtd";
		testCompletionFor("<?xml|", dtdFileURI, false, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 5),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xml|>", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 6),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xml|?>", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 7),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}

	@Test
	public void testAutoCompletionPrologDTDFileWithoutXML() throws BadLocationException {
		// No 'xml' label
		String dtdFileURI = "test://test/test.dtd";
		testCompletionFor("<?|", dtdFileURI, false, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 2),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?|", dtdFileURI, false, false, c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>",
				r(0, 2, 0, 2), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?|>", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 3),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?|?>", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 4),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}

	@Test
	public void testAutoCompletionPrologDTFFileWithPartialXML() throws BadLocationException {
		String dtdFileURI = "test://test/test.dtd";
		testCompletionFor("<?x|", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 3),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xm|", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 4),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?x|", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 3),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xm|?>", dtdFileURI, true, true,
				c("<?xml ... ?>", "xml version=\"1.0\" encoding=\"UTF-8\"?>$0", r(0, 2, 0, 6),
						"xml version=\"1.0\" encoding=\"UTF-8\"?>"));
		testCompletionFor("<?xm|?>", dtdFileURI, true, false, c("<?xml ... ?>",
				"xml version=\"1.0\" encoding=\"UTF-8\"?>", r(0, 2, 0, 6), "xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}

	private void testCompletionFor(String xml, CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(xml, null, expectedItems);
	}

	private void testCompletionFor(String xml, int expectedCount, CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(xml, expectedCount, expectedItems);
	}

	private void testCompletionFor(String xml, String fileURI, boolean autoCloseTags, boolean isSnippetsSupported,
			CompletionItem... expectedItems) throws BadLocationException {
		testCompletionFor(xml, fileURI, formattingSettings,
				createCompletionSettings(autoCloseTags, isSnippetsSupported), expectedItems);
	}

	private void testCompletionFor(String xml, String fileURI, XMLFormattingOptions formattingSettings,
			XMLCompletionSettings completionSettings, CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(new XMLLanguageService(), xml, null, null, fileURI, null, completionSettings,
				formattingSettings, expectedItems);
	}

	private void testCompletionFor(String xml, boolean autoCloseTags, boolean isSnippetsSupported,
			CompletionItem... expectedItems) throws BadLocationException {
		testCompletionFor(xml, formattingSettings, createCompletionSettings(autoCloseTags, isSnippetsSupported),
				expectedItems);
	}

	private void testCompletionFor(String xml, XMLFormattingOptions formattingSettings,
			XMLCompletionSettings completionSettings, CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(new XMLLanguageService(), xml, null, null, null, null, completionSettings,
				formattingSettings, expectedItems);
	}

	private XMLCompletionSettings createCompletionSettings(boolean autoCloseTags, boolean isSnippetsSupported) {
		XMLCompletionSettings completionSettings = new XMLCompletionSettings(autoCloseTags);
		CompletionCapabilities capabilities = new CompletionCapabilities();
		CompletionItemCapabilities itemCapabilities = new CompletionItemCapabilities(isSnippetsSupported);
		capabilities.setCompletionItem(itemCapabilities);
		completionSettings.setCapabilities(capabilities);
		return completionSettings;
	}
}
