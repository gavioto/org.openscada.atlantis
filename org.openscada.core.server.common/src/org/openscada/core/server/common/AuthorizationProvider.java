/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.core.server.common;

import org.openscada.core.server.common.session.AbstractSessionImpl;
import org.openscada.sec.AuthorizationReply;
import org.openscada.sec.AuthorizationRequest;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.utils.concurrent.NotifyFuture;

public interface AuthorizationProvider<SI extends AbstractSessionImpl>
{
    public NotifyFuture<UserInformation> impersonate ( SI session, String targetUserName, CallbackHandler handler );

    public NotifyFuture<AuthorizationReply> authorize ( AuthorizationRequest authorizationRequest, CallbackHandler handler, AuthorizationResult defaultResult );
}
