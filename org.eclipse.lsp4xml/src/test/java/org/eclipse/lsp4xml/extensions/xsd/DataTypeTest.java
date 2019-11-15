/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4xml.extensions.xsd;

import org.junit.Assert;
import org.junit.Test;

/**
 * XSD Data type tests
 * 
 * @author Angelo ZERR
 *
 */
public class DataTypeTest {

	@Test
	public void xsString() {
		DataType string = DataType.getDataType("string");
		Assert.assertNotNull(string);
		Assert.assertEquals(string.getName(), "string");
	}
}
