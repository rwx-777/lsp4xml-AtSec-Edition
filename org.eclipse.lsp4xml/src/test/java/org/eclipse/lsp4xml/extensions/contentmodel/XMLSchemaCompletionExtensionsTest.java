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
package org.eclipse.lsp4xml.extensions.contentmodel;

import static org.eclipse.lsp4xml.XMLAssert.c;
import static org.eclipse.lsp4xml.XMLAssert.te;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.util.URI.MalformedURIException;
import org.eclipse.lsp4j.CompletionCapabilities;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemCapabilities;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4xml.XMLAssert;
import org.eclipse.lsp4xml.commons.BadLocationException;
import org.eclipse.lsp4xml.services.XMLLanguageService;
import org.eclipse.lsp4xml.settings.XMLCompletionSettings;
import org.junit.Test;

/**
 * XSD completion tests.
 *
 */
public class XMLSchemaCompletionExtensionsTest extends BaseFileTempTest {

	@Test
	public void completionInRoot() throws BadLocationException {
		// completion on <|
		String xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	<|" + //
				"</project>";
		testCompletionFor(xml, c("modelVersion", te(3, 1, 3, 2, "<modelVersion></modelVersion>"), "<modelVersion"), //
				c("parent", "<parent></parent>", "<parent"));
		// completion on <| >
		xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	<|   >" + //
				"</project>";
		testCompletionFor(xml, c("modelVersion", te(3, 1, 3, 6, "<modelVersion></modelVersion>"), "<modelVersion"), //
				c("parent", "<parent></parent>", "<parent"));
		// completion on |
		xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	|" + //
				"</project>";
		testCompletionFor(xml, c("modelVersion", te(3, 1, 3, 1, "<modelVersion></modelVersion>"), "modelVersion"), //
				c("parent", "<parent></parent>", "parent"));
		// completion on mod
		xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	mod|" + //
				"</project>";
		testCompletionFor(xml, c("modelVersion", te(3, 1, 3, 4, "<modelVersion></modelVersion>"), "modelVersion"), //
				c("parent", "<parent></parent>", "parent"));
	}

	@Test
	public void completionInRootWithCloseBracket() throws BadLocationException {
		String xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	<|  >" + // here last '<' is replaced with <modelVersion></modelVersion>
				"</project>";
		testCompletionFor(xml, c("modelVersion", te(3, 1, 3, 5, "<modelVersion></modelVersion>"), "<modelVersion"), //
				c("parent", "<parent></parent>", "<parent"));
	}

	@Test
	public void completionInChildElement() throws BadLocationException {
		String xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	<parent><|" + //
				"</project>";
		testCompletionFor(xml, c("groupId", "<groupId></groupId>", "<groupId"), //
				c("artifactId", "<artifactId></artifactId>", "<artifactId"), //
				c("version", "<version></version>", "<version"));
	}

	@Test
	public void completionInChildElementNoOpenBracket() throws BadLocationException {
		String xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	<parent>|" + //
				"</project>";
		testCompletionFor(xml, c("groupId", "<groupId></groupId>", "groupId"), //
				c("artifactId", "<artifactId></artifactId>", "artifactId"), //
				c("version", "<version></version>", "version"));
	}

