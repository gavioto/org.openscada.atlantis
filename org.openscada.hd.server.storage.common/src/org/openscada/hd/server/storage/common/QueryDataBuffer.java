/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.storage.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryState;
import org.openscada.hd.data.QueryParameters;
import org.openscada.hd.data.ValueInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QueryDataBuffer
{
    public static final String STDDEV = "STDDEV";

    public static final String MAX = "MAX";

    public static final String MIN = "MIN";

    public static final String AVG = "AVG";

    private final static Logger logger = LoggerFactory.getLogger ( QueryDataBuffer.class );

    protected abstract static class Data
    {

        protected final Date start;

        protected final Date end;

        protected boolean changed;

        private double average = Double.NaN;

        private double quality = Double.NaN;

        private double manual = Double.NaN;

        private double min = Double.NaN;

        private double max = Double.NaN;

        private double stdDev = Double.NaN;

        public Data ( final Date start, final Date end )
        {
            this.start = start;
            this.end = end;

            // defaulting to true since we need at least one transmission
            this.changed = true;
        }

        public Date getStart ()
        {
            return this.start;
        }

        public Date getEnd ()
        {
            return this.end;
        }

        public boolean isChanged ()
        {
            return this.changed;
        }

        public void resetChanged ()
        {
            this.changed = false;
        }

        public void setAverage ( final double average )
        {
            if ( Double.compare ( this.average, average ) != 0 )
            {
                this.changed = true;
                this.average = average;
            }
        }

        public void setStdDev ( final double stdDev )
        {
            if ( Double.compare ( this.stdDev, stdDev ) != 0 )
            {
                this.changed = true;
                this.stdDev = stdDev;
            }
        }

        public void setQuality ( final double error )
        {
            if ( Double.compare ( this.quality, error ) != 0 )
            {
                this.changed = true;
                this.quality = error;
            }
        }

        public void setManual ( final double manual )
        {
            if ( Double.compare ( this.manual, manual ) != 0 )
            {
                this.changed = true;
                this.manual = manual;
            }
        }

        public double getAverage ()
        {
            return this.average;
        }

        public double getStdDev ()
        {
            return this.stdDev;
        }

        public double getQuality ()
        {
            return this.quality;
        }

        public double getManual ()
        {
            return this.manual;
        }

        public void setMax ( final double max )
        {
            this.max = max;
        }

        public void setMin ( final double min )
        {
            this.min = min;
        }

        public double getMin ()
        {
            return this.min;
        }

        public double getMax ()
        {
            return this.max;
        }

        public abstract long getEntryCount ();

        public void apply ( final Data data )
        {
            setAverage ( data.average );
            setManual ( data.manual );
            setQuality ( data.quality );
            setMax ( data.max );
            setMin ( data.min );
            setStdDev ( data.stdDev );
        }
    }

    private final QueryListener listener;

    private final Executor executor;

    protected QueryState state;

    private final Date fixedStartDate;

    private final Date fixedEndDate;

    protected abstract Data[] getData ();

    public QueryDataBuffer ( final QueryListener listener, final Executor executor, final Date fixedStartDate, final Date fixedEndDate )
    {
        super ();
        this.listener = listener;
        this.executor = executor;
        this.fixedStartDate = fixedStartDate;
        this.fixedEndDate = fixedEndDate;
    }

    protected static void fillDataCells ( final QueryDataBuffer.Data[] data, final Calendar start, final Calendar end, final DataFactory dataFactory )
    {
        fillDataCells ( data, start.getTimeInMillis (), end.getTimeInMillis (), dataFactory );
    }

    protected static void fillDataCells ( final QueryDataBuffer.Data[] data, final long start, final long end, final DataFactory dataFactory )
    {
        // create data buffer
        final double period = (double) ( end - start ) / (double)data.length;
        double counter = 0;
        for ( int i = 0; i < data.length; i++ )
        {
            final long startTix = (long) ( start + counter );
            final long endTix = (long) ( start + ( counter + period ) );
            logger.trace ( "Init index {} with {} -> {}", new Object[] { i, startTix, endTix } );
            data[i] = dataFactory.create ( new Date ( startTix ), new Date ( endTix ) );
            counter += period;
        }
    }

    protected synchronized void notifyParameterUpdate ( final QueryParameters parameters, final Set<String> valueTypes )
    {
        if ( this.listener == null )
        {
            return;
        }

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                QueryDataBuffer.this.listener.updateParameters ( parameters, valueTypes );
            }
        } );
    }

    protected synchronized void notifyData ( final int index, final Map<String, List<Double>> values, final List<ValueInformation> valueInformation )
    {
        if ( this.listener == null )
        {
            return;
        }

        logger.debug ( "Sending data - index: {}, values#: {}, informations#: {}", new Object[] { index, values.size (), valueInformation.size () } );

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                QueryDataBuffer.this.listener.updateData ( index, values, valueInformation );
            }
        } );
    }

    protected synchronized void notifyStateUpdate ( final QueryState state )
    {
        logger.debug ( "Change state to {}", state );

        if ( this.state == state )
        {
            return;
        }

        this.state = state;

        if ( this.listener == null )
        {
            return;
        }

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                QueryDataBuffer.this.listener.updateState ( state );
            }
        } );

    }

    protected void notifyData ( final int startIndex, final int endIndex )
    {
        final int size = endIndex - startIndex;

        final List<ValueInformation> information = new ArrayList<ValueInformation> ( size );
        final Map<String, List<Double>> values = new HashMap<String, List<Double>> ( 4 );
        values.put ( AVG, new ArrayList<Double> ( size ) );
        values.put ( MIN, new ArrayList<Double> ( size ) );
        values.put ( MAX, new ArrayList<Double> ( size ) );
        values.put ( STDDEV, new ArrayList<Double> ( size ) );

        final QueryDataBuffer.Data[] data = getData ();

        int lastIndex = startIndex;
        for ( int i = startIndex; i < endIndex; i++ )
        {
            if ( data[i].isChanged () )
            {
                data[i].resetChanged ();

                // check of we are outside the fixed valid range
                if ( this.fixedStartDate != null && data[i].getEnd ().before ( this.fixedStartDate ) || this.fixedEndDate != null && data[i].getStart ().after ( this.fixedEndDate ) )
                {
                    // we are outside
                    information.add ( new ValueInformation ( 0.0, 0.0, convert ( data[i].getStart () ), convert ( data[i].getEnd () ), 0L ) );
                    values.get ( AVG ).add ( Double.NaN );
                    values.get ( MIN ).add ( Double.NaN );
                    values.get ( MAX ).add ( Double.NaN );
                    values.get ( STDDEV ).add ( Double.NaN );
                }
                else
                {
                    // we are inside
                    final double quality = Double.isNaN ( data[i].getQuality () ) ? 0.0 : data[i].getQuality ();
                    final double manual = Double.isNaN ( data[i].getManual () ) ? 0.0 : data[i].getManual ();

                    // add
                    information.add ( new ValueInformation ( quality, manual, convert ( data[i].getStart () ), convert ( data[i].getEnd () ), data[i].getEntryCount () ) );
                    values.get ( AVG ).add ( data[i].getAverage () );
                    values.get ( MIN ).add ( data[i].getMin () );
                    values.get ( MAX ).add ( data[i].getMax () );
                    values.get ( STDDEV ).add ( data[i].getStdDev () );
                }
            }
            else
            {
                // send
                if ( !information.isEmpty () )
                {
                    notifyData ( lastIndex, values, information );
                    information.clear ();
                    values.get ( AVG ).clear ();
                    values.get ( MIN ).clear ();
                    values.get ( MAX ).clear ();
                    values.get ( STDDEV ).clear ();
                }
                // clear
                lastIndex = i + 1;
            }
        }

        // send last
        if ( !information.isEmpty () )
        {
            notifyData ( lastIndex, values, information );
        }
    }

    private long convert ( final Date date )
    {
        return date.getTime ();
    }
}
