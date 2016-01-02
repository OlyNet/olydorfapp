package eu.olynet.olydorfapp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:simon.domke@olynet.eu">Simon Domke</a>
 */
public class News implements Serializable {
    public Date date;
    public int id;
    public byte[] image;
    public int organization;
    public String text;
    public String title;
}
