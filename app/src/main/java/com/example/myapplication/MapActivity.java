package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private List<LocationData> locationList;
    private DatabaseHelper databaseHelper;
    private   Marker marker;
    private TextView textViewAddress;
    private List<Marker> markerList;
    private boolean isMarkerVisible = true;
    private static final long BLINK_INTERVAL = 1000;
    private boolean isAnimating = false;
    private static final long ANIMATION_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Button backButton = findViewById(R.id.backButton);
        databaseHelper = new DatabaseHelper(this);
        locationList = databaseHelper.getAllLocations();
        textViewAddress = findViewById(R.id.addressTextView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String source = intent.getStringExtra("source");
                if (source != null && source.equals("ActivityAddMarker")) {
                    Intent i = new Intent(MapActivity.this, ActivityAddMarker.class);
                    startActivity(i);
                    finish();
                }else if(source != null && source.equals("MainActivity")){
                    Intent i = new Intent(MapActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");
        if (source != null && source.equals("MainActivity")) {
            Intent i = new Intent(MapActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }else{
            super.onBackPressed();
        }
    }
       @Override
       public void onMapReady(@NonNull GoogleMap googleMap) {
           mMap = googleMap;
           markerList = new ArrayList<>();
           /*for (LocationData location : locationList) {
               LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
              //MarkerOptions markerOptions = new MarkerOptions().position(latLng);
             //  mMap.addMarker(markerOptions);
           }*/
           int height = 25;
           int width = 25;
           BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.technoviainfosolutions_logo);
           Bitmap b = bitmapdraw.getBitmap();
           Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

           if (!locationList.isEmpty()) {
               for (int i = 0; i < locationList.size(); i++) {
                   LatLng firstLatLng = new LatLng(locationList.get(i).getLatitude(),
                           locationList.get(i).getLongitude());
                   String s = firstLatLng.toString();
                   Log.d("LatLng ", "-" + s);
                    marker= mMap.addMarker(new MarkerOptions()
                           .position(firstLatLng)
                           .title("Marker " + firstLatLng)
                           .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                           );
                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 1));
                   mMap.moveCamera(CameraUpdateFactory.newLatLng(firstLatLng));
                   mMap.animateCamera(CameraUpdateFactory.zoomIn());
                   markerList.add(marker);
                  //startBlinkingAnimation();
                   startFadingAnimation();

               }
           }
           // Set up marker click listener to display address
           mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
               @Override
               public boolean onMarkerClick(@NonNull Marker marker) {
                   Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                   LatLng markerLatLng = marker.getPosition();
                   try {
                       List<Address> addresses = geocoder.getFromLocation(markerLatLng.latitude, markerLatLng.longitude, 1);
                       if (!addresses.isEmpty()) {
                           String address = addresses.get(0).getAddressLine(0);
                           textViewAddress.setText(address);
                           Toast.makeText(MapActivity.this, "Address: " + address, Toast.LENGTH_LONG).show();
                       }
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   return false;
               }
           });
        /*   mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
               @Override
               public void onMapLongClick(LatLng latLng) {
                   marker.setDraggable(true);
               }
           });*/
         /*  mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
               @Override
               public void onMarkerDragStart(Marker marker) {
                   // Do nothing on drag start
               }

               @Override
               public void onMarkerDrag(Marker marker) {
                   // Do nothing while dragging
               }

               @Override
               public void onMarkerDragEnd(Marker marker) {
                   if (databaseHelper != null) {
                       databaseHelper.deleteLocation(marker.getPosition().latitude, marker.getPosition().longitude);

                       Log.d("Removed LatLng ", "-" + marker.getPosition().latitude +" "
                               + marker.getPosition().longitude);
                   }
                   marker.remove();
                // Remove the marker on drag end
               }
           });*/
           mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
               @Override
               public void onMapLongClick(@NonNull LatLng latLng) {
                   //for (Marker marker : markerList) {
                   if (!markerList.isEmpty()) {
                       marker.remove();
                       markerList.remove(marker);
                       if (databaseHelper != null) {
                           databaseHelper.deleteLocation(marker.getPosition().latitude, marker.getPosition().longitude);
                           String s1 = latLng.toString();
                           Log.d("Removed LatLng from database", "-" + s1);
                       }
                   }
                 //  markerList.clear();
                 /*  if(marker != null) {
                       float[] distance = new float[1];
                       Location.distanceBetween(latLng.latitude, latLng.longitude,
                               marker.getPosition().latitude, marker.getPosition().longitude, distance);
                       if (distance[0] < 500) {
                           marker.remove();
                       if (databaseHelper != null) {
                           databaseHelper.deleteLocation(marker.getPosition().latitude, marker.getPosition().longitude);
                           String s1 = latLng.toString();
                           Log.d("Removed LatLng ", "-" + s1);
                       }
                   }
                   }*/
               }
           });
       }

    private void startBlinkingAnimation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isMarkerVisible) {
                    marker.setVisible(false);
                    isMarkerVisible = false;
                } else {
                    marker.setVisible(true);
                    isMarkerVisible = true;
                }
                handler.postDelayed(this, BLINK_INTERVAL);
            }
        }, BLINK_INTERVAL);
    }
    private void startFadingAnimation() {
        isAnimating = true;
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(marker, "alpha", 0f, 1f);
        fadeIn.setDuration(ANIMATION_DURATION / 2); // Fade-in duration
        fadeIn.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(marker, "alpha", 1f, 0f);
        fadeOut.setDuration(ANIMATION_DURATION / 2); // Fade-out duration
        fadeOut.setInterpolator(new AccelerateInterpolator());

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(fadeIn, fadeOut);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (isAnimating) {
                    animatorSet.start();
                }
            }
        });
        animatorSet.start();
    }
    protected void onDestroy() {
        super.onDestroy();
        isAnimating = false;
    }
   /* @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        marker.remove();
        if (databaseHelper != null) {
            databaseHelper.deleteLocation(marker.getPosition().latitude, marker.getPosition().longitude);
        }

    }*/
  /*  @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Fetch location data from SQLite database
        List<LocationData> locationList = databaseHelper.getAllLocations();

        // Add markers for each location in the list
        for (LocationData location : locationList) {
            Log.d("LocationData", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());
            //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng latLng = new LatLng(-34.0,151.0);
            String s= latLng.toString();
            Log.d("LatLng ",""+s);
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title"));
        }

       *//* for (int i = 0; i < locationList.size(); i++) {
            final  LatLng latLng = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker Title"));
        }*//*


            // Move the camera to include all markers
        if (!locationList.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LocationData location : locationList) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }*/
}
