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

package org.openscada.da.server.osgi;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.DataItem;
import org.openscada.utils.osgi.pool.AllObjectPoolServiceTracker;
import org.openscada.utils.osgi.pool.ObjectPoolListener;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private HiveImpl service;

    private ServiceRegistration handle;

    private ServiceListener listener;

    private ObjectPoolTracker poolTracker;

    private AllObjectPoolServiceTracker itemTracker;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new HiveImpl ( context );
        this.service.start ();

        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();

        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A common generic OSGi DA Hive" );

        this.handle = context.registerService ( Hive.class.getName (), this.service, properties );

        context.addServiceListener ( this.listener = new ServiceListener () {

            public void serviceChanged ( final ServiceEvent event )
            {
                switch ( event.getType () )
                {
                case ServiceEvent.REGISTERED:
                    Activator.this.addItem ( event.getServiceReference () );
                    break;
                case ServiceEvent.UNREGISTERING:
                    Activator.this.removeItem ( event.getServiceReference () );
                    break;
                }
            }
        }, "(" + Constants.OBJECTCLASS + "=" + DataItem.class.getName () + ")" );

        final ServiceReference[] refs = context.getServiceReferences ( DataItem.class.getName (), null );
        if ( refs != null )
        {
            for ( final ServiceReference ref : refs )
            {
                addItem ( ref );
            }
        }

        this.poolTracker = new ObjectPoolTracker ( context, DataItem.class.getName () );
        this.poolTracker.open ();

        this.itemTracker = new AllObjectPoolServiceTracker ( this.poolTracker, new ObjectPoolListener () {

            public void serviceRemoved ( final Object service, final Dictionary<?, ?> properties )
            {
                Activator.this.service.removeItem ( (DataItem)service );
            }

            public void serviceModified ( final Object service, final Dictionary<?, ?> properties )
            {
            }

            public void serviceAdded ( final Object service, final Dictionary<?, ?> properties )
            {
                Activator.this.service.addItem ( (DataItem)service, properties );
            }
        } );
        this.itemTracker.open ();
    }

    protected void removeItem ( final ServiceReference serviceReference )
    {
        this.service.removeItem ( serviceReference );
    }

    protected void addItem ( final ServiceReference serviceReference )
    {
        this.service.addItem ( serviceReference );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        context.removeServiceListener ( this.listener );

        this.itemTracker.close ();
        this.poolTracker.close ();

        this.handle.unregister ();
        this.handle = null;

        this.service.stop ();
        this.service = null;

    }

}
