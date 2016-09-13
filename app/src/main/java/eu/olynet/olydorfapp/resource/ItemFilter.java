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
