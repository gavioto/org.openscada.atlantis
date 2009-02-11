package org.openscada.da.server.proxy;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.chain.WriteHandler;
import org.openscada.da.server.common.chain.WriteHandlerItem;
import org.openscada.utils.collection.MapBuilder;

/**
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 *
 */
public class ProxyConnection
{
    private static Logger logger = Logger.getLogger ( ProxyConnection.class );

    /**
     * item name for items which are only relevant for proxy server
     */
    public static final String ITEM_PREFIX = "proxy.connection";

    private final Hive hive;

    private final ProxyGroup group;

    private final FolderCommon connectionsFolder;

    private final String separator;

    private WriteHandlerItem activeConnectionItem;

    private DataItemInputChained switchStarted;

    private DataItemInputChained switchEnded;

    private DataItemInputChained switchInProgress;

    private DataItemInputChained switchDuration;

    private final FolderCommon connectionFolder;

    private DataItemCommand connectItem;

    private DataItemCommand disconnectItem;

    /**
     * @param hive
     * @param prefix 
     * @param connectionsFolder
     */
    public ProxyConnection ( final Hive hive, final ProxyPrefixName prefix, final FolderCommon connectionsFolder )
    {
        this.hive = hive;
        this.connectionsFolder = connectionsFolder;
        this.group = new ProxyGroup ( hive, prefix );
        this.separator = this.hive.getSeparator ();

        this.connectionFolder = new FolderCommon ();
        this.group.setConnectionFolder ( this.connectionFolder );
        this.connectionsFolder.add ( this.group.getPrefix ().getName (), this.connectionFolder, new HashMap<String, Variant> () );
    }

    protected DataItemInputChained createItem ( final String localId )
    {
        final DataItemInputChained item = new DataItemInputChained ( itemName ( localId ) );

        this.hive.registerItem ( item );
        this.connectionFolder.add ( localId, item, new MapBuilder<String, Variant> ().getMap () );

        return item;
    }

    private String itemName ( final String localId )
    {
        return this.group.getPrefix ().getName () + this.separator + ITEM_PREFIX + this.separator + localId;
    }

    /**
     * 
     */
    public void init ()
    {
        this.switchStarted = createItem ( "switch.started" );
        this.switchEnded = createItem ( "switch.ended" );
        this.switchInProgress = createItem ( "switch.inprogress" );
        this.switchDuration = createItem ( "switch.duration" );

        // active Connection
        this.activeConnectionItem = new WriteHandlerItem ( itemName ( "active" ), new WriteHandler () {

            public void handleWrite ( final Variant value ) throws Exception
            {
                final String newId = value.asString ( null );
                ProxyConnection.this.switchTo ( newId );
            }
        } );
        this.hive.registerItem ( this.activeConnectionItem );

        // fill active connection information
        final HashMap<String, Variant> availableConnections = new HashMap<String, Variant> ();
        for ( final ProxySubConnection subConnection : this.group.getSubConnections ().values () )
        {
            availableConnections.put ( "available.connection." + subConnection.getId (), new Variant ( subConnection.getPrefix ().getName () ) );
        }
        this.connectionFolder.add ( "active", this.activeConnectionItem, availableConnections );

        this.activeConnectionItem.updateData ( new Variant ( this.group.getCurrentConnection ().toString () ), availableConnections, AttributeMode.SET );

        this.connectItem = new DataItemCommand ( itemName ( "connect" ) );
        this.connectItem.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value ) throws Exception
            {
                ProxyConnection.this.group.connectCurrentConnection ();
            }
        } );
        this.hive.registerItem ( this.connectItem );
        this.connectionFolder.add ( "connect", this.connectItem, new MapBuilder<String, Variant> ().getMap () );

        this.disconnectItem = new DataItemCommand ( itemName ( "disconnect" ) );
        this.disconnectItem.addListener ( new DataItemCommand.Listener () {

            public void command ( final Variant value ) throws Exception
            {
                ProxyConnection.this.group.connectCurrentConnection ();
            }
        } );
        this.hive.registerItem ( this.disconnectItem );
        this.connectionFolder.add ( "disconnect", this.disconnectItem, new MapBuilder<String, Variant> ().getMap () );

        this.group.addConnectionStateListener ( new NotifyConnectionErrorListener ( this.group ) );

        // add proxy folder for actual items
        this.group.start ();
    }

    protected void switchTo ( final String newId )
    {
        try
        {
            final ProxySubConnection newSubConnection = ProxyConnection.this.group.getSubConnections ().get ( new ProxySubConnectionId ( newId ) );
            if ( newSubConnection != null )
            {
                ProxyConnection.this.switchTo ( newSubConnection.getId () );
            }
        }
        catch ( final Throwable e )
        {
            logger.error ( String.format ( "Failed to switch to: %s", newId ), e );
        }
    }

    protected void switchTo ( final ProxySubConnectionId id )
    {
        // mark start of switch
        final long start = System.currentTimeMillis ();
        this.switchStarted.updateData ( new Variant ( start ), null, AttributeMode.UPDATE );
        this.switchInProgress.updateData ( new Variant ( true ), null, AttributeMode.UPDATE );

        // perform switch
        this.group.switchTo ( id );
        this.activeConnectionItem.updateData ( new Variant ( id ), null, null );

        // mark end of switch
        this.switchInProgress.updateData ( new Variant ( false ), null, AttributeMode.UPDATE );
        final long end = System.currentTimeMillis ();
        this.switchEnded.updateData ( new Variant ( end ), null, AttributeMode.UPDATE );
        this.switchDuration.updateData ( new Variant ( end - start ), null, AttributeMode.UPDATE );
    }

    /**
     * @param id
     * @return item
     */
    public ProxyDataItem realizeItem ( final String id )
    {
        return this.group.realizeItem ( id );
    }

    /**
     * 
     */
    public void dispose ()
    {
        this.group.stop ();
    }

    public ProxyPrefixName getPrefix ()
    {
        return this.group.getPrefix ();
    }

    public void setWait ( final int wait )
    {
        this.group.setWait ( wait );
    }

    public void addConnection ( final Connection connection, final String id, final ProxyPrefixName proxyPrefixName ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        this.group.addConnection ( connection, id, proxyPrefixName, this.connectionFolder );
    }
}
