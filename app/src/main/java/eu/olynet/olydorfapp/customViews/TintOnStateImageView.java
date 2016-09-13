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
package eu.olynet.olydorfapp.customViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import eu.olynet.olydorfapp.R;

/**
 * ImageView that changes it's image color depending on the state (pressed, selected...)
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class TintOnStateImageView extends ImageView {
    private ColorStateList mColorStateList;

    public TintOnStateImageView(Context context) {
        super(context);
    }

    public TintOnStateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context, attrs, 0);
    }

    public TintOnStateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context, attrs, defStyleAttr);
    }

    /**
     * Create, bind and set up the resource
     *
     * @param context      is the context to get the resource from
     * @param attributeSet is the attributeSet
     * @param defStyle     is the style
     */
    private void initialise(Context context, AttributeSet attributeSet, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attributeSet,
                R.styleable.TintOnStateImageView, defStyle,
                0);
        mColorStateList = a.getColorStateList(R.styleable.TintOnStateImageView_colorStateList);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mColorStateList != null && mColorStateList.isStateful()) {
            updateTintColor();
        }
    }

    /**
     * Updates the color of the image
     */
    private void updateTintColor() {
        int color = mColorStateList.getColorForState(getDrawableState(),
                ContextCompat.getColor(getContext(),
                        R.color.nav_drawer_item_icon_normal));

        super.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
