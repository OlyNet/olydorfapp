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

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import eu.olynet.olydorfapp.model.AbstractMetaItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class SimpleImageListener implements ImageListener {

    private final Context context;
    public final AbstractMetaItem<?> oldItem;
    private final ImageView vImage;

    /**
     * Instantiates a new SimpleImageListener.
     *
     * @param context the local Context.
     * @param oldItem the AbstractMetaItem to be updated with the image once it becomes available.
     * @param vImage  the ImageView to be updated once the image is available.
     */
    public SimpleImageListener(Context context, AbstractMetaItem<?> oldItem, ImageView vImage) {
        this.context = context;
        this.oldItem = oldItem;
        this.vImage = vImage;
    }

    @Override
    public void onImageLoad(AbstractMetaItem<?> item) {
        Log.d("ImageListener", "called for item: " + item.toString());

        /* get the image via Reflection */
        byte[] image;
        try {
            image = (byte[]) item.getClass().getMethod("getImage").invoke(item);
        } catch (Exception e) { /* pray that nothing goes wrong */
            throw new RuntimeException(e);
        }

        /* update the ImageView */
        Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image,
                                                                    UtilsDevice.getScreenWidth(
                                                                            context));
        if (bitmap != null) {
            vImage.setImageBitmap(bitmap);
        }

        /* update the image within the AbstractMetaItem via Reflection magic */
        try {
            oldItem.getClass().getMethod("setImage", byte[].class).invoke(oldItem, image);
        } catch (Exception e) { /* pray that nothing goes wrong */
            throw new RuntimeException(e);
        }
    }
}
