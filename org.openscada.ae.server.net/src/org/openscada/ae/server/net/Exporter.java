/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.net;

import java.io.IOException;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerDelegate;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerFactory;
import org.openscada.ae.server.Service;
import org.openscada.ae.server.exporter.ExporterBase;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.server.net.Server;

public class Exporter extends ExporterBase
{
    private Server server;

    public Exporter ( final Service service, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( service, connectionInformation );
    }

    private void createServer () throws IOException
    {
        this.server = new Server ( this.connectionInformation );
        this.server.start ( createFactory () );
    }

    private SingleSessionIoHandlerDelegate createFactory ()
    {
        return new SingleSessionIoHandlerDelegate ( new SingleSessionIoHandlerFactory () {

            public SingleSessionIoHandler getHandler ( final IoSession session ) throws Exception
            {
                return new ServerConnectionHandler ( Exporter.this.service, session, Exporter.this.connectionInformation );
            }
        } );
    }

    public void start () throws Exception
    {
        createServer ();
    }

    public void stop () throws Exception
    {
        destroyServer ();
    }

    private void destroyServer ()
    {
        if ( this.server != null )
        {
            this.server.dispose ();
            this.server = null;
        }
    }
}
