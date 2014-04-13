package de.olynet.olydorfapp.cards;

import android.content.Context;

import de.olynet.olydorfapp.R;
import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Simon on 11.04.2014.
 */
public class BierstubeDrinksExpandCard extends CardExpand
{
    public BierstubeDrinksExpandCard(Context context) {
        super(context, R.layout.bs_static_offer);
    }

    public BierstubeDrinksExpandCard(Context context, int innerLayout) {
        super(context, R.layout.bs_static_offer);
    }
}
