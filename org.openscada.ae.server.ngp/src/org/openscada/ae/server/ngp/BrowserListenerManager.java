/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ae.server.ngp;

import java.util.List;
import java.util.Set;

import org.openscada.ae.BrowserListener;
import org.openscada.ae.data.BrowserEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserListenerManager implements BrowserListener
{

    private final static Logger logger = LoggerFactory.getLogger ( BrowserListenerManager.class );

    private final ServerConnectionImpl connection;

    public BrowserListenerManager ( final ServerConnectionImpl connection )
    {
        this.connection = connection;
    }

    @Override
    public void dataChanged ( final List<BrowserEntry> addedOrUpdated, final Set<String> removed, final boolean full )
    {
        logger.debug ( "Browser data changed: {}, {}, {}", new Object[] { addedOrUpdated, removed, full } );

        this.connection.handleBrowseDataChanged ( this, addedOrUpdated, removed, full );
    }

}
