package de.olynet.olydorfapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import de.olynet.olydorfapp.R;
import de.olynet.olydorfapp.cards.BierstubeDailyOfferCard;
import de.olynet.olydorfapp.cards.BierstubeDrinksExpandCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
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
        // first get the view for our header
        View dailyOffers = getLayoutInflater(getArguments()).inflate(R.layout.bs_daily_offers, null);

        BierstubeDailyOfferCard fotdCard = new BierstubeDailyOfferCard(getActivity());
        BierstubeDailyOfferCard dotdCard = new BierstubeDailyOfferCard(getActivity());

        fotdCard.setHeaderTitle(getString(R.string.food_of_the_day));
        dotdCard.setHeaderTitle(getString(R.string.drink_of_the_day));

        fotdCard.init();
        dotdCard.init();

        ((CardView) dailyOffers.findViewById(R.id.bs_card_fotd)).setCard(fotdCard);
        ((CardView) dailyOffers.findViewById(R.id.bs_card_dotd)).setCard(dotdCard);


        Card drinks = new Card(getActivity());
        CardHeader drinksHeader = new CardHeader(getActivity());
        drinksHeader.setTitle("Drinks");
        drinksHeader.setButtonExpandVisible(true);
        drinks.addCardHeader(drinksHeader);
        BierstubeDrinksExpandCard drinksExpand = new BierstubeDrinksExpandCard(getActivity());
        drinks.addCardExpand(drinksExpand);
        drinks.setExpanded(true);
        //Animator listener
        drinks.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {
                Toast.makeText(getActivity(), "Expand " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        drinks.setOnCollapseAnimatorEndListener(new Card.OnCollapseAnimatorEndListener() {
            @Override
            public void onCollapseEnd(Card card) {
                Toast.makeText(getActivity(),"Collpase " +card.getCardHeader().getTitle(),Toast.LENGTH_SHORT).show();
            }
        });

        Card snacks = new Card(getActivity());
        CardHeader snacksHeader = new CardHeader(getActivity());
        snacksHeader.setTitle("Snacks");
        snacksHeader.setButtonExpandVisible(true);
        snacks.addCardHeader(snacksHeader);
        snacks.setExpanded(true);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(drinks);
        cards.add(snacks);
        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.bs_static_items_list);
        if (listView != null){
            listView.addHeaderView(dailyOffers);
            listView.setAdapter(mCardArrayAdapter);
        }
    }
}