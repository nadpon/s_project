package kmitl.cs.s_project;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class MapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        onBackPressed();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        GoogleMap mMap,mMap2;
        Marker mMarker,mMarker2;
        LocationManager lm;
        double lat, lng;
        String latitude,longitude;
        Button submit_button,cancel_button;
        SupportMapFragment mapFragment,mapFragment2;
        LocationListener listener;
        ProgressDialog pDialog;
        InputStream is;
        String js_result;
        JSONObject jsonObject;
        int RESULT_NOT = 1;
        String postID;
        String postIm;
        String postName;
        String date;
        String status;
        LayoutInflater layoutInflater = (LayoutInflater) MapActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.mapinfo,null);

        public PlaceholderFragment() {
        }

        public class getMap extends AsyncTask<Void, Void, String>{
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(MapActivity.this);
                pDialog.setMessage("กรุณารอสักครู่ ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(Void... params) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://reportdatacenter.esy.es/sendMap.php");
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    while ((line = reader.readLine()) != null){
                        sb.append(line+ "\n");
                    }
                    is.close();
                    js_result = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                try {
                    JSONArray jsonArray = new JSONArray(js_result);
                    for (int i=0;i<jsonArray.length();i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        Double gpsLatitude = jsonObject.getDouble("gpsLatitude");
                        Double gpsLongitude = jsonObject.getDouble("gpsLongitude");
                        postID = String.valueOf(jsonObject.getInt("postID"));
                        postIm = jsonObject.getString("postImage");
                        postName = jsonObject.getString("postName");
                        date = jsonObject.getString("postDate");
                        status = jsonObject.getString("statusName");
                        mMarker2 = mMap2.addMarker(new MarkerOptions()
                                .position(new LatLng(gpsLatitude, gpsLongitude))
                                .title(postName)
                                .snippet("\n"
                                        + "แจ้งเมื่อ : " + date + "\n" + "สถานะ : " + status)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                        //set MapInfo from post
                        showMapInfo();

                        //click map info to postInfoActivity
                        clickMapInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pDialog!=null)
                    pDialog.dismiss();

                // Alert to user before mark a map
                final Dialog dialog = new Dialog(MapActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.map_dialog);
                dialog.setCancelable(false);
                dialog.show();
                Button ok = (Button) dialog.findViewById(R.id.ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            private void clickMapInfo() {
                mMap2.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(MapActivity.this,PostInfoActivity.class);
                        intent.putExtra("postName",marker.getTitle());
                        startActivity(intent);
                    }
                });
            }

            private void showMapInfo() {
                mMap2.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        TextView postTitle = (TextView) view.findViewById(R.id.title);
                        postTitle.setText(marker.getTitle());

                        TextView postInfo = (TextView) view.findViewById(R.id.snippet);
                        postInfo.setText(marker.getSnippet());

                        return view;
                    }
                });
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude());
                    lat = location.getLatitude();
                    lng = location.getLongitude();

                    if(mMarker != null)
                        mMarker.remove();

                    mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 17));
                    mMarker.hideInfoWindow();
                    mMarker.setDraggable(true);

                    lm.removeUpdates(listener);

                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            lm.removeUpdates(listener);
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            lat = marker.getPosition().latitude;
                            lng = marker.getPosition().longitude;
                        }
                    });
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            };
        }

        @Override
        public void onResume() {
            super.onResume();
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            boolean isNetwork =
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean isGPS =
                    lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(isNetwork) {
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                        , 0, 10, listener);
                Location loc = lm.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
                if(loc != null) {
                    lat = loc.getLatitude();
                    lng = loc.getLongitude();
                }
            }

            if(isGPS) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER
                        , 0, 10, listener);
                Location loc = lm.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
                if(loc != null) {
                    lat = loc.getLatitude();
                    lng = loc.getLongitude();
                }
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            lm.removeUpdates(listener);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            //---------init--------------
            submit_button = (Button) rootView.findViewById(R.id.submit_Button);
            cancel_button = (Button) rootView.findViewById(R.id.cancel_Button);
            FragmentManager fm = getChildFragmentManager();
            mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
            mapFragment2 = (SupportMapFragment) fm.findFragmentById(R.id.map);
            mMap2 = mapFragment2.getMap();

            new getMap().execute();

            // clickSubmitButton
            submit_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    longitude = String.valueOf(lng);
                    latitude = String.valueOf(lat);
                    Intent intent = new Intent(MapActivity.this,PostActivity.class);
                    intent.putExtra("lat",latitude);
                    intent.putExtra("lng",longitude);
                    startActivity(intent);
                }
            });

            // clickCancelButton
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            return rootView;
        }
    }
}
