package eu.olynet.olydorfapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.NewsItem;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class NewsViewerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ITEM_KEY = "news_item";

    private SwipeRefreshLayout mRefreshLayout;
    private NewsItem item = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* get the NewsItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.item = arguments.getParcelable(ITEM_KEY);
        } else {
            Log.e("NewsViewerFrag", "arguments is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* inflate the layout for this Fragment */
        View view = inflater.inflate(R.layout.fragment_news_viewer, container, false);

        /* set SwipeRefreshLayout */
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_view_fragment);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setEnabled(false); /* disable for now */

        /* set the data */
        if (item != null) {
            /* Title */
            TextView newsTitle = (TextView) view.findViewById(R.id.newsViewTitle);
            newsTitle.setText(item.getTitle());

            /* Image */
            ImageView newsImage = (ImageView) view.findViewById(R.id.newsViewImage);
            byte[] image = item.getImage();
            if (image == null || image.length <= 0) { /* fall back to Organization image */
                image = item.getOrganization().getLogo();
            }
            if (image != null && image.length > 0) { /* finally set the image if one is available */
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                if (imageBitmap == null) {
                    newsImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
                } else {
                    DisplayMetrics dm = new DisplayMetrics();
                    WindowManager windowManager = (WindowManager)
                            getContext().getSystemService(Context.WINDOW_SERVICE);
                    windowManager.getDefaultDisplay().getMetrics(dm);
                    newsImage.setImageBitmap(imageBitmap);
                }
            } else {
                newsImage.setImageResource(R.drawable.ic_account_circle_white_64dp);
            }

            /* Content */
            TextView newsContent = (TextView) view.findViewById(R.id.newsViewContent);
            newsContent.setText(item.getText());
        }

        /* return the View */
        return view;
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setEnabled(false);
        // TODO: implement replacing of this Fragment
    }

}
