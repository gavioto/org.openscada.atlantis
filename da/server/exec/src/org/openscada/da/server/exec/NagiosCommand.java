/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/**
 * 
 */
package org.openscada.da.server.exec;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.utils.collection.MapBuilder;

public class NagiosCommand extends CommandBase
{
    /**
     * Logger
     */
    private static Logger logger = Logger.getLogger ( NagiosCommand.class );

    /**
     * The nagios test result state
     */
    private final DataItemInputChained stateItem;

    /**
    * Constructor
    * @param hive
    * @param queue
    * @param commandName
    */
    public NagiosCommand ( HiveCommon hive, String commandName, CommandQueue queue )
    {
        super ( hive, commandName, queue );

        // show whether the command is currently active or not
        this.stateItem = this.getCommandItemFactory ().constructErrorChainInput ( "state" );
        this.stateItem.updateValue ( new Variant ( false ) );
        this.stateItem.updateAttributes ( new MapBuilder<String, Variant> ().put ( "execution.error", new Variant ( true ) ).getMap () );
    }

    /**
     * run the command task
     */
    @Override
    public void execute ()
    {
        try
        {
            Thread.sleep ( 50 );
            this.stateItem.updateValue ( new Variant ( true ) );
            this.stateItem.updateAttributes ( new MapBuilder<String, Variant> ().put ( "execution.error", new Variant ( true ) ).getMap () );
        }
        catch ( Throwable ch )
        {

        }
    }

}
