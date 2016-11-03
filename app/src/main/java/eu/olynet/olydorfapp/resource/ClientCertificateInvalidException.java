package eu.olynet.olydorfapp.resource;

import java.io.IOException;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */

public class ClientCertificateInvalidException extends IOException {

    public ClientCertificateInvalidException() {
        super();
    }

    public ClientCertificateInvalidException(String message) {
        super(message);
    }

    public ClientCertificateInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientCertificateInvalidException(Throwable cause) {
        super(cause);
    }
}
