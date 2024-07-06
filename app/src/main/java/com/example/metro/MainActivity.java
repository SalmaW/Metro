// MainActivity.java
package com.example.metro;

import static com.example.metro.MetroAppFinal.allStations;
import static com.example.metro.MetroAppFinal.line1;
import static com.example.metro.MetroAppFinal.line2;
import static com.example.metro.MetroAppFinal.line3;
import static com.example.metro.MetroAppFinal.line3new;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Spinner startStationSpinner;
    private Spinner endStationSpinner;
    private TextView resultTextView;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStationSpinner = findViewById(R.id.startSpinner);
        endStationSpinner = findViewById(R.id.endSpinner);
        resultTextView = findViewById(R.id.resultText);
        calculateButton = findViewById(R.id.submitButton);

        allStations.addAll(line1);
        allStations.addAll(line2);
        allStations.addAll(line3);
        allStations.addAll(line3new);

        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allStations);
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startStationSpinner.setAdapter(startAdapter);

        ArrayAdapter<String> endAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allStations);
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        endStationSpinner.setAdapter(endAdapter);

//        // Create ArrayAdapter using station_names array from strings.xml
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this,
//                R.array.station_names,
//                android.R.layout.simple_spinner_item
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // Set adapters for spinners
//        startStationSpinner.setAdapter(adapter);
//        endStationSpinner.setAdapter(adapter);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startStation = startStationSpinner.getSelectedItem().toString().trim().toLowerCase();
                String endStation = endStationSpinner.getSelectedItem().toString().trim().toLowerCase();

                List<Integer> startLines = MetroAppFinal.findLines(startStation);
                List<Integer> endLines = MetroAppFinal.findLines(endStation);

                if (!startLines.isEmpty() && !endLines.isEmpty() && !startStation.equals(endStation)) {
                    // Perform the calculation
                    int count = 0;
                    StringBuilder result = new StringBuilder();

                    // Check if start and end stations are on the same physical line
                    Set<Integer> commonLines = new HashSet<>(startLines);
                    commonLines.retainAll(endLines);

                    if (!commonLines.isEmpty()) {
                        int commonLine = commonLines.iterator().next();
                        MetroAppFinal.getDirection();
                        result.append("Take Line ").append(commonLine)
                                .append(" from ").append(startStation.toUpperCase())
                                .append(" to ").append(endStation.toUpperCase()).append("\n");
                        result.append(MetroAppFinal.printStations(startStation, endStation, commonLine));
                        count = MetroAppFinal.routeStations.size();
                    } else {
                        int startLine = startLines.get(0);
                        int endLine = endLines.get(0);
                        ArrayList<String> startLineName = MetroAppFinal.getLineName(startLine);

                        String interchangeStation = MetroAppFinal.getInterchangeStation(startLine, endLine, startStation, startLineName);

                        MetroAppFinal.getDirection();
                        result.append("Take Line ").append(startLine)
                                .append(" from -").append(startStation.toUpperCase())
                                .append("- to -").append(interchangeStation.toUpperCase()).append("- :\n");
                        result.append(MetroAppFinal.printStations(startStation, interchangeStation, startLine));
                        result.append("\nChange to Line ").append(endLine)
                                .append(" and from -").append(interchangeStation.toUpperCase())
                                .append("- to -").append(endStation.toUpperCase()).append("- :\n");
                        MetroAppFinal.printStations(interchangeStation, endStation, endLine);
                        count = MetroAppFinal.routeStations.size() - 1;
                    }

                    result.append("\nNumber Of Stations: ").append(count);
                    int price = MetroAppFinal.getPrice(count);
                    result.append("\nPrice: ").append(price).append(" EGP");
                    result.append("\nEstimated Time: ").append(count * 2).append(" minutes.");

                    resultTextView.setText(result.toString());
                } else {
                    resultTextView.setText("Please enter valid stations.");
                }
            }
        });
    }
}
