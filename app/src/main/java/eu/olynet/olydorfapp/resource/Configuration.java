package eu.olynet.olydorfapp.resource;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public final class Configuration {

    /**
     * The file containing the OlyNet e.V. custom Certificate Authority (CA).
     */
    protected static final String CA_FILE = "olynet_ca.pem";

    /**
     * The file containing the version-specific user certificate for accessing the server.
     */
    protected static final String CERTIFICATE_FILE = "app_01.pfx";

    /**
     * The decryption key for the version-specific user certificate.
     */
    protected static final char[] CERTIFICATE_KEY = "$gf6yuW$%Cs4".toCharArray();

    /**
     * The base URL of the REST endpoints on the server.
     */
    protected static final String SERVER_BASE_URL = "https://wstest.olynet.eu/dorfapp-rest/api";
}
