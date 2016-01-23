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
        initMap.put(0, new Organization(0, "N/A"));
        initMap.put(1, new Organization(1, "OlyNet"));
        initMap.put(2, new Organization(2, "Bierstube"));
        initMap.put(3, new Organization(3, "OlyDisco"));
        initMap.put(4, new Organization(4, "OlyLounge"));

        organizations = Collections.unmodifiableMap(initMap);
    }

    private final int id;
    private final String name;

    public Organization(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
