package eu.olynet.olydorfapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BierstubeFragment extends ListFragment {

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            HttpGet httpget = new HttpGet(uri[0]);
            httpget.setHeader("Content-type","application/json");

            try {
                response = httpclient.execute(httpget);
                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception e) {
                Log.e("HTTP Connection", e.toString());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("result", result != null ? result : "null");

            /* this should not happen */
            if(result == null)
                return;

            try {
                JSONArray menuItems = new JSONObject(result).getJSONArray("items");

                BaseAdapter adapter = new MealListAdapter(getActivity(), menuItems);
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch(JSONException e){
                Log.e("HTTP Response", e.toString());
            }

            //super.onPostExecute(result);
            //Do anything with response..
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new RequestTask().execute(getString(R.string.web_url));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO: for future versions
    }
}