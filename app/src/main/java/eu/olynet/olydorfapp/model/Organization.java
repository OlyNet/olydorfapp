/**
 * Copyright (C) OlyNet e.V. 2016 - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential
 */
package eu.olynet.olydorfapp.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu<a>
 */
public class Organization {

    public static final Map<Integer, Organization> organizations;

    static {
        Map<Integer, Organization> initMap = new LinkedHashMap<>();
        initMap.put(0, new Organization(0, "N/A", "https://olydorf.mhn.de", new byte[0], "unknown"));
        initMap.put(1, new Organization(1, "OlyNet e.V.", "https://www.olynet.eu", new byte[0], "olynet"));
        initMap.put(2, new Organization(2, "Die Bierstube", "https://www.facebook.com/bierstube/", new byte[0], "bierstube"));

        organizations = Collections.unmodifiableMap(initMap);
    }

    private final int id;
    private final String name;
    private final String website;
    private final byte[] image;
    private final String shortName;

    public Organization(int id, String name, String website, byte[] image, String shortName) {
        this.id = id;
        this.name = name;
        this.website = website;
        this.image = image;
        this.shortName = shortName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
