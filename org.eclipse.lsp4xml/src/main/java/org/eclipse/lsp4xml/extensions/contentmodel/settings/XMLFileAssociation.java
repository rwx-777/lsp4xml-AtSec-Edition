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
package org.eclipse.lsp4xml.extensions.contentmodel.settings;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.lsp4xml.settings.PathPatternMatcher;
import org.eclipse.lsp4xml.uriresolver.IExternalSchemaLocationProvider;

/**
 * XML file association between a XML file pattern (glob) and an XML Schema file
 * (systemId).
 **/
public class XMLFileAssociation extends PathPatternMatcher {

	private transient Map<String, String> externalSchemaLocation;
	private String systemId;

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
		this.externalSchemaLocation = null;
	}

	public Map<String, String> getExternalSchemaLocation() {
		if (externalSchemaLocation == null) {
			this.externalSchemaLocation = new HashMap<String, String>();
			this.externalSchemaLocation.put(IExternalSchemaLocationProvider.NO_NAMESPACE_SCHEMA_LOCATION, systemId);
		}
		return externalSchemaLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getPattern() == null) ? 0 : getPattern().hashCode());
		result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLFileAssociation other = (XMLFileAssociation) obj;
		String thisPattern = getPattern();
		String otherPattern = other.getPattern();
		if (thisPattern == null) {
			if (otherPattern != null)
				return false;
		} else if (!thisPattern.equals(otherPattern))
			return false;
		if (systemId == null) {
			if (other.systemId != null)
				return false;
		} else if (!systemId.equals(other.systemId))
			return false;
		return true;
	}
}
