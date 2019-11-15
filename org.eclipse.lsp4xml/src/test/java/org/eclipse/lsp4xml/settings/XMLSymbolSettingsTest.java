/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4xml.settings;

import static org.eclipse.lsp4xml.utils.OSUtils.isWindows;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * XMLSymbolsSettingsTest
 */
public class XMLSymbolSettingsTest {

	@Test
	public void isExcludedTest() {
		XMLSymbolSettings symbolSettings = new XMLSymbolSettings();
		symbolSettings.setExcluded(new String[] {"**/*.xsd", "**/*.xml"});
		assertTrue(symbolSettings.isExcluded("file:///nikolas/komonen/test.xml"));
		assertTrue(symbolSettings.isExcluded("file:///C:/Users/Nikolas/test.xsd"));
		assertFalse(symbolSettings.isExcluded("file:///nikolas/komonen/test.java"));
	}
}