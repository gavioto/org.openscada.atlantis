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

package org.openscada.da.core;

import org.openscada.utils.lang.Immutable;

@Immutable
public class WriteResult
{
    private Throwable error = null;

    /**
     * A pre-existing OK result
     */
    public static final WriteResult OK = new WriteResult ();

    /**
     * Create an "OK" result
     */
    public WriteResult ()
    {
    }

    /**
     * Create a result with error information
     * @param error the error or <code>null</code> if the result is "ok"
     */
    public WriteResult ( final Throwable error )
    {
        this.error = error;
    }

    public Throwable getError ()
    {
        return this.error;
    }

    public boolean isError ()
    {
        return this.error != null;
    }

    public boolean isSuccess ()
    {
        return this.error == null;
    }
}