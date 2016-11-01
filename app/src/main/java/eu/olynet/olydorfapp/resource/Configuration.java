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

/**
 * The resteasy client configuration file.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
final class Configuration {

    /**
     * The file containing the OlyNet e.V. custom Certificate Authority (CA).
     */
    static final String CA_FILE = "olynet_ca.pem";

    /**
     * The file containing the version-specific user certificate for accessing the server.
     */
    static final String CERTIFICATE_FILE = "app_01.pfx";

    /**
     * The decryption key for the version-specific user certificate.
     */
    static final char[] CERTIFICATE_KEY = "$gf6yuW$%Cs4".toCharArray();

    /**
     * The base URL of the REST endpoints on the server.
     */
    static final String SERVER_BASE_URL = "https://wstest.olynet.eu/api";
}
