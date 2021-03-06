/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.opc.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A job which depends on a blocking function run in the current thread.
 * <p>
 * Canceling the job works by issuing {@link Thread#interrupt()} on the thread
 * executing the function.
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 *
 */
public abstract class ThreadJob extends Job
{

    private final static Logger logger = LoggerFactory.getLogger ( ThreadJob.class );

    protected volatile Thread runningThread;

    public ThreadJob ( final long timeout )
    {
        super ( timeout );
    }

    @Override
    protected synchronized void interrupt ()
    {
        if ( !this.canceled && this.runningThread != null )
        {
            logger.info ( "Interrupting current job" );
            this.canceled = true;
            this.runningThread.interrupt ();
        }
    }

    @Override
    protected void run () throws Exception
    {
        this.runningThread = Thread.currentThread ();

        try
        {
            perform ();
        }
        catch ( final Throwable e )
        {
            this.error = e;
        }
        finally
        {
            synchronized ( this )
            {
                this.runningThread = null;
            }

            if ( Thread.currentThread ().isInterrupted () )
            {
                // catch up interrupted state 
                try
                {
                    Thread.sleep ( 0 );
                }
                catch ( final InterruptedException e )
                {
                    // we expected that
                }
            }
        }
    }

    protected abstract void perform () throws Exception;
}
