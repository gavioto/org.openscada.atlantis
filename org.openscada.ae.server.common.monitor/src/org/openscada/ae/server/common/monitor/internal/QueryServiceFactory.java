/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.ae.server.common.monitor.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.openscada.ae.server.common.monitor.MonitorQuery;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class QueryServiceFactory extends AbstractServiceConfigurationFactory<BundleMonitorQuery>
{
    public final static String FACTORY_ID = "ae.monitor.query";

    private final ObjectPoolTracker poolTracker;

    public QueryServiceFactory ( final BundleContext context, final ObjectPoolTracker poolTracker )
    {
        super ( context );
        this.poolTracker = poolTracker;
    }

    @Override
    protected Entry<BundleMonitorQuery> createService ( final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final BundleMonitorQuery query = new BundleMonitorQuery ( context, this.poolTracker );
        query.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ();
        properties.put ( Constants.SERVICE_PID, configurationId );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );

        return new Entry<BundleMonitorQuery> ( configurationId, query, context.registerService ( MonitorQuery.class.getName (), query, properties ) );
    }

    @Override
    protected void disposeService ( final String configurationId, final BundleMonitorQuery service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<BundleMonitorQuery> updateService ( final String configurationId, final Entry<BundleMonitorQuery> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return entry;
    }

}
