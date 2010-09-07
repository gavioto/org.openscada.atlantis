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

package org.openscada.da.server.browser.common;

import java.util.Collections;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.core.browser.FolderEntry;

public class FolderEntryCommon implements FolderEntry
{
    private String _name = null;

    private Folder _folder = null;

    private Map<String, Variant> _attributes = null;

    public FolderEntryCommon ( final String name, final Folder folder, final Map<String, Variant> attributes )
    {
        this._name = name;
        this._folder = folder;
        this._attributes = attributes;
        if ( this._attributes == null )
        {
            this._attributes = Collections.emptyMap ();
        }
    }

    public String getName ()
    {
        return this._name;
    }

    public Folder getFolder ()
    {
        return this._folder;
    }

    public Map<String, Variant> getAttributes ()
    {
        return this._attributes;
    }
}
