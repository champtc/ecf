/*******************************************************************************
 * Copyright (c) 2004 Composent, Inc., Peter Nehrer, Boris Bokowski. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.datashare.events;

import org.eclipse.ecf.core.identity.ID;

/**
 * Super interface for events delivered to IChannelContainerAdapter instances
 * @author slewis
 *
 */
public interface IChannelContainerEvent {
	/**
	 * Get the id of the channel associated with this event
	 * @return ID of the channel
	 */
	public ID getChannelID();
	/**
	 * Get the id of the channel container associated with this event
	 * @return ID of the channel container
	 */
	public ID getChannelContainerID();
}
