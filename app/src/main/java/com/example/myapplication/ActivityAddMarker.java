package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityAddMarker extends AppCompatActivity {
    private EditText etLatitude, etLongitude;
    private TextView textView;
    private RecyclerView recyclerView;
    private List<LocationData> locationList;
    private DatabaseHelper databaseHelper;
    private LocationAdapter locationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        etLatitude = findViewById(R.id.etLatitude);
        etLongitude = findViewById(R.id.etLongitude);
        recyclerView = findViewById(R.id.recyclerView);
        Button backButton = findViewById(R.id.backButton);
        textView = findViewById(R.id.textViewStore);
        databaseHelper = new DatabaseHelper(this);

        locationList = new ArrayList<>();
        locationAdapter = new LocationAdapter(locationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(locationAdapter);
        Button addButton = findViewById(R.id.btnAdd);
        /*etLatitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(etLatitude);
            }
        });
        etLongitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showKeyboard(etLongitude);
            }
        });*/
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etLatitude.getText().toString().equals("") || etLongitude.getText().toString().equals("")){
                    Toast.makeText(ActivityAddMarker.this, "Please enter latitude and longitude values", Toast.LENGTH_LONG).show();
                }else{
                    double latitude = Double.parseDouble(etLatitude.getText().toString());
                    double longitude = Double.parseDouble(etLongitude.getText().toString());
                    LocationData locationData = new LocationData(latitude, longitude);
                    long id = databaseHelper.insertLocation(locationData);

                    if (id != -1) {
                        locationData.setId((int) id);
                        locationList.add(locationData);
                        locationAdapter.notifyDataSetChanged();
                    }

                    etLatitude.setText("");
                    etLongitude.setText("");
                   // textView.setVisibility(View.VISIBLE);
                }
            }
        });

        loadLocationData();
        Button openMapButton = findViewById(R.id.openMapButton);
        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAddMarker.this, MapActivity.class);
                intent.putExtra("source", "ActivityAddMarker");
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityAddMarker.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ActivityAddMarker.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadLocationData() {
        locationList.clear();
        locationList.addAll(databaseHelper.getAllLocations());
        locationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
    private void showKeyboard(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}