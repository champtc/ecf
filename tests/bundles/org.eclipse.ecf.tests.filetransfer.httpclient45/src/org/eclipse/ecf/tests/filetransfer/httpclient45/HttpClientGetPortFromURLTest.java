/****************************************************************************
 * Copyright (c) 2012, 2020 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/

package org.eclipse.ecf.tests.filetransfer.httpclient45;

import org.eclipse.ecf.provider.filetransfer.httpclient45.HttpClientRetrieveFileTransfer;

import junit.framework.TestCase;

public class HttpClientGetPortFromURLTest extends TestCase {

	public static class HttpClientRetrieveTest extends HttpClientRetrieveFileTransfer {
		
		public HttpClientRetrieveTest() {
			super(null);
		}

		public static int getPortFromURLTest(String url) {
			return getPortFromURL(url);
		}
	}
	
	public void testHttp() {
		int port;
		String url = "http://www.test.com";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertEquals(80, port);
	}

	public void testHttpWithPort() {
		int port;
		String url = "http://www.test.com:80/";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertEquals(80, port);
	}

	public void testHttps() {
		int port;
		String url = "https://www.test.com/";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertEquals(443, port);
	}

	public void testHttpsPortNOK() {
		int port;
		String url = "https://www.test.com:440/";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertFalse(443 == port);
	}

	public void testHttpWithSearchpart() {
		int port;
		String url = "https://www.test.com/test?value=yes";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertEquals(443, port);
	}

	public void testHttpWithCredentialsAndSearchpart() {
		int port;
		String url = "https://testuser:testpasswd@www.test.com/test?value=yes";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertEquals(443, port);
	}

	public void testHttpWithCredentialsPortSearchpart() {
		int port;
		String url = "http://testuser:testpasswd@www.test.com:8080/test?value=yes";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertEquals(8080, port);
	}

	public void testHttpWithCredentialsPortLongPath() {
		int port;
		String url = "https://testuser:testpasswd@host.domain.eu:443/pu/fisgui/svn/repository/fisgui/source-internal/trunk/target-platform/fisgui-5.8.1/content.xml";
		port = HttpClientRetrieveTest.getPortFromURLTest(url);
		assertEquals(443, port);
	}

}
