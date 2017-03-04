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

import java.util.List;

/**
 * The MultiID object is used to indicate that multiple IDs are to be fetched. Its toString
 * representation is formatted in such a way that it will be recognized by the OlydorfApp
 * middleware.
 *
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
@SuppressWarnings("unused")
final class MultiID {

    private final int[] ids;

    /**
     * Creates a MultiID object.
     *
     * @param ids at least one integer.
     */
    public MultiID(int... ids) {
        this.ids = ids;
    }

    /**
     * Creates a MultiID object.
     *
     * @param ids A List containing the Integers. Must not be <b>null</b>.
     */
    public MultiID(List<Integer> ids) {
        this.ids = new int[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            this.ids[i] = ids.get(i);
        }
    }

    @Override
    public String toString() {
        String result = "";
        if (ids != null) {
            for (int id : ids) {
                if (!result.equals("")) {
                    result += "%3b";
                }
                result += id;
            }
        }
        return result;
    }
}
