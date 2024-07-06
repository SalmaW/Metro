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

        startDirection = findViewById(R.id.startDirection);
        endDirection = findViewById(R.id.endDirection);
        noStations = findViewById(R.id.noStations);
        ticketPrice = findViewById(R.id.ticketPrice);
        time = findViewById(R.id.time);
        startStations = findViewById(R.id.startStations);
        endStations = findViewById(R.id.endStations);

        startDirection.setText(start);
        endDirection.setText(end);
    }
}