package kmitl.cs.s_project;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A placeholder fragment containing a simple view.
 */
public class PostMapActivityFragment extends Fragment {
    GoogleMap mMap;
    Marker mMarker;
    SupportMapFragment mapFragment;
    double lat,lng;

    public PostMapActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_post_map, container, false);
        //รับค่า
        lat = Double.parseDouble(getActivity().getIntent().getStringExtra("lat"));
        lng = Double.parseDouble(getActivity().getIntent().getStringExtra("lng"));

        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mMap = mapFragment.getMap();

        mMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),15));

        return rootView;
    }
}