	@Test
	public void completionInRootWithAndWithoutPrefixes() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\"\r\n" + //
				"       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"       xmlns:camel=\"http://camel.apache.org/schema/spring\"\r\n" + //
				"       xmlns:cxf=\"http://camel.apache.org/schema/cxf\"\r\n" + //
				"       xsi:schemaLocation=\"\r\n" + //
				"         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/cxf http://camel.apache.org/schema/cxf/camel-cxf.xsd\">\r\n" + //
				"\r\n" + //
				"  <| ";
		testCompletionFor(xml, c("bean", "<bean></bean>", "<bean"),
				c("camel:camelContext", "<camel:camelContext></camel:camelContext>", "<camel:camelContext"));
	}

	@Test
	public void completionInRootWithAndWithoutPrefixesNoOpenBracket() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\"\r\n" + //
				"       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"       xmlns:camel=\"http://camel.apache.org/schema/spring\"\r\n" + //
				"       xmlns:cxf=\"http://camel.apache.org/schema/cxf\"\r\n" + //
				"       xsi:schemaLocation=\"\r\n" + //
				"         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/cxf http://camel.apache.org/schema/cxf/camel-cxf.xsd\">\r\n" + //
				"\r\n" + //
				"  | ";
		testCompletionFor(xml, c("bean", "<bean></bean>", "bean"),
				c("camel:camelContext", "<camel:camelContext></camel:camelContext>", "camel:camelContext"));
	}

	@Test
	public void completionInRootWithOnlyPrefix() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\"\r\n" + //
				"       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"       xmlns:camel=\"http://camel.apache.org/schema/spring\"\r\n" + //
				"       xmlns:cxf=\"http://camel.apache.org/schema/cxf\"\r\n" + //
				"       xsi:schemaLocation=\"\r\n" + //
				"         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/cxf http://camel.apache.org/schema/cxf/camel-cxf.xsd\">\r\n" + //
				"\r\n" + //
				"  <camel:| ";
		testCompletionFor(xml, c("camel:camelContext", "<camel:camelContext></camel:camelContext>"));
	}

	@Test
	public void completionInChildElementWithPrefixes() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\"\r\n" + //
				"       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"       xmlns:camel=\"http://camel.apache.org/schema/spring\"\r\n" + //
				"       xmlns:cxf=\"http://camel.apache.org/schema/cxf\"\r\n" + //
				"       xsi:schemaLocation=\"\r\n" + //
				"         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd\r\n"
				+ //
				"         http://camel.apache.org/schema/cxf http://camel.apache.org/schema/cxf/camel-cxf.xsd\">\r\n" + //
				"\r\n" + //
				"  <camel:camelContext><| ";
		testCompletionFor(xml, c("camel:route", "<camel:route></camel:route>"),
				c("camel:template", "<camel:template />"));
	}

	@Test
	public void completionInChildElementWithElementWhichDeclareNS() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<root>\r\n" + //
				"  <camelContext id=\"camel\" xmlns=\"http://camel.apache.org/schema/spring\">\r\n" + "\r\n" + //
				"    <|";
		testCompletionFor(xml, c("route", "<route></route>"), c("template", "<template />"));
	}

	@Test
	public void noNamespaceSchemaLocationCompletion() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<Configuration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"xsd/Format.xsd\">\r\n"
				+ //
				"  <ViewDefinitions>\r\n" + //
				"    <View><|";
		// Completion only with Name
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/Format.xml", 4 + 2 /* CDATA and Comments */,
				c("Name", "<Name></Name>"), c("End with '</Configuration>'", "/Configuration>"),
				c("End with '</ViewDefinitions>'", "/ViewDefinitions>"), c("End with '</View>'", "/View>"));

		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<Configuration xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"xsd/Format.xsd\">\r\n"
				+ //
				"  <ViewDefinitions>\r\n" + //
				"    <View><Name /><|";
		// Completion only with Name
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/Format.xml", 5 + 2 /* CDATA and Comments */,
				c("OutOfBand", "<OutOfBand></OutOfBand>"), c("ViewSelectedBy", "<ViewSelectedBy></ViewSelectedBy>"),
				c("End with '</Configuration>'", "/Configuration>"),
				c("End with '</ViewDefinitions>'", "/ViewDefinitions>"), c("End with '</View>'", "/View>"));
	}

	@Test
	public void schemaLocationWithXSDFileSystemCompletion() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<invoice xmlns=\"http://invoice\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ " xsi:schemaLocation=\"http://invoice xsd/invoice-ns.xsd \">\r\n" + //
				"  <|";
		// Completion only for date
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/invoice.xml", 2 + 2 /* CDATA and Comments */,
				c("date", "<date></date>"), c("End with '</invoice>'", "</invoice>"));

		// Completion only for number
		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<invoice xmlns=\"http://invoice\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ " xsi:schemaLocation=\"http://invoice xsd/invoice-ns.xsd \">\r\n" + //
				"  <date></date>|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/invoice.xml", 2 + 2 /* CDATA and Comments */,
				c("number", "<number></number>"), c("End with '</invoice>'", "</invoice>"));
	}

	@Test
	public void completionOnAttributeName() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean |/>";
		testCompletionFor(xml, c("abstract", "abstract=\"false\""), c("autowire", "autowire=\"default\""),
				c("class", "class=\"\""));
	}

	@Test
	public void completionOnAttributeValue() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean autowire=\"|\"/>";
		testCompletionFor(xml, c("byName", "byName"), c("byType", "byType"), c("constructor", "constructor"));
	}

	@Test
	public void completionOnAttributeValue2() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<invoice xmlns=\"http://invoice\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ " xsi:schemaLocation=\"http://invoice xsd/invoice.xsd \">\r\n" + //
				"  <payments>\r\n" + //
				"    <payment method=\"|\"/>\r\n" + //
				"  </payments>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/invoice.xml", null, c("credit", "credit"),
				c("debit", "debit"), c("cash", "cash"));
	}

	@Test
	public void schemaLocationWithElementAndAttributeCompletion() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<invoice xmlns=\"http://simpleAttribute\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ " xsi:schemaLocation=\"http://simpleAttribute xsd/simpleAttribute.xsd \">\r\n" + //
				"  <pro|</invoice>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/simpleAttribute.xml", null,
				c("product", "<product description=\"\" />"));
	}

	@Test
	public void completionWithoutStartBracket() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	|";
		testCompletionFor(xml, c("bean", "<bean></bean>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	|" + "</beans>";
		testCompletionFor(xml, c("bean", "<bean></bean>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean />|" + "</beans>";
		testCompletionFor(xml, c("bean", "<bean></bean>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean />|";
		testCompletionFor(xml, c("bean", "<bean></bean>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean ></bean>|" + "</beans>";
		testCompletionFor(xml, c("bean", "<bean></bean>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean ></bean>|";
		testCompletionFor(xml, c("bean", "<bean></bean>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean>|</bean>";
		testCompletionFor(xml, c("constructor-arg", "<constructor-arg></constructor-arg>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + //
				"<beans xmlns=\"http://www.springframework.org/schema/beans\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ //
				"	<bean>\r\n   |      \r\n</bean>";
		testCompletionFor(xml, c("constructor-arg", "<constructor-arg></constructor-arg>"));

	}

	@Test
	public void completionWithXMLSchemaContentChanged() throws Exception {
		// This https://github.com/angelozerr/lsp4xml/issues/194 for the test scenario
		String xsdPath = tempDirUri.getPath() + "/resources.xsd";
		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
				+ "<resources | xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " //
				+ "xsi:noNamespaceSchemaLocation=\"" + xsdPath + "\">\r\n" + //
				"    <resource name=\"res00\" >\r\n" + //
				"        <property name=\"propA\" value=\"...\" />\r\n" + //
				"        <property name=\"propB\" value=\"...\" />\r\n" + //
				"    </resource>\r\n" + //
				"    <resource name=\"\" >\r\n" + //
				"        <property name=\"\" value=\"...\" />\r\n" + //
				"        <property name=\"\" value=\"...\" />\r\n" + //
				"    </resource> \r\n" + "</resources>";

		// Schema defines variant attribute -> completion for @variant
		String schema = "<?xml version=\"1.0\"?>\r\n" + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n"
				+ "\r\n" + "    <xs:complexType name=\"property\">\r\n"
				+ "        <xs:attribute name=\"name\" type=\"xs:string\" />\r\n"
				+ "        <xs:attribute name=\"value\" type=\"xs:string\" />\r\n" + "    </xs:complexType>\r\n"
				+ "\r\n" + "    <xs:complexType name=\"resource\">\r\n" + "        <xs:sequence>\r\n"
				+ "            <xs:element name=\"property\" type=\"property\" minOccurs=\"0\" maxOccurs=\"unbounded\" />\r\n"
				+ "        </xs:sequence>\r\n"
				+ "        <xs:attribute name=\"name\" type=\"xs:string\" use=\"required\" />\r\n"
				+ "    </xs:complexType>\r\n" + "\r\n" + "    <xs:element name=\"resources\">\r\n"
				+ "        <xs:complexType>\r\n" + "            <xs:sequence>\r\n"
				+ "                <xs:element name=\"resource\" type=\"resource\" minOccurs=\"0\" maxOccurs=\"unbounded\" />\r\n"
				+ "            </xs:sequence>\r\n"
				+ "            <xs:attribute name=\"variant\" type=\"xs:string\" use=\"required\"/>\r\n"
				+ "        </xs:complexType>\r\n" + "    </xs:element>\r\n" + "</xs:schema>";
		
		createFile(xsdPath, schema);
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/resources.xml", 5, false,
				c("variant", "variant=\"\""));

		// Update resources.xsd, Schema doesn't define variant attribute -> no
		// completion
		schema = "<?xml version=\"1.0\"?>\r\n" + "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n"
				+ "\r\n" + "    <xs:complexType name=\"property\">\r\n"
				+ "        <xs:attribute name=\"name\" type=\"xs:string\" />\r\n"
				+ "        <xs:attribute name=\"value\" type=\"xs:string\" />\r\n" + "    </xs:complexType>\r\n"
				+ "\r\n" + "    <xs:complexType name=\"resource\">\r\n" + "        <xs:sequence>\r\n"
				+ "            <xs:element name=\"property\" type=\"property\" minOccurs=\"0\" maxOccurs=\"unbounded\" />\r\n"
				+ "        </xs:sequence>\r\n"
				+ "        <xs:attribute name=\"name\" type=\"xs:string\" use=\"required\" />\r\n"
				+ "    </xs:complexType>\r\n" + "\r\n" + "    <xs:element name=\"resources\">\r\n"
				+ "        <xs:complexType>\r\n" + "            <xs:sequence>\r\n"
				+ "                <xs:element name=\"resource\" type=\"resource\" minOccurs=\"0\" maxOccurs=\"unbounded\" />\r\n"
				+ "            </xs:sequence>\r\n"
				// + " <xs:attribute name=\"variant\" type=\"xs:string\" use=\"required\"/>\r\n"
				+ "        </xs:complexType>\r\n" + "    </xs:element>\r\n" + "</xs:schema>";
		updateFile(xsdPath, schema);
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/resources.xml", 4, false);

	}

	/**
	 * @see https://github.com/angelozerr/lsp4xml/issues/214
	 * 
	 * @throws BadLocationException
	 * @throws MalformedURIException
	 */
	@Test
	public void issue214() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<edmx:Edmx xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Version=\"4.0\">\r\n"
				+ //
				"  <edmx:Reference Uri=\"https://oasis-tcs.github.io/odata-vocabularies/vocabularies/Org.OData.Core.V1.xml\">\r\n"
				+ //
				"    <edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\">\r\n" + //
				"      <Annotation Term=\"Core.DefaultNamespace\" />      \r\n" + //
				"    </edmx:Include>\r\n" + //
				" |";
		testCompletionFor(xml, c("Annotation", "<Annotation Term=\"\"></Annotation>"), //
				c("edmx:Include", "<edmx:Include Namespace=\"\"></edmx:Include>"), //
				c("edmx:IncludeAnnotations", "<edmx:IncludeAnnotations TermNamespace=\"\" />"));

		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
				+ "<edmx:Edmx xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Version=\"4.0\">   \r\n"
				+ "  <edmx:DataServices>\r\n" + //
				"    <Schema Namespace=\"ODataDemo\">\r\n" + //
				" |";
		testCompletionFor(xml, c("Action", "<Action Name=\"\"></Action>"), //
				c("Annotation", "<Annotation Term=\"\"></Annotation>"), //
				c("Annotations", "<Annotations Target=\"\"></Annotations>"), //
				c("ComplexType", "<ComplexType Name=\"\"></ComplexType>"));
	}

	@Test
	public void issue214WithMarkdown() throws BadLocationException, MalformedURIException {
		String edmxURI = getXMLSchemaFileURI("edmx.xsd");
		String edmURI = getXMLSchemaFileURI("edm.xsd");
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<edmx:Edmx xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Version=\"4.0\">\r\n"
				+ //
				"  <edmx:Reference Uri=\"https://oasis-tcs.github.io/odata-vocabularies/vocabularies/Org.OData.Core.V1.xml\">\r\n"
				+ //
				"    <edmx:Include Namespace=\"Org.OData.Core.V1\" Alias=\"Core\">\r\n" + //
				"      <Annotation Term=\"Core.DefaultNamespace\" />      \r\n" + //
				"    </edmx:Include>\r\n" + //
				" |";
		testCompletionMarkdownSupporytFor(xml,
				c("Annotation", te(6, 1, 6, 1, "<Annotation Term=\"\"></Annotation>"), "Annotation",
						"Source: [edm.xsd](" + edmURI + ")", MarkupKind.MARKDOWN), //
				c("edmx:Include", te(6, 1, 6, 1, "<edmx:Include Namespace=\"\"></edmx:Include>"), "edmx:Include",
						"Source: [edmx.xsd](" + edmxURI + ")", MarkupKind.MARKDOWN), //
				c("edmx:IncludeAnnotations", "<edmx:IncludeAnnotations TermNamespace=\"\" />"));

		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
				+ "<edmx:Edmx xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Version=\"4.0\">   \r\n"
				+ "  <edmx:DataServices>\r\n" + //
				"    <Schema Namespace=\"ODataDemo\">\r\n" + //
				" |";
		testCompletionMarkdownSupporytFor(xml, c("Action", "<Action Name=\"\"></Action>"), //
				c("Annotation", "<Annotation Term=\"\"></Annotation>"), //
				c("Annotations", "<Annotations Target=\"\"></Annotations>"), //
				c("ComplexType", "<ComplexType Name=\"\"></ComplexType>"));
	}

	/**
	 * @see https://github.com/angelozerr/lsp4xml/issues/311
	 * 
	 * @throws BadLocationException
	 */
	@Test
	public void issue311() throws BadLocationException {
		// with xmlns:edm="http://docs.oasis-open.org/odata/ns/edm"
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
				+ "<edmx:Edmx xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\" xmlns:edm=\"http://docs.oasis-open.org/odata/ns/edm\" Version=\"4.0\">\r\n"
				+ "  | \r\n" //
				+ "</edmx:Edmx>";
		XMLAssert.testCompletionFor(xml, "src/test/resources/catalogs/catalog.xml", "test.xsd",
				4 /*
					 * edmx:DataServices, <edmx:DataServices, #region, #endregion AND NOT
					 * edm:Annotation
					 */ + 2 /* CDATA and Comments */, c("edmx:DataServices", "<edmx:DataServices></edmx:DataServices>"), //
				c("edmx:Reference", "<edmx:Reference Uri=\"\"></edmx:Reference>"));

		// with xmlns="http://docs.oasis-open.org/odata/ns/edm"
		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
				+ "<edmx:Edmx xmlns:edmx=\"http://docs.oasis-open.org/odata/ns/edmx\" xmlns=\"http://docs.oasis-open.org/odata/ns/edm\" Version=\"4.0\">\r\n"
				+ "  | \r\n" //
				+ "</edmx:Edmx>";
		XMLAssert.testCompletionFor(xml, "src/test/resources/catalogs/catalog.xml", "test.xsd",
				4 /*
					 * edmx:DataServices, <edmx:DataServices, #region, #endregion AND NOT
					 * edm:Annotation
					 */ + 2 /* CDATA and Comments */, c("edmx:DataServices", "<edmx:DataServices></edmx:DataServices>"), //
				c("edmx:Reference", "<edmx:Reference Uri=\"\"></edmx:Reference>"));
	}

	@Test
	public void xsiCompletionTestAllItems() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n"
				+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + "    |>\r\n" + "</project>";
		XMLAssert.testCompletionFor(xml, 4, c("xsi:nil", "xsi:nil=\"true\""), c("xsi:type", "xsi:type=\"\""),
				c("xsi:noNamespaceSchemaLocation", "xsi:noNamespaceSchemaLocation=\"\""),
				c("xsi:schemaLocation", "xsi:schemaLocation=\"\""));
	}

	@Test
	public void xsiCompletionNonRootElement() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n"
				+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ "  <modelVersion xs|></modelVersion>\r\n" + "</project>";

		XMLAssert.testCompletionFor(xml, 2, c("xsi:nil", "xsi:nil=\"true\""), c("xsi:type", "xsi:type=\"\""));
	}

	@Test
	public void xsiCompletionNonRootElement2() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n"
				+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ "  <modelVersion xsi:nil=\"\" |></modelVersion>\r\n" + "</project>";

		XMLAssert.testCompletionFor(xml, 1, c("xsi:type", "xsi:type=\"\""));
	}

	@Test
	public void xsiCompletionNotUsingXSIName() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n"
				+ "    xmlns:XXY=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "    XXY:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" |>\r\n"
				+ "  <modelVersion></modelVersion>\r\n" + "</project>";

		XMLAssert.testCompletionFor(xml, 4, c("XXY:nil", "XXY:nil=\"true\""), c("XXY:type", "XXY:type=\"\""),
				c("XXY:noNamespaceSchemaLocation", "XXY:noNamespaceSchemaLocation=\"\""));
	}

	@Test
	public void xmlnsXSICompletion() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + "    xsi:|>\r\n"
				+ "  <modelVersion></modelVersion>\r\n" + "</project>";

		XMLAssert.testCompletionFor(xml, 1, c("xmlns:xsi", "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""));
	}

	@Test
	public void xmlnsXSIValueCompletion() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + "    xmlns:xsi=|>\r\n"
				+ "  <modelVersion></modelVersion>\r\n" + "</project>";

		XMLAssert.testCompletionFor(xml, 1,
				c("http://www.w3.org/2001/XMLSchema-instance", "\"http://www.w3.org/2001/XMLSchema-instance\""));
	}

	@Test
	public void xsiCompletionSchemaLocationExists() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n"
				+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" |>\r\n"
				+ "  <modelVersion></modelVersion>\r\n" + "</project>";

		XMLAssert.testCompletionFor(xml, 4, c("xsi:nil", "xsi:nil=\"true\""), c("xsi:type", "xsi:type=\"\""),
				c("xsi:noNamespaceSchemaLocation", "xsi:noNamespaceSchemaLocation=\"\""));
	}

	@Test
	public void xsiCompletionNoNamespaceSchemaLocationExists() throws BadLocationException {
		String xml = "<project\r\n" + "    xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n"
				+ "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "    xsi:noNamespaceSchemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" |>\r\n"
				+ // <- completion
				"  <modelVersion></modelVersion>\r\n" + "</project>";

		XMLAssert.testCompletionFor(xml, 3, c("xsi:nil", "xsi:nil=\"true\""), c("xsi:type", "xsi:type=\"\""),
				c("xsi:schemaLocation", "xsi:schemaLocation=\"\""));
	}

	@Test
	public void choice() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<person xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"xsd/choice.xsd\">\r\n"
				+ //
				" <|";
		// Completion only member or employee
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/choice.xml", null, c("member", "<member></member>"),
				c("employee", "<employee></employee>"));

		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<person xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"xsd/choice.xsd\">\r\n"
				+ //
				" <employee /> | ";
		// Completion only member or employee
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/choice.xml", null, c("member", "<member></member>"),
				c("employee", "<employee></employee>"));

		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<person xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"xsd/choice.xsd\">\r\n"
				+ //
				" <employee />\r\n" + //
				" <employee /> <| " + //
				"</person>";
		// Completion only member or employee
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/choice.xml", 2 + 2 /* CDATA and Comments */,
				c("employee", "<employee></employee>"));

		xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + //
				"<person xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"xsd/choice.xsd\">\r\n"
				+ //
				" <employee />\r\n" + //
				" <employee />\r\n" + //
				" <employee /> <| " + //
				"</person>";
		// maxOccurs = 3, completion should be empty
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/choice.xml", 2 /* CDATA and Comments */);
	}

	@Test
	public void sequence() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/sequence.xsd\">\r\n" + //
				"	|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", null, c("e1", "<e1></e1>"),
				c("optional0", "<optional0></optional0>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/sequence.xsd\">\r\n" + //
				"	<e1></e1> | ";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", null, c("e2", "<e2></e2>"),
				c("optional1", "<optional1></optional1>"), c("optional11", "<optional11></optional11>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/sequence.xsd\">\r\n" + //
				"	<e1></e1>|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", null, c("e2", "<e2></e2>"),
				c("optional1", "<optional1></optional1>"), c("optional11", "<optional11></optional11>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/sequence.xsd\">\r\n" + //
				"	<e1></e1><e2></e2>|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", null, c("e3", "<e3></e3>"),
				c("optional2", "<optional2></optional2>"), c("optional22", "<optional22></optional22>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/sequence.xsd\">\r\n" + //
				"	<e1></e1><e2></e2><e3 />|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", null,
				c("optional3", "<optional3></optional3>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/sequence.xsd\">\r\n" + //
				"	<e1></e1><e2></e2><e3 /><optional3></optional3>|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", null,
				c("optional3", "<optional3></optional3>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/sequence.xsd\">\r\n" + //
				"	<e1></e1><e2></e2><e3 /><optional3></optional3><optional3></optional3>|";
		// optional3 is not return by completion since optional3 has a max=2 occurences
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 1 + 2 /* CDATA and Comments */,
				c("End with '</data>'", "</data>"));
	}

	@Test
	public void xsAny() throws IOException, BadLocationException {
		Path dir = Paths.get("target/xsd/");
		if (!Files.isDirectory(dir)) {
			Files.createDirectory(dir);
		}
		Files.deleteIfExists(Paths.get(dir.toString(), "any.xsd"));
		XMLLanguageService xmlLanguageService = new XMLLanguageService();

		// Test completion with xs:any processContents="strict"
		String schema = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	targetNamespace=\"http://ui\">\r\n" + //
				"	<xs:element name=\"textbox\"></xs:element>\r\n" + //
				"	<xs:element name=\"page\">\r\n" + //
				"		<xs:complexType>\r\n" + //
				"			<xs:sequence>				\r\n" + //
				"				<xs:element name=\"title\" />\r\n" + //
				"				<xs:any processContents=\"strict\" />\r\n" + // <-- xs:any processContents="strict"
				"			</xs:sequence>			\r\n" + //
				"		</xs:complexType>\r\n" + //
				"	</xs:element>\r\n" + //
				"</xs:schema>";
		Files.write(Paths.get("target/xsd/any.xsd"), schema.getBytes());

		String xml = "<ui:page xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ui=\"http://ui\" xsi:schemaLocation=\"http://ui xsd/any.xsd\" >\r\n"
				+ //
				"	|	\r\n" + //
				"	<a/>" + //
				"</ui:page>";
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/any.xml", 4 + 1, true,
				c("title", "<title></title>"));

		// xs:any completion with strict -> only XML Schema global element declaration
		// available in completion
		xml = "<ui:page xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ui=\"http://ui\" xsi:schemaLocation=\"http://ui xsd/any.xsd\" >\r\n"
				+ //
				"	<title></title>\r\n" + //
				"	|	\r\n" + //
				"	<a/>" + //
				"</ui:page>";
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/any.xml", 4 + 2, true,
				c("ui:page", "<ui:page></ui:page>"), c("ui:textbox", "<ui:textbox></ui:textbox>"));

		// no completion
		xml = "<ui:page xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ui=\"http://ui\" xsi:schemaLocation=\"http://ui xsd/any.xsd\" >\r\n"
				+ //
				"	<title></title>\r\n" + //
				"	<ui:textbox></ui:textbox>\r\n" + //
				"	|	\r\n" + //
				"	<a/>" + //
				"</ui:page>";
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/any.xml", 4, true);

		// Test completion with xs:any processContents="lax" (or processContents="skip")
		schema = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\r\n" + //
				"	targetNamespace=\"http://ui\">\r\n" + //
				"	<xs:element name=\"textbox\"></xs:element>\r\n" + //
				"	<xs:element name=\"page\">\r\n" + //
				"		<xs:complexType>\r\n" + //
				"			<xs:sequence>				\r\n" + //
				"				<xs:element name=\"title\" />\r\n" + //
				"				<xs:any processContents=\"lax\" />\r\n" + // <-- xs:any processContents="lax"
				"			</xs:sequence>			\r\n" + //
				"		</xs:complexType>\r\n" + //
				"	</xs:element>\r\n" + //
				"</xs:schema>";

		Files.write(Paths.get("target/xsd/any.xsd"), schema.getBytes());

		xml = "<ui:page xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ui=\"http://ui\" xsi:schemaLocation=\"http://ui xsd/any.xsd\" >\r\n"
				+ //
				"	|	\r\n" + //
				"	<a/>" + //
				"</ui:page>";
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/any.xml", 4 + 1, true,
				c("title", "<title></title>"));

		// xs:any completion with strict -> all XML Schema element declaration
		// available in completion + tags completion
		xml = "<ui:page xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ui=\"http://ui\" xsi:schemaLocation=\"http://ui xsd/any.xsd\" >\r\n"
				+ //
				"	<title></title>\r\n" + //
				"	|	\r\n" + //
				"	<a/>" + //
				"</ui:page>";
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/any.xml", 4 + 4, true,
				c("title", "<title></title>"), c("a", "<a/>"), c("ui:page", "<ui:page></ui:page>"),
				c("ui:textbox", "<ui:textbox></ui:textbox>"));

		// no completion
		xml = "<ui:page xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ui=\"http://ui\" xsi:schemaLocation=\"http://ui xsd/any.xsd\" >\r\n"
				+ //
				"	<title></title>\r\n" + //
				"	<ui:textbox></ui:textbox>\r\n" + //
				"	|	\r\n" + //
				"	<a/>" + //
				"</ui:page>";
		XMLAssert.testCompletionFor(xmlLanguageService, xml, null, null, "target/any.xml", 4, true);
	}

	@Test
	public void xsAnyDuplicate() throws IOException, BadLocationException {
		String xml = "<Page loaded=\"pageLoaded\" class=\"page\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"xsd/tns.xsd\" >\r\n"
				+ //
				"\r\n" + //
				" | ";
		// testCompletionFor checks the duplicate label
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/tns.xml", null,
				c("Page", te(2, 1, 2, 1, "<Page></Page>"), "Page", "Source: tns.xsd", MarkupKind.PLAINTEXT),
				c("AbsoluteLayout", te(2, 1, 2, 1, "<AbsoluteLayout></AbsoluteLayout>"), "AbsoluteLayout",
						"Source: tns.xsd", MarkupKind.PLAINTEXT),
				c("DockLayout", te(2, 1, 2, 1, "<DockLayout></DockLayout>"), "DockLayout", "Source: tns.xsd",
						MarkupKind.PLAINTEXT));
	}
	
	@Test
	public void substitutionGroup() throws BadLocationException {
		String xml = "<fleet xmlns=\"http://example/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://example/ xsd/substitutionGroup.xsd\">\r\n"
				+ "	   | ";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/substitutionGroup.xml", null,
				c("truck", "<truck />"), //
				c("automobile", "<automobile />"));
	}

	@Test
	public void tag() throws BadLocationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 4 + 2 /* CDATA and Comments */,
				c("tag", "<tag></tag>"), c("End with '</root>'", "</root>"), c("#region", "<!-- #region -->"),
				c("#endregion", "<!-- #endregion-->"), c("cdata", "<![CDATA[ ]]>"), //
				c("comment", "<!-- -->"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	|\r\n" + //
				"</root>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 3 + 2 /* CDATA and Comments */,
				c("tag", "<tag></tag>"), c("#region", "<!-- #region -->"), c("#endregion", "<!-- #endregion-->"),
				c("cdata", "<![CDATA[ ]]>"), c("comment", "<!-- -->"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 2 + 2 /* CDATA and Comments */,
				c("tag", "<tag></tag>"), c("End with '</root>'", "</root>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<|\r\n" + //
				"</root>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 1 + 2 /* CDATA and Comments */,
				c("tag", "<tag></tag>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 2 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"), c("End with '</root>'", "</root>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />|\r\n" + "</root>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 1 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag /><|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 2 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"), c("End with '</root>'", "/root>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag /><|\r\n" + "</root>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 1 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 4 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"), c("End with '</root>'", "</root>"),
				c("#region", "<!-- #region -->"), c("#endregion", "<!-- #endregion-->"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"|r\n" + "</root>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 3 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"), c("#region", "<!-- #region -->"),
				c("#endregion", "<!-- #endregion-->"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"<|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 2 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"), c("End with '</root>'", "/root>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"<|\r\n" + "</root>";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 1 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"	<optional />\r\n" + //
				"|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 4 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"), c("End with '</root>'", "</root>"),
				c("#region", "<!-- #region -->"), c("#endregion", "<!-- #endregion-->"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"	<optional />\r\n" + //
				"<|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 2 + 2 /* CDATA and Comments */,
				c("optional", "<optional></optional>"), c("End with '</root>'", "/root>"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"	<optional />\r\n" + //
				"	<optional />\r\n" + //
				"|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 3 + 2 /* CDATA and Comments */,
				c("End with '</root>'", "</root>"), c("#region", "<!-- #region -->"),
				c("#endregion", "<!-- #endregion-->"));

		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" + //
				"<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:noNamespaceSchemaLocation=\"xsd/tag.xsd\">\r\n" + //
				"	<tag />\r\n" + //
				"	<optional />\r\n" + //
				"	<optional />\r\n" + //
				"<|";
		XMLAssert.testCompletionFor(xml, null, "src/test/resources/sequence.xml", 1 + 2 /* CDATA and Comments */,
				c("End with '</root>'", "/root>"));

	}

	@Test
	public void documentationAsPlainText() throws BadLocationException {
		String xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	<|" + //
				"</project>";
		testCompletionFor(xml, c("groupId", te(3, 1, 3, 2, "<groupId></groupId>"), "<groupId",
				"A universally unique identifier for a project. It is normal to use a fully-qualified package name to distinguish it from other projects with a similar name "
						+ "(eg. <code>org.apache.maven</code>)." + //
						System.lineSeparator() + //
						System.lineSeparator() + //
						"Source: maven-4.0.0.xsd",
				MarkupKind.PLAINTEXT));

	}

	@Test
	public void documentationAsMarkdown() throws BadLocationException, MalformedURIException {
		String xml = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\r\n" + //
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n" + //
				"	xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\r\n"
				+ //
				"	<|" + //
				"</project>";

		String mavenFileURI = getXMLSchemaFileURI("maven-4.0.0.xsd");
		testCompletionMarkdownSupporytFor(xml, c("groupId", te(3, 1, 3, 2, "<groupId></groupId>"), "<groupId",
				"A universally unique identifier for a project. It is normal to use a fully-qualified package name to distinguish it from other projects with a similar name "
						+ "(eg. `org.apache.maven`)." + //
						System.lineSeparator() + //
						System.lineSeparator() + //
						"Source: [maven-4.0.0.xsd](" + mavenFileURI + ")",
				MarkupKind.MARKDOWN));
	}

	private static String getXMLSchemaFileURI(String schemaURI) throws MalformedURIException {
		return XMLEntityManager.expandSystemId("xsd/" + schemaURI, "src/test/resources/test.xml", true).replace("///",
				"/");
	}

	private void testCompletionFor(String xml, CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(xml, "src/test/resources/catalogs/catalog.xml", expectedItems);
	}

	private void testCompletionMarkdownSupporytFor(String xml, CompletionItem... expectedItems)
			throws BadLocationException {
		XMLCompletionSettings completionSettings = new XMLCompletionSettings();
		CompletionCapabilities completionCapabilities = new CompletionCapabilities();
		CompletionItemCapabilities completionItem = new CompletionItemCapabilities(false);
		completionItem.setDocumentationFormat(Arrays.asList(MarkupKind.MARKDOWN));
		completionCapabilities.setCompletionItem(completionItem);
		completionSettings.setCapabilities(completionCapabilities);
		XMLAssert.testCompletionFor(new XMLLanguageService(), xml, "src/test/resources/catalogs/catalog.xml", null,
				null, null, completionSettings, expectedItems);
	}
}
