package org.openscada.hd.perf.test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication
{

    private final ExecutorService executor = Executors.newFixedThreadPool ( Runtime.getRuntime ().availableProcessors () );

    public Object start ( final IApplicationContext context ) throws Exception
    {

        final Collection<Future<?>> tasks = new LinkedList<Future<?>> ();
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "hd:net://localhost:1402", "h.1" ) ) );
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "da:net://localhost:1402", "h.1" ) ) );
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "hd:net://localhost:1402", "h.1" ) ) );
        tasks.add ( this.executor.submit ( new ConnectionRunner ( "hd:net://localhost:1402", "h.1" ) ) );

        for ( final Future<?> task : tasks )
        {
            task.get ();
        }

        this.executor.shutdown ();

        dumpPerformanData ();

        return null;
    }

    public static final Object QUERY = new Object ();

    private void dumpPerformanData () throws InterruptedException, IOException
    {
        Thread.sleep ( 5 * 1000 );
        System.gc ();

        Writer out = new FileWriter ( "/tmp/psdata.dot" );
        Tracker.dumpCollect ( out, Tracker.THREAD );
        out.close ();

        out = new FileWriter ( "/tmp/psdata.query.dot" );
        Tracker.dumpCollect ( out, QUERY );
        out.close ();
    }

    public void stop ()
    {
    }

}
