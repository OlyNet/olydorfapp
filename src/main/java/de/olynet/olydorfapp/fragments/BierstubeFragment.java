package de.olynet.olydorfapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.olynet.olydorfapp.R;
import de.olynet.olydorfapp.cards.BierstubeDailyOfferCard;
import de.olynet.olydorfapp.ui.list.StaticOfferExpandableListAdapter;
import de.olynet.olydorfapp.ui.list.StaticOfferHeader;
import de.olynet.olydorfapp.ui.list.StaticOfferItem;
import it.gmariotti.cardslib.library.view.CardView;

/**
 * @author Simon Domke &lt;simon.domke@olydorf.mhn.de&gt;
 * @version $Id$
 */
public class BierstubeFragment extends BaseFragment
{
    @Override
    public int getTitleResourceId() {
        return R.string.bierstube;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.f_bierstube, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    private void init()
    {
        // TODO: fix content and add all sections plus content

        // first get the view for our header
        View dailyOffers = getLayoutInflater(getArguments()).inflate(R.layout.bs_daily_offers, null);

        BierstubeDailyOfferCard fotdCard = new BierstubeDailyOfferCard(getActivity());
        BierstubeDailyOfferCard dotdCard = new BierstubeDailyOfferCard(getActivity());

        fotdCard.setHeaderTitle(getString(R.string.food_of_the_day));
        dotdCard.setHeaderTitle(getString(R.string.drink_of_the_day));
        dotdCard.disableRowCook();

        fotdCard.init();
        dotdCard.init();

        ((CardView) dailyOffers.findViewById(R.id.bs_card_fotd)).setCard(fotdCard);
        ((CardView) dailyOffers.findViewById(R.id.bs_card_dotd)).setCard(dotdCard);

        // and then deal with the regular, static offers
        List<StaticOfferHeader> models = new ArrayList<StaticOfferHeader>();
        StaticOfferHeader drinks = new StaticOfferHeader("Drinks", R.drawable.mug);
        drinks.setChildren(Arrays.asList(new StaticOfferItem[]{
                new StaticOfferItem("Helles", "3,60€"),
                new StaticOfferItem("Spezi", "5,20€"),
                new StaticOfferItem("Cola", "8,20€"),
                new StaticOfferItem("Apfelschorle", "17,33€")
        }));
        models.add(drinks);
        StaticOfferHeader snacks = new StaticOfferHeader("Snacks", -1);
        snacks.setChildren(Arrays.asList(new StaticOfferItem[]{
                new StaticOfferItem("Kleine Pommes", "3,30€"),
                new StaticOfferItem("Wiener", "1,20€"),
                new StaticOfferItem("Breze", "5,00€"),
                new StaticOfferItem("O'batzdn", "10,00€")
        }));
        models.add(snacks);

        final ExpandableListView listView = (ExpandableListView) getActivity().findViewById(R.id.bs_items_list);
        if(listView != null){
            listView.setGroupIndicator(null);
            listView.addHeaderView(dailyOffers, null, false);
            listView.setAdapter(new StaticOfferExpandableListAdapter(getActivity(), models));
        }
    }
}