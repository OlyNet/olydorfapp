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
package eu.olynet.olydorfapp.fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import eu.olynet.olydorfapp.R;
import eu.olynet.olydorfapp.model.NewsItem;
import eu.olynet.olydorfapp.model.OrganizationItem;
import eu.olynet.olydorfapp.utils.UtilsDevice;
import eu.olynet.olydorfapp.utils.UtilsMiscellaneous;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */
public class NewsViewerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String NEWS_KEY = "news_item";
    public static final String ORGANIZATION_KEY = "organization_item";

    private SwipeRefreshLayout mRefreshLayout;

    private NewsItem newsItem = null;
    private OrganizationItem organizationItem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* get the NewsItem */
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.newsItem = arguments.getParcelable(NEWS_KEY);
            this.organizationItem = arguments.getParcelable(ORGANIZATION_KEY);
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
        if (newsItem != null) {
            SimpleDateFormat localFormat
                    = (SimpleDateFormat) android.text.format.DateFormat.getDateFormat(getContext());
            TextView newsDate = (TextView) view.findViewById(R.id.newsViewDate);
            newsDate.setText(localFormat.format(newsItem.getDate()));

            /* Title */
            TextView newsTitle = (TextView) view.findViewById(R.id.newsViewTitle);
            newsTitle.setText(newsItem.getTitle());

            /* Image */
            byte[] image = newsItem.getImage();
            int screenWidth = UtilsDevice.getScreenWidth(getContext());
            Bitmap bitmap = UtilsMiscellaneous.getOptimallyScaledBitmap(image, screenWidth);
            if (bitmap != null) {
                ((ImageView) view.findViewById(R.id.newsViewImage)).setImageBitmap(bitmap);
            }

            /* Content */
            TextView newsContent = (TextView) view.findViewById(R.id.newsViewContent);
            Spanned content;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                content = Html.fromHtml(newsItem.getText(), Html.FROM_HTML_MODE_COMPACT);
            } else {
                content = Html.fromHtml(newsItem.getText());
            }
            newsContent.setText(content);
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
