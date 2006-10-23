package org.openscada.da.client.viewer.model.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.da.client.net.Connection;

public class DataItemObject extends BaseDynamicObject
{
    private DataItemOutput _output = null;
    private DataItemInput _input = null;
    
    private String _item = null;
    private String _connectionURI = null;
    
    public DataItemObject ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "connection" ) );
        addInput ( new PropertyInput ( this, "item" ) );
    }
    
    public void setItem ( String item )
    {
        _item = item;
        update ();
    }
    
    public void setConnection ( String connectionURI )
    {
        _connectionURI = connectionURI;
        update ();
    }
    
    protected void update ()
    {
        if ( _item != null && _connectionURI != null && _output == null )
        {
            try
            {
                _output = new DataItemOutput ( getConnection (), _item, "value" );
                addOutput ( _output );
                _input = new DataItemInput ( getConnection (), _item, "value" );
                addInput ( _input );
            }
            catch ( Exception e )
            {
                // FIXME: report that
            }
        }
    }
    
    protected Connection getConnection () throws URISyntaxException
    {
        ConnectionInfo ci = ConnectionInfo.fromUri ( new URI ( _connectionURI ) );
        Connection connection = new Connection ( ci );
        connection.connect ();
        return connection;
    }
}
