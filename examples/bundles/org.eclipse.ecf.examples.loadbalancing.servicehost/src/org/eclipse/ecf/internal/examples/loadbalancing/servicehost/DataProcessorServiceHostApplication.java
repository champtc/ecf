/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.internal.examples.loadbalancing.servicehost;

import java.util.Properties;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.IContainerManager;
import org.eclipse.ecf.examples.loadbalancing.IDataProcessor;
import org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class DataProcessorServiceHostApplication implements IApplication {

	private static final String LB_SVCHOST_CONTAINER_TYPE = "ecf.jms.activemq.tcp.manager.lb.svchost";
	public static final String DEFAULT_QUEUE_ID = "tcp://localhost:61616/exampleQueue";
	private static final String DEFAULT_TOPIC_ID = "tcp://localhost:61616/exampleTopic";
	private static final String LOAD_BALANCING_SERVICE_PROPERTY = "jms.queue.loadbalance";

	private BundleContext bundleContext;
	private ServiceTracker containerManagerServiceTracker;

	// JMS Queue URI that we will attach to as queue message producer (to issue
	// actual remote method/invocation
	// requests to server consumers). Note that this queueId can be changed by
	// using the -queueId launch parameter...e.g.:
	// -queueId tcp://myjmdnsbrokerdnsname:61616/myQueueName
	private String queueId = DEFAULT_QUEUE_ID;
	// JMS topic URI that we will register remote service registrations on...
	// so that service consumers can lookup/get/use remote services. Note that
	// this
	// topicId can be changed by using the -topicId launch parameter...e.g.
	// -topicId tcp://myjmdnsbrokerdnsname:61616/myTopicName
	private String topicId = DEFAULT_TOPIC_ID;

	// Container instance that connects us with the ActiveMQ queue as a message
	// producer and publishes the service on the topicId
	private IContainer container;
	// The service host remote service registration. This is used simply to
	// unregister the service when this application is stopped
	private IRemoteServiceRegistration dataProcessorServiceHostRegistration;

	public Object start(IApplicationContext appContext) throws Exception {
		bundleContext = Activator.getContext();
		// Process Arguments...i.e. set queueId and topicId if specified
		processArgs(appContext);

		// Create container of appropriate type, and with the topicId and
		// queueId set
		// upon construction
		container = getContainerManagerService().getContainerFactory()
				.createContainer(LB_SVCHOST_CONTAINER_TYPE,
						new Object[] { topicId, queueId });

		// Get IRemoteServiceContainerAdapter
		IRemoteServiceContainerAdapter remoteServiceAdapter = (IRemoteServiceContainerAdapter) container
				.getAdapter(IRemoteServiceContainerAdapter.class);

		Properties properties = new Properties();
		// This is setting (currently) magical service property that indicates
		// that
		// this service registration is a load balancing service host
		properties.put(LOAD_BALANCING_SERVICE_PROPERTY, "true");
		// Register the remote service with the IDataProcessor interface as it's
		// service registration.
		// Note that the LOAD_BALANCING_SERVICE_PROPERTY allows a new Object to
		// be specified as the implementation.
		// This object does not implement the IDataProcessor service interface,
		// but it is not actually used. Rather,
		// the LOAD_BALANCING_SERVICE_PROPERTY set to "true" specifies that for
		// this container the remote service
		// requests are proxied and forwarded to the JMS queue (where they are
		// load balanced among the n servers
		// that are consumers from that queue)
		dataProcessorServiceHostRegistration = remoteServiceAdapter
				.registerRemoteService(new String[] { IDataProcessor.class
						.getName() }, new Object(), properties);

		System.out.println("Registered service host with registration="
				+ dataProcessorServiceHostRegistration);

		// wait for remote service requests until stopped
		waitForDone();

		return IApplication.EXIT_OK;
	}

	public void stop() {
		if (dataProcessorServiceHostRegistration != null) {
			dataProcessorServiceHostRegistration.unregister();
			dataProcessorServiceHostRegistration = null;
		}
		if (container != null) {
			container.dispose();
			container = null;
			getContainerManagerService().removeAllContainers();
		}
		if (containerManagerServiceTracker != null) {
			containerManagerServiceTracker.close();
			containerManagerServiceTracker = null;
		}
		bundleContext = null;
	}

	private void processArgs(IApplicationContext appContext) {
		String[] originalArgs = (String[]) appContext.getArguments().get(
				"application.args");
		if (originalArgs == null)
			return;
		for (int i = 0; i < originalArgs.length; i++) {
			if (originalArgs[i].equals("-queueId")) {
				queueId = originalArgs[i + 1];
				i++;
			} else if (originalArgs[i].equals("-topicId")) {
				topicId = originalArgs[i + 1];
				i++;
			}
		}
	}

	private IContainerManager getContainerManagerService() {
		if (containerManagerServiceTracker == null) {
			containerManagerServiceTracker = new ServiceTracker(bundleContext,
					IContainerManager.class.getName(), null);
			containerManagerServiceTracker.open();
		}
		return (IContainerManager) containerManagerServiceTracker.getService();
	}

	private final Object appLock = new Object();
	private boolean done = false;

	private void waitForDone() {
		// then just wait here
		synchronized (appLock) {
			while (!done) {
				try {
					appLock.wait();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
	}

}
