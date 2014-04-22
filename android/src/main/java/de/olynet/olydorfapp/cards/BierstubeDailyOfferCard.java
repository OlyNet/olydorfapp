package de.olynet.olydorfapp.cards;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import de.olynet.olydorfapp.R;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * @author Simon Domke &lt;simon.domke@olydorf.mhn.de&gt;
 * @version $Id$
 */
public class BierstubeDailyOfferCard extends Card
{
    protected String headerTitle;
    protected String secondaryTitle;
    protected float rating;
    private boolean isDrinkOfTheDayCard = false;

    public BierstubeDailyOfferCard(Context context) {
        super(context, R.layout.bs_card_daily_offer_inner);
        init();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        // TODO: populate controls

        TextView name = (TextView) view.findViewById(R.id.bs_daily_offer_name);
        name.setText("Schweinebraten");

        TextView price = (TextView) view.findViewById(R.id.bs_daily_offer_price);
        price.setText("4,50€");

        TextView cook = (TextView) view.findViewById(R.id.bs_daily_offer_cook);
        cook.setText("Rongo");

        if(isDrinkOfTheDayCard){
            // only hide the cook row, otherwise we have to differently looking cards
            view.findViewById(R.id.bs_daily_offer_tag_cook).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.bs_daily_offer_cook).setVisibility(View.INVISIBLE);
        }

        RatingBar mRatingBar = (RatingBar) parent.findViewById(R.id.bs_daily_offer_ratingbar);

        mRatingBar.setNumStars(5);
        mRatingBar.setMax(5);
        mRatingBar.setStepSize(0.5f);
        mRatingBar.setRating(4.7f);

    }

    public void init() {
        CardHeader header = new CardHeader(getContext());
        header.setTitle(headerTitle);

        addCardHeader(header);

        BierstubeDailyOfferThumbnail thumbnail = new BierstubeDailyOfferThumbnail(getContext());
        thumbnail.setDrawableResource(R.drawable.jm);
        addCardThumbnail(thumbnail);

        // Edit this to make the card clickable
        setClickable(false);
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public void disableRowCook()
    {
        this.isDrinkOfTheDayCard = true;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        this.secondaryTitle = secondaryTitle;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    class BierstubeDailyOfferThumbnail extends CardThumbnail {

        public BierstubeDailyOfferThumbnail(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage)
        {
            if (viewImage != null) {
                if (parent!=null && parent.getResources()!=null){
                    DisplayMetrics metrics=parent.getResources().getDisplayMetrics();

                    int base = 98;

                    if (metrics!=null){
                        viewImage.getLayoutParams().width = (int)(base*metrics.density);
                        viewImage.getLayoutParams().height = (int)(base*metrics.density);
                    }else{
                        viewImage.getLayoutParams().width = 196;
                        viewImage.getLayoutParams().height = 196;
                    }
                }
            }

        }
    }
}
