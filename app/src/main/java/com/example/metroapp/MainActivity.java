package com.example.metroapp;

import static com.example.metroapp.Constants.line1;
import static com.example.metroapp.Constants.line2;
import static com.example.metroapp.Constants.line3;
import static com.example.metroapp.Constants.line3new;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Spinner startStationsSpinner, endStationsSpinner;
    Button submitButton, soundButton;
    TextView resultText, clear;
    ScrollView scrollView;

    TextToSpeech tts;

    public ArrayList<String> routeStations = new ArrayList<>();

    String startDirection = "";
    String endDirection = "";
    byte counter = 0;
    int firstCount = 0;
    String interchangeStation = "";
    MetroPathFinder mpf;
    MetroDirectionFinder mdf;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clear = findViewById(R.id.clear);
        submitButton = findViewById(R.id.submit);
        soundButton = findViewById(R.id.sound);
        startStationsSpinner = findViewById(R.id.spinnerStart);
        endStationsSpinner = findViewById(R.id.spinnerEnd);
        resultText = findViewById(R.id.resultText);
        scrollView = findViewById(R.id.scrollView);
        tts=new TextToSpeech(this,this);

        fillSpinner(startStationsSpinner);
        fillSpinner(endStationsSpinner);


    }

    public void submit(View view) {

        String startStationAnswer = startStationsSpinner.getSelectedItem().toString();
        String endStationAnswer = endStationsSpinner.getSelectedItem().toString();
        if (startStationAnswer.equalsIgnoreCase("Select station") || endStationAnswer.equalsIgnoreCase("Select station")) {
            Toast.makeText(this, "Please select station", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startStationAnswer.equalsIgnoreCase(endStationAnswer)) {
            Toast.makeText(this, "Please select another station", Toast.LENGTH_SHORT).show();
            resultText.setText("");
            return;
        }
        getAllData(startStationAnswer, endStationAnswer);

        if (!interchangeStation.equals("")) {
            soundButton.setEnabled(true);
        }

        // Scroll to the top of the ScrollView
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));

        counter = 0;
        firstCount = 0;
        routeStations.clear();
    }

    @Override
    protected void onStop() {

        tts.stop();
        tts.shutdown();

        if (!resultText.getText().toString().isEmpty()){
            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("allData", resultText.getText().toString());
            editor.putString("startSpinnerSelected", startStationsSpinner.getSelectedItem().toString());
            editor.putString("endSpinnerSelected", endStationsSpinner.getSelectedItem().toString());
            editor.apply();
        }
        super.onStop();
    }

    void getAllData(String startStationAnswer, String endStationAnswer) {
        mdf = new MetroDirectionFinder();
        startDirection = mdf.getDirection(startStationAnswer, endStationAnswer)[0];
        endDirection = mdf.getDirection(startStationAnswer, endStationAnswer)[1];

        if (!endDirection.equals(""))
            resultText.setText("Start Direction: " + startDirection + ", End Direction: " + endDirection);
        else
            resultText.setText("Direction: " + startDirection);

        resultText.append("\n");

        List<Integer> startLines = findLines(startStationAnswer);
        List<Integer> endLines = findLines(endStationAnswer);

        // Check if start and end stations are on the same physical line
        Set<Integer> commonLines = new HashSet<>(startLines);
        commonLines.retainAll(endLines);

        if (!commonLines.isEmpty()) {
            int commonLine = commonLines.iterator().next();
            resultText.append("\nTake Line " + commonLine + " from " + startStationAnswer.toUpperCase() + " to " + endStationAnswer.toUpperCase());
             if (connectWithLine3(startStationAnswer, endStationAnswer)) {
                printStations(startStationAnswer, endStationAnswer, commonLine);
            }
        } else {
            int startLine = startLines.get(0);
            int endLine = endLines.get(0);
            ArrayList<String> startLineName = getLineName(startLine);

            interchangeStation = getInterchangeStation(startLine, endLine, startStationAnswer, startLineName);

            resultText.append("\n**Take Line " + startLine + " from -" + startStationAnswer.toUpperCase() + "- to -" + interchangeStation.toUpperCase() + "- :");
            Log.i("interchangeStation", interchangeStation);

                if (connectWithLine3(startStationAnswer, interchangeStation)) {
                    printStations(startStationAnswer, interchangeStation, startLine);
                }

            firstCount += routeStations.size();
            routeStations.clear();
            if (endLine == 4)
                endLine = 3;
            resultText.append("\n\n**Change to Line " + endLine + " and from -" + interchangeStation.toUpperCase() + "- to -" + endStationAnswer.toUpperCase() + "- :");
            if (endStationsSpinner.toString().equalsIgnoreCase("cairo university")) {
                getStations(interchangeStation, endStationAnswer, endLine);
                int size = routeStations.size();
                routeStations.clear();
                interchangeStation = "nasser";
                if (connectWithLine3(interchangeStation, endStationAnswer)) {
                    getStations(interchangeStation, endStationAnswer, endLine);
                }
                if (size < routeStations.size()){
                    routeStations.clear();
                    interchangeStation = getInterchangeStation(startLine, endLine, startStationAnswer, startLineName);
                    printStations(interchangeStation, endStationAnswer, endLine);
                }
            }else {
                if (connectWithLine3(interchangeStation, endStationAnswer)) {
                    printStations(interchangeStation, endStationAnswer, endLine);
                }
            }

        }

        resultText.append("\n");
        // Number Of Stations
        int count = firstCount + routeStations.size() - 1;
        resultText.append("\nNumber Of Stations: " + count);
        // Price
        int price = getPrice(count);
        resultText.append("\nPrice: " + price + " EGP");
        // Time
        String time = calculateEstimatedTime(count);
        resultText.append("\nTime: " + time);

        getAllPaths(startStationAnswer, endStationAnswer);
    }

    void printStations(String startStationAnswer, String endStationAnswer, int line) {
        getStations(startStationAnswer, endStationAnswer, line);
        resultText.append("\n");
        for (String station : routeStations) {
            if (station.equalsIgnoreCase(routeStations.get(routeStations.size() - 1)))
                resultText.append(station);
            else
                resultText.append(station + " -> ");
        }
    }

    private void fillSpinner(Spinner spinner) {
        ArrayList<String> items = new ArrayList<>();
        items.add(0, "Select station");
        items.addAll(line1);
        items.addAll(line2);
        items.addAll(line3);
        items.addAll(line3new);

        //items->adapter->spinner
        ArrayAdapter adapter = new ArrayAdapter(this
                , android.R.layout.simple_list_item_1, items);
        spinner.setAdapter(adapter);

        pref = getSharedPreferences("data",MODE_PRIVATE);
//        String data = pref.getString("allData","");
        String startSpinnerSelected = pref.getString("startSpinnerSelected","");
        String endSpinnerSelected = pref.getString("endSpinnerSelected","");
        if (!startSpinnerSelected.isEmpty() && !endSpinnerSelected.isEmpty()) {
            startStationsSpinner.setSelection(adapter.getPosition(startSpinnerSelected));
            endStationsSpinner.setSelection(adapter.getPosition(endSpinnerSelected));
        }

    }

    void getAllPaths(String startStation, String endStation) {
        mpf = new MetroPathFinder();
        List<List<String>> allPaths = mpf.findAllPaths(startStation, endStation);
        resultText.append("\n\nAll possible paths from " + startStation.toUpperCase() + " to " + endStation.toUpperCase() + ":");
        int min = allPaths.get(0).size();
        int shortIndex = 0;
        for (List<String> path : allPaths) {
            if (path.contains("kit kat")&&path.contains("cairo university")){
                if ((path.contains("tawfikia")&&path.contains("wadi el nile")&&path.contains("gamet el dowel")
                &&path.contains("boulak el dakrour")&&path.contains("cairo university"))){
                    resultText.append("\n\n##" + path);
                }
            }else if (!path.contains("kit kat")&&!path.contains("cairo university")){
                resultText.append("\n\n##" + path);
            }
//            count = path.size();
            if (path.size() < min) {
                min = path.size();
                shortIndex = allPaths.indexOf(path);
            }
            resultText.append("\n\n##" + path);
        }
        resultText.append("\n\n**Shortest Route: " + allPaths.get(shortIndex));

    }

    public List<Integer> findLines(String station) {
        List<Integer> lines = new ArrayList<>();
        if (Constants.line1.contains(station)) {
            lines.add(1);
        }
        if (line2.contains(station)) {
            lines.add(2);
        }
        if (line3.contains(station)) {
            lines.add(3);
        }
        if (line3new.contains(station)) {
            lines.add(4);
        }
        return lines;  // Return all lines containing the station
    }

    public String shortestPath(int startLine, int endLine, String startStation, ArrayList<String> startLineName) {
        String interchangeStation = "";

        if ((startLine == 1 && endLine == 2) || (startLine == 2 && endLine == 1)) {
            int startStationIndex = startLineName.indexOf(startStation);
            int shohadaIndex = startLineName.indexOf("al shohadaa");

            int interchangeStation1 = Math.abs(startStationIndex - shohadaIndex);
            int sadatIndex = startLineName.indexOf("sadat");
            int interchangeStation2 = Math.abs(startStationIndex - sadatIndex);

            if (interchangeStation1 < interchangeStation2) {
                interchangeStation = "al shohadaa";
            }
            else {
                interchangeStation = "sadat";
            }
        }

        if ((startLine == 2 && (endLine == 3||endLine == 4)) || ((startLine == 3||startLine == 4) && endLine == 2)) {
            int startStationIndex = startLineName.indexOf(startStation);
            int atabaIndex = startLineName.indexOf("ataba");

            int interchangeStation1 = Math.abs(startStationIndex - atabaIndex);
            int cuIndex = startLineName.indexOf("cairo university");
            int interchangeStation2 = Math.abs(startStationIndex - cuIndex);

            if (interchangeStation1 < interchangeStation2) {
                interchangeStation = "ataba";
            }else {
                interchangeStation = "cairo university";
            }
        }
        return interchangeStation;
    }

    public String getInterchangeStation(int startLine, int endLine, String startStation, ArrayList<String> startLineName) {
        // Interchange stations for each line
        String interchangeStations = "";
        if ((startLine == 1 || endLine == 1) && (startLine == 2 || endLine == 2)) {
            interchangeStations = shortestPath(startLine, endLine, startStation, startLineName);//1&2
        } else if ((startLine == 2 || endLine == 2) && ((startLine == 3 || endLine == 3)||((startLine == 4 || endLine == 4)))) {
            interchangeStations = shortestPath(startLine, endLine, startStation, startLineName);//2&3
        } else if ((startLine == 1 || endLine == 1) && (startLine == 3 || endLine == 3)) {
            interchangeStations = "nasser";//1&3
        }

        return interchangeStations;
    }

    public ArrayList<String> getLineName(int line) {
        switch (line) {
            case 1:
                return line1;
            case 2:
                return line2;
            case 3:
                return line3;
            case 4:
                return line3new;
            default:
                return null;
        }
    }

    boolean connectWithLine3(String startStation, String endStation) {
        String kitKatStation = "kit kat";
        String tawfikiaStation = "tawfikia";

        if (startStation.equalsIgnoreCase("cairo university") && (!line3new.contains(endStation) && !line3.contains(endStation))) {
            return true;
        }
        if (endStation.equalsIgnoreCase("cairo university") && (!line3new.contains(startStation) && !line3.contains(startStation))) {
            return true;
        }

        if (line3new.contains(startStation) && line3new.contains(endStation)){
            startDirection = "Adly Mansour";
            printStations(startStation, endStation, 4);
            return false;
        }

        if (line3new.contains(endStation)) {
            printStations(startStation, kitKatStation, 3);
            firstCount += routeStations.size();
            routeStations.clear();
            resultText.append(" -> ");
            printStations(tawfikiaStation, endStation, 4);
            return false;
        }
        if (line3new.contains(startStation)) {
            printStations(startStation, tawfikiaStation, 4);
            firstCount += routeStations.size();
            routeStations.clear();
            resultText.append(" -> ");
            printStations(kitKatStation, endStation, 3);
            return false;
        }

        return true;
    }



    String calculateEstimatedTime(int count) {
        byte arrivalTime = (byte) (count * 2);
        while (arrivalTime > 60) {
            counter++;
            arrivalTime = (byte) (arrivalTime - 60);
        }
        if (counter > 0) {
            return (counter + " Hr and " + arrivalTime + " Min");
        }

        return (arrivalTime + " min");
    }

    public int getPrice(int numberOfStations) {
        if (numberOfStations <= 9) {
            return 6;
        } else if (numberOfStations <= 16) {
            return 8;
        } else if (numberOfStations <= 23) {
            return 12;
        } else {
            return 15;
        }
    }

    public void getStations(String start, String end, int line) {
        ArrayList<String> lineStations = getLineName(line);
        int startIndex = lineStations.indexOf(start);
        int endIndex = lineStations.indexOf(end);

        if (startIndex <= endIndex) {
            for (int i = startIndex; i <= endIndex; i++) {
                routeStations.add(lineStations.get(i));
            }
        } else {
            for (int i = startIndex; i >= endIndex; i--) {
                routeStations.add(lineStations.get(i));
            }
        }
    }

