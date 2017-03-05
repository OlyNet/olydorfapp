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

import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
class ImageCallback implements Callback<ResponseBody> {

    private final String type;
    private final int id;
    private final String field;

    ImageCallback(String type, int id, String field) {
        this.type = type;
        this.id = id;
        this.field = field;
    }

    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        ResourceManager rm = ProductionResourceManager.getInstance();
        byte[] image = null;

        // TODO: implement retry (probably by using some kind of recursion)
        int code = response.code();
        if (code == 200) {
            try {
                image = response.body().bytes();
            } catch (IOException e) {
                Log.e("ImageCallback", "IOException in response.body().bytes()", e);
            }
        } else if (code == 404) {
            Log.e("ResourceManager", "HTTP 404: image '" + type + "' - '" + id + "' - '" +
                                     field + "'");
        } else if (code >= 500 && code < 600) {
            Log.e("ResourceManager", "HTTP " + code + ": image '" + type + "' - '" + id +
                                     "' - '" + field + "'");
        } else {
            Log.e("ResourceManager", "Unexpected HTTP " + code + ": image '" + type +
                                     "' - '" + id + "' - '" + field + "'");
        }

        /* notify the ResourceManager if we received a valid image */
        if (image != null) {
            rm.asyncReceptionHook(this.type, this.id, image);
        }
    }

    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.e("ImageCallback", "onFailure triggered", t);
    }
}
