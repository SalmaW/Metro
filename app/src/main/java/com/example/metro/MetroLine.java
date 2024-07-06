package com.example.metro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MetroLine extends AppCompatActivity {
    TextView noStations, ticketPrice, time, startDirection, endDirection, startStations, endStations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metro_line);

        String start = getIntent().getStringExtra("startStation");
        String end = getIntent().getStringExtra("endStation");
    }
}