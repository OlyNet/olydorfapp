package eu.olynet.olydorfapp.resources;

import android.util.Log;

import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Wrapped version of the URLConnectionEngine to be able to specify a connection timeout.
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class HackedURLConnectionEngine extends URLConnectionEngine {

    int connectionTimeout = -1;

    protected HttpURLConnection createConnection(ClientInvocation request) throws IOException{
        HttpURLConnection con = super.createConnection(request);
        if(connectionTimeout > 0) {
            con.setConnectTimeout(connectionTimeout);
        }

        return con;
    }

    /**
     * Sets the maximum time in milliseconds to wait while connecting.
     * Connecting to a server will fail with a SocketTimeoutException if the timeout elapses before a connection is established.
     * The default value of 0 causes us to do a blocking connect.
     * This does not mean we will never time out, but it probably means you'll get a TCP timeout after several minutes.
     *
     * @see java.net.HttpURLConnection#setConnectTimeout(int)
     * @param connectionTimeout the timeout in miliseconds
     */
    public void setConnectionTimeout(int connectionTimeout) {
        if(connectionTimeout >= 0) {
            this.connectionTimeout = connectionTimeout;
        }
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }
}
