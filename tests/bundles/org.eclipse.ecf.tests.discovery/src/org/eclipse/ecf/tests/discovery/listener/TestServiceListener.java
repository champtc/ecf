/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev_eclipse.org <at> lemmster <dot> de) - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tests.discovery.listener;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.discovery.IDiscoveryLocator;
import org.eclipse.ecf.discovery.IServiceEvent;
import org.eclipse.ecf.discovery.IServiceListener;
import org.eclipse.ecf.tests.discovery.AbstractDiscoveryTest;

public class TestServiceListener extends TestListener implements IServiceListener {

	private final IDiscoveryLocator locator;
	private final String testName;


	public TestServiceListener(int eventsToExpect, IDiscoveryLocator aLocator, String testName) {
		super(eventsToExpect);
		Assert.isNotNull(testName);
		this.testName = testName;
		Assert.isNotNull(aLocator);
		locator = aLocator;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IServiceListener#serviceDiscovered(org.eclipse.ecf.discovery.IServiceEvent)
	 */
	public void serviceDiscovered(IServiceEvent anEvent) {
		if (matchesExpected(anEvent)) {
			events.add(anEvent);
			if(events.size() == amountOfEventsToExpect) {
				synchronized (this) {
					notifyAll();
				}
			}
		} else {
			System.err.println("Ignored unexpected events received by test listener " + anEvent);
		}
	}

	protected boolean matchesExpected(IServiceEvent anEvent) {
		return (testName.equals(anEvent.getServiceInfo().getServiceProperties()
				.getProperty(AbstractDiscoveryTest.TEST_NAME)));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ecf.discovery.IServiceListener#serviceUndiscovered(org.eclipse.ecf.discovery.IServiceEvent)
	 */
	public void serviceUndiscovered(IServiceEvent anEvent) {
		throw new java.lang.UnsupportedOperationException("TestServiceListener#serviceUndiscovered not yet implemented");
	}


	/**
	 * @return A DiscoveryLocator (Container)
	 */
	public IContainer getLocator() {
		return (IContainer) locator;
	}

	public boolean triggerDiscovery() {
		return false;
	}
}
