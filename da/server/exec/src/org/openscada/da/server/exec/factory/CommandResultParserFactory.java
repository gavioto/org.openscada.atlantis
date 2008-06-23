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

package org.openscada.da.server.exec.factory;

import java.lang.reflect.InvocationTargetException;

import org.openscada.da.server.exec.base.CommandResultParser;

public class CommandResultParserFactory
{
    /**
     * Create a new CommandQueue
     * @param commandQueueClassName
     * @param hive
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static CommandResultParser createParser ( String commandResultParserClassName ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        Class<?> commandQueueClass = Class.forName ( commandResultParserClassName );
        return (CommandResultParser)commandQueueClass.newInstance ();
    }
}
