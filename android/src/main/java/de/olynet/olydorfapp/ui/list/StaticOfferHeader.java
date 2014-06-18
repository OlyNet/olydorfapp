package de.olynet.olydorfapp.ui.list;

import java.util.List;

/**
 * @author Simon Domke &lt;simon.domke@olydorf.mhn.de&gt;
 * @version $Id$
 */
public class StaticOfferHeader {
    private String name;
    private int resource;

    // ArrayList to store child objects
    private List<StaticOfferItem> children;

    public StaticOfferHeader(String name, int resource) {
        this.name = name;
        this.resource = resource;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getResource()
    {
        return resource;
    }

    // ArrayList to store child objects
    public List<StaticOfferItem> getChildren()
    {
        return children;
    }

    public void setChildren(List<StaticOfferItem> children)
    {
        this.children = children;
    }
}
