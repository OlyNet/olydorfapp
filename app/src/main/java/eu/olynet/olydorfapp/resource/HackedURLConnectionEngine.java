/*
 * This file is part of OlydorfApp.
 *
 * OlydorfApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OlydorfApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OlydorfApp.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.olynet.olydorfapp.resource;

import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Wrapped version of the URLConnectionEngine because it is a piece of shit.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@SuppressWarnings("unused")
public class HackedURLConnectionEngine extends URLConnectionEngine {

    private int connectionTimeout = 0;

    private SSLContext notFuckedUpSslContext;

    protected HttpURLConnection createConnection(ClientInvocation request) throws IOException {
        HttpURLConnection con = super.createConnection(request);

        /* fix for timeout not being set */
        con.setConnectTimeout(connectionTimeout);

        /* fix for SSLContext not being applied */
        if (con instanceof HttpsURLConnection) {
            ((HttpsURLConnection) con).setSSLSocketFactory(
                    this.notFuckedUpSslContext.getSocketFactory());
        }

        /* return the actually working connection */
        return con;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    /**
     * Sets the maximum time in milliseconds to wait while connecting. Connecting to a server will
     * fail with a SocketTimeoutException if the timeout elapses before a connection is established.
     * The default value of 0 causes us to do a blocking connect. This does not mean we will never
     * time out, but it probably means you'll get a TCP timeout after several minutes.
     *
     * @param connectionTimeout the timeout in milliseconds
     * @see java.net.HttpURLConnection#setConnectTimeout(int)
     */
    public void setConnectionTimeout(int connectionTimeout) {
        if (connectionTimeout >= 0) {
            this.connectionTimeout = connectionTimeout;
        }
    }

    public SSLContext getNotFuckedUpSslContext() {
        return notFuckedUpSslContext;
    }

    public void setNotFuckedUpSslContext(SSLContext notFuckedUpSslContext) {
        this.notFuckedUpSslContext = notFuckedUpSslContext;
    }
}