//    public List<String> getNeighbors(String station) {
//        List<String> neighbors = new ArrayList<>();
//
//        // Check neighbors on line 1
//        if (line1.contains(station)) {
//            int index = line1.indexOf(station);
//            if (index > 0) {
//                neighbors.add(line1.get(index - 1));
//            }
//            if (index < line1.size() - 1) {
//                neighbors.add(line1.get(index + 1));
//            }
//        }
//
//        // Check neighbors on line 2
//        if (line2.contains(station)) {
//            int index = line2.indexOf(station);
//            if (index > 0) {
//                neighbors.add(line2.get(index - 1));
//            }
//            if (index < line2.size() - 1) {
//                neighbors.add(line2.get(index + 1));
//            }
//        }
//
//        // Check neighbors on line 3
//        if (line3.contains(station)) {
//            int index = line3.indexOf(station);
//            if (index > 0) {
//                neighbors.add(line3.get(index - 1));
//            }
//            if (index < line3.size() - 1) {
//                neighbors.add(line3.get(index + 1));
//            }
//
//            // Check if this station is "kit kat" to add neighbors from line3new
//            if (station.equals("kit kat")) {
//                neighbors.addAll(line3new); // Add all stations from line3new as neighbors
//            }
//        }
//
//
//        // Check neighbors on line 3new
//        if (line3new.contains(station)) {
//            int index = line3new.indexOf(station);
//            Log.i("index", index+"");
//            if (index > 0) {
//                neighbors.add(line3new.get(index - 1));
//            }
//            if (index < line3new.size() - 1) {
//                neighbors.add(line3new.get(index + 1));
//            }
//        }
//
//        return neighbors;
//    }


    // Helper method to get neighbors from line3New
//    private List<String> getNeighborsFromLine3New(String station) {
//        List<String> neighbors = new ArrayList<>();
//
//        int index = line3new.indexOf(station);
//        if (index > 0) {
//            neighbors.add(line3new.get(index - 1));
//        }
//        if (index < line3new.size() - 1) {
//            neighbors.add(line3new.get(index + 1));
//        }
//
//        return neighbors;
//    }

    public void clear(View view) {
        resultText.setText("");
        startStationsSpinner.setSelection(0);
        endStationsSpinner.setSelection(0);

        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("allData", resultText.getText().toString());
        editor.putString("startSpinnerSelected", startStationsSpinner.getSelectedItem().toString());
        editor.putString("endSpinnerSelected", endStationsSpinner.getSelectedItem().toString());
        editor.apply();
    }

    @Override
    public void onInit(int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void sound(View view) {
        tts.speak(interchangeStation,TextToSpeech.QUEUE_FLUSH,null,null);
    }
}