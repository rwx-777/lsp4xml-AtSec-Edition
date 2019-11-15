package org.eclipse.lsp4xml.extensions.xsd;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.lsp4xml.dom.DOMAttr;
import org.eclipse.lsp4xml.dom.DOMElement;
import org.eclipse.lsp4xml.extensions.xsd.utils.XSDUtils;
import org.eclipse.lsp4xml.utils.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DataType {

	private static String lineSeparator = System.lineSeparator();

	private static final Map<String, DataType> dataTypes;

	static {
		dataTypes = loadDataTypes();
	}

	public static DataType getDataType(String name) {
		return dataTypes.get(name);
	}

	public static Collection<DataType> getDataTypes() {
		return dataTypes.values();
	}

	private final String name;

	private final String url;

	private String documentation;

	public DataType(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getDocumentation() {
		if (documentation == null) {
			documentation = createDocumentation();
		}
		return documentation;
	}

	private String createDocumentation() {
		StringBuilder doc = new StringBuilder();
		doc.append("**");
		doc.append(getName());
		doc.append("**");
		if (!StringUtils.isEmpty(url)) {
			doc.append(lineSeparator);
			doc.append("See [documentation](");
			doc.append(getUrl());
			doc.append(") for more informations.");
		}
		return doc.toString();
	}

	public static String getDocumentation(DOMAttr attr) {
		StringBuilder doc = new StringBuilder();
		doc.append("**");
		doc.append(attr.getValue());
		doc.append("**");
		DOMElement element = attr.getOwnerElement();
		if (XSDUtils.isXSComplexType(element)) {
			doc.append(lineSeparator);
			doc.append(" - Type: `Complex Type` ");
		} else if (XSDUtils.isXSSimpleType(element)) {
			doc.append(lineSeparator);
			doc.append(" - Type: `Simple Type` ");
		}
		return doc.toString();
	}

	private static Map<String, DataType> loadDataTypes() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DataTypeHandler handler = new DataTypeHandler();
			saxParser.parse(new InputSource(DataType.class.getResourceAsStream("/schemas/xsd/datatypes.xml")), handler);
			return handler.getDataTypes();
		} catch (Exception e) {
			return null;
		}
	}

	private static class DataTypeHandler extends DefaultHandler {

		private final Map<String, DataType> dataTypes;

		public DataTypeHandler() {
			dataTypes = new HashMap<>();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if ("datatype".contentEquals(qName)) {
				DataType dataType = new DataType(attributes.getValue("name"), attributes.getValue("url"));
				dataTypes.put(dataType.getName(), dataType);
			}
			super.startElement(uri, localName, qName, attributes);
		}

		public Map<String, DataType> getDataTypes() {
			return dataTypes;
		}

	}

}
