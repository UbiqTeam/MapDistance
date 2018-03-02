package com.example.spencer.mapdistance;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.button;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    TextView mTextView;
    TextView mTextView2;
    TextView mTextView3;
    TextView coordinates;
    Polyline mPolyline;
    Polygon area;
    Button clear;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final List<Marker> markers = new ArrayList<>();
        final List<Integer> distances = new ArrayList<>();


        mTextView = findViewById(R.id.textView);
        mTextView2 = findViewById(R.id.textView2);
        coordinates = findViewById(R.id.coordinates);
        mTextView3 = findViewById(R.id.textView3);
        clear = findViewById(R.id.clearButton);
        mTextView2.setText("array size: " + markers.size());
        mTextView.setText("Distance in meters: ");
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(36.057829,-94.176290),18.0f));


        mTextView.setTextColor(Color.RED);
        mTextView2.setTextColor(Color.RED);
        mTextView3.setTextColor(Color.RED);
        coordinates.setTextColor(Color.RED);



        mMap.setOnMapClickListener(new OnMapClickListener() {


            int count = -1;

            @Override
            public void onMapClick(LatLng latLng) {


                clear.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        mMap.clear();
                        markers.clear();
                        mTextView2.setText("array size: " + markers.size());
                        mTextView.setText("Distance in meters: ");
                        mTextView3.setText("lengths: ");
                        coordinates.setText(" ");
                        distances.clear();
                        count = -1;
                    }
                });

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude,latLng.longitude)));
                markers.add(marker);

                mPolyline = mMap.addPolyline(new PolylineOptions().geodesic(true));

                mTextView2.setText("array size: " + markers.size());

                double lat = marker.getPosition().latitude;
                double lon = marker.getPosition().longitude;
                coordinates.setText(lat + " " + lon);

                if (markers.size() > 1 && markers.size() < 5){

                        int distance = (int) SphericalUtil.computeDistanceBetween(markers.get(count).getPosition(),markers.get(count+1).getPosition());
                        distances.add(distance);
                        //mPolyline.setPoints(Arrays.asList(markers.get(i).getPosition(),markers.get(i+1).getPosition()));
                        mTextView.setText("Distance in meters: " + distances.get(count));

                }
                if (markers.size() == 4) {
                    int distance = (int) SphericalUtil.computeDistanceBetween(markers.get(3).getPosition(),markers.get(0).getPosition());
                    distances.add(distance);

                     area = mMap.addPolygon(new PolygonOptions().add(markers.get(0).getPosition(),markers.get(1).getPosition(),markers.get(2).getPosition(),
                                markers.get(3).getPosition()).strokeColor(Color.BLACK));
                    int i = 0;
                        mTextView3.setText("lengths: " + distances.get(i) + ", " + distances.get(i+1) + ", " + distances.get(i+2) + ", " + distances.get(i+3) );

                }
                mMap.addMarker(markerOptions);
                count++;
            }
        });
    }
}
