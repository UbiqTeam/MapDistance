package com.example.spencer.mapdistance;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
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
import java.util.List;
import java.math.BigDecimal;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    TextView areaText;
    TextView markerCountText;
    TextView lengthsText;
    TextView coordinatesText;
    Polyline mPolyline;
    Polygon area;
    Button clear;
    Button drop;
    Button setMarker;
    Switch mode;
    private GoogleMap mMap;

    LocationManager locationManager;
    Location location;

    final List<Marker> markers = new ArrayList<>();
    final List<MarkerOptions> markerOptionsList = new ArrayList<>();
    final List<Double> distances = new ArrayList<>();
    final List<LatLng> coorList = new ArrayList<>();

    int count = -1;
    double totalArea = 1.0;
    Boolean switchState = true;

    public void handleMarkers(MarkerOptions markerOptions, Marker marker) {

        markers.add(marker);
        mPolyline = mMap.addPolyline(new PolylineOptions().geodesic(true));
        markerCountText.setText("Marker Count: " + markers.size());
        coordinatesText.setText(coorList.toString());

        if (markers.size() == 1) {
            mMap.clear();
        }

        if (markers.size() > 1) {
            double distance = SphericalUtil.computeDistanceBetween(markers.get(count).getPosition(), markers.get(count + 1).getPosition());
            distances.add(distance);
            areaText.setText("Area (sq m): " + totalArea);
        }

        if (markers.size() == 4) {
            double distance = SphericalUtil.computeDistanceBetween(markers.get(3).getPosition(), markers.get(0).getPosition());
            distances.add(distance);
            area = mMap.addPolygon(new PolygonOptions().add(markers.get(0).getPosition(), markers.get(1).getPosition(), markers.get(2).getPosition(),
                    markers.get(3).getPosition()).strokeColor(Color.BLACK));
            int i = 0;
            lengthsText.setText("lengths: " + distances.get(i) + ", " + distances.get(i + 1) + ", " + distances.get(i + 2) + ", " + distances.get(i + 3));
            markers.clear();
            totalArea = computeArea(distances);
            distances.clear();
            coorList.clear();
            count = -2;
            areaText.setText("Area (sq m): " + totalArea);
        } else {
            lengthsText.setText("");
        }

        mMap.addMarker(markerOptions);
        count++;
    }

    public double computeArea(List<Double> distances) {
        BigDecimal tmp = new BigDecimal(0);
        BigDecimal TWO = new BigDecimal(2);

        double a = distances.get(0);
        double b = distances.get(1);
        double c = distances.get(2);
        double d = distances.get(3);

        for (Double val : distances) {
            tmp = tmp.add(new BigDecimal(val.intValue()));
        }

        double s = tmp.divide(TWO).doubleValue();
        return Math.sqrt((s - a) * (s - b) * (s - c) * (s - d));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        areaText = findViewById(R.id.areaText);
        markerCountText = findViewById(R.id.markerCountText);
        coordinatesText = findViewById(R.id.coordinatesText);
        lengthsText = findViewById(R.id.lengthsText);
        clear = findViewById(R.id.clearButton);
        drop = findViewById(R.id.drop);
        setMarker = findViewById(R.id.setMarkerButton);
        mode = findViewById(R.id.modeSwitch);
        markerCountText.setText("Marker Count: " + markers.size());
        areaText.setText("Area (sq m): ");
        mMap.setMapType(mMap.MAP_TYPE_SATELLITE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mMap.setMyLocationEnabled(true);
        } else {
            markerCountText.setText("no GPS permission");
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18.0f));

        areaText.setTextColor(Color.RED);
        markerCountText.setTextColor(Color.RED);
        lengthsText.setTextColor(Color.RED);
        coordinatesText.setTextColor(Color.RED);
        mode.setTextColor(Color.BLACK);
        mode.setBackgroundColor(Color.LTGRAY);

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mode.isChecked()) {
                    drop.setVisibility(View.VISIBLE);
                    setMarker.setVisibility(View.GONE);
                } else {
                    drop.setVisibility(View.GONE);
                    setMarker.setVisibility(View.VISIBLE);
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMap.clear();
                markers.clear();
                markerCountText.setText("Marker Count: " + markers.size());
                areaText.setText("Area (sq m): ");
                lengthsText.setText(" ");
                coordinatesText.setText(" ");
                distances.clear();
                coorList.clear();
                count = -1;
                totalArea = 1;
                switchState = mode.isChecked();
            }
        });

        drop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!mode.isChecked()) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    try {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    } catch (SecurityException e) {
                        markerCountText.setText("no GPS permission");
                    }
                    LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    markerOptions.position(newLatLng);
                    coorList.add(newLatLng);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).draggable(true));
                    handleMarkers(markerOptions, marker);
                }

            }
        });



            mMap.setOnMapClickListener(new OnMapClickListener() {
                Boolean button = false;
                Marker marker = null;

                @Override
                public void onMapClick(final LatLng latLng) {
                    if (mode.isChecked()) {
                        final List<Marker> tmpMarkers = new ArrayList<>();
                        final MarkerOptions markerOptions = new MarkerOptions();

                        if(!button) {
                            markerOptions.position(latLng);
                            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).draggable(true));
                            button = true;
                        }


                        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {

                            }

                            @SuppressWarnings("unchecked")
                            @Override
                            public void onMarkerDragEnd(Marker arg0) {

                                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude)).draggable(true));
                                LatLng newLatLng = new LatLng(arg0.getPosition().latitude,arg0.getPosition().longitude);
                                markerOptions.position(newLatLng);

                            }

                            @Override
                            public void onMarkerDrag(Marker arg0) {
                            }
                        });



                        setMarker.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                tmpMarkers.add(marker);
                                markerOptionsList.add(markerOptions);
                                LatLng tmp = markerOptions.getPosition();
                                coorList.add(tmp);
                                if(button) {
                                    handleMarkers(markerOptions, marker);

                                }
                               button = false;
                            }
                        });
                    }
                }
            });
    }
}
