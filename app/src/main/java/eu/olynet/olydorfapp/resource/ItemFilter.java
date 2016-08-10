package eu.olynet.olydorfapp.resource;

import eu.olynet.olydorfapp.model.AbstractMetaItem;

/**
 * Basically a Predicate<AbstractMetaItem> but Android doesn't support that on APIs < 24.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public interface ItemFilter {

    /**
     * Check whether to include a specific item.
     *
     * @param abstractMetaItem the AbstractMetaItem to check.
     * @return <b>true</b> iff it should be included.
     */
    boolean test(AbstractMetaItem<?> abstractMetaItem);
}
