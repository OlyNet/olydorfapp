/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.resources;

import android.util.Log;

import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Wrapped version of the URLConnectionEngine because it is a piece of shit.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class HackedURLConnectionEngine extends URLConnectionEngine {

    private int connectionTimeout = 0;

    private SSLContext notFuckedUpSslContext;

    protected HttpURLConnection createConnection(ClientInvocation request) throws IOException {
        HttpURLConnection con = super.createConnection(request);

        /* fix for timeout not being set */
        con.setConnectTimeout(connectionTimeout);

        /* fix for SSLContext not being applied */
        if(con instanceof HttpsURLConnection) {
            ((HttpsURLConnection) con).setSSLSocketFactory(
                    this.notFuckedUpSslContext.getSocketFactory());
        }

        /* return the actually working connection */
        return con;
    }

    /**
     * Sets the maximum time in milliseconds to wait while connecting.
     * Connecting to a server will fail with a SocketTimeoutException if the timeout elapses before a connection is established.
     * The default value of 0 causes us to do a blocking connect.
     * This does not mean we will never time out, but it probably means you'll get a TCP timeout after several minutes.
     *
     * @param connectionTimeout the timeout in milliseconds
     * @see java.net.HttpURLConnection#setConnectTimeout(int)
     */
    public void setConnectionTimeout(int connectionTimeout) {
        if (connectionTimeout >= 0) {
            this.connectionTimeout = connectionTimeout;
        }
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public SSLContext getNotFuckedUpSslContext() {
        return notFuckedUpSslContext;
    }

    public void setNotFuckedUpSslContext(SSLContext notFuckedUpSslContext) {
        this.notFuckedUpSslContext = notFuckedUpSslContext;
    }
}
