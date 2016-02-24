package eu.olynet.olydorfapp.resources;

import java.io.IOException;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class NoConnectionException extends IOException {

    public NoConnectionException() {
        super();
    }

    public NoConnectionException(String message) {
        super(message);
    }

    public NoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoConnectionException(Throwable cause) {
        super(cause);
    }
}
