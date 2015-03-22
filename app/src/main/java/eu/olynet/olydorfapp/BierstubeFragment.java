package eu.olynet.olydorfapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

class MenuItem{
    String date;
    String price;
    String cook;
    String meal;
}

public class BierstubeFragment extends ListFragment {

    class MyArrayAdapter extends ArrayAdapter<MenuItem> {
        private final Context context;
        private final MenuItem[] values;

        public MyArrayAdapter(Context context, MenuItem[] values) {
            super(context, R.layout.menuitem, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.menuitem,parent,false);

            TextView date = (TextView) rowView.findViewById(R.id.date);
            TextView meal = (TextView) rowView.findViewById(R.id.meal);
            TextView price = (TextView) rowView.findViewById(R.id.price);
            TextView cook = (TextView) rowView.findViewById(R.id.cook);

            date.setText(values[position].date);
            meal.setText(values[position].meal);
            price.setText(values[position].price);
            cook.setText(values[position].cook);

            return rowView;
        }
    }

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
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                Log.v("result",result);
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONArray menuItems = obj.getJSONArray("items");

                    MenuItem[] items = new MenuItem[menuItems.length()];


                    for (int i = 0; i < menuItems.length(); i++) {
                        try {
                            JSONObject oneObject = menuItems.getJSONObject(i);
                            // Pulling items from the array


                            MenuItem item = new MenuItem();
                            item.date = oneObject.getString("date");
                            item.cook = oneObject.getString("cook");
                            item.meal = oneObject.getString("meal");
                            item.price = oneObject.getString("price");

                            items[i]=item;

                        } catch (JSONException e) {
                            Log.e("jsonerror",e.toString());
                        }
                    }

                    MyArrayAdapter adapter = new MyArrayAdapter(getActivity(), items);
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }catch(JSONException e){
                    Log.e("jsonerror",e.toString());
                }



            }else{
                Log.v("result","null");
            }

            //super.onPostExecute(result);
            //Do anything with response..
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new RequestTask().execute("http://192.168.0.3/menu");

//        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//                "Linux", "OS/2" };
//        MyArrayAdapter adapter = new MyArrayAdapter(getActivity(), values);
//        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // do something with the data
    }
}