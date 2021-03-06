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

package org.openscada.da.server.test.items;

import java.util.Timer;
import java.util.TimerTask;

import org.openscada.da.server.common.DataItemInputCommon;

public abstract class ScheduledDataItem extends DataItemInputCommon implements Runnable
{
    private final Timer timer;

    public ScheduledDataItem ( final String name, final Timer timer, final int period )
    {
        super ( name );
        this.timer = timer;
        this.timer.scheduleAtFixedRate ( new TimerTask () {

            @Override
            public void run ()
            {
                ScheduledDataItem.this.run ();
            }
        }, 0, period );
    }
}
