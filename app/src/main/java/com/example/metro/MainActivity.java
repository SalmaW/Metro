package com.example.metro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class MainActivity extends AppCompatActivity {

    RadioGroup startRadioGroup, endRadioGroup;
    Spinner startStationsSpinner, endStationsSpinner;
    Button submitButton;
    TextView resultText;
    ScrollView scrollView;

    public ArrayList<String> routeStations = new ArrayList<>();
    ArrayList<String> line1 = new ArrayList<>(Arrays.asList(
            "Select station",
            "helwan", "ain helwan", "helwan university", "wadi hof", "hadayek helwan",
            "el-maasara", "tora el-asmant", "kozzika", "tora el-balad", "sakanat el-maadi", "el-maadi",
            "hadayek el-maadi", "dar el-salam", "el-zahraa", "mar girgis", "el-malek el-saleh",
            "al-sayeda zeinab", "saad zaghloul", "sadat", "nasser", "orabi", "al shohadaa",
            "ghamra", "el-demerdash", "manshiet el-sadr", "kobri el-qobba", "hammamat el-qobba",
            "saray el-qobba", "hadayek el-zaitoun", "helmeyet el-zaitoun", "el-matareyya",
            "ain shams", "ezbet el-nakhl", "el-marg", "new el-marg"
    ));
    ArrayList<String> line2 = new ArrayList<>(Arrays.asList(
            "Select station",
            "el mounib", "sakiat mekki", "omm el misryeen", "giza", "faisal",
            "cairo university", "bohooth", "dokki", "opera", "sadat", "naguib",
            "ataba", "al shohadaa", "massara", "road el-farag", "sainte teresa",
            "khalafawy", "mezallat", "koliet el-zeraa", "shobra el kheima"
    ));
    ArrayList<String> line3 = new ArrayList<>(Arrays.asList(
            "Select station",
            "adly mansour", "hikestep", "omar ibn al khattab", "kebaa", "hisham barakat",
            "el nozha", "el shames club", "alf maskan", "heliopolis", "haroun",
            "al ahram", "koleyet el banat", "cairo stadium", "fair zone", "abbassiya",
            "abdou pasha", "el geish", "bab el shaaria", "ataba", "nasser",
            "maspero", "zamalek", "kit kat", "sudan st.", "imbaba",
            "el bohy", "el qawmia", "ring road", "rod el farag corr"
    ));

    ArrayList<String> line3new = new ArrayList<>(Arrays.asList(
            "tawfikia", "wadi el nile", "gamet el dowel", "boulak el dakrour",
            "cairo university"
    ));
    String startDirection = "";
    String endDirection = "";
    byte counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitButton = findViewById(R.id.submit);
        startRadioGroup = findViewById(R.id.radioGroupStart);
        endRadioGroup = findViewById(R.id.radioGroupEnd);
        startStationsSpinner = findViewById(R.id.spinnerStart);
        endStationsSpinner = findViewById(R.id.spinnerEnd);
        resultText = findViewById(R.id.resultText);
        scrollView = findViewById(R.id.scrollView);

        checkLine(startRadioGroup, startStationsSpinner);
        checkLine(endRadioGroup, endStationsSpinner);
    }

    private void checkLine(RadioGroup radioGroup, Spinner spinner){
        // Uncheck or reset the radio buttons initially
        radioGroup.clearCheck();

        // Add the Listener to the RadioGroup
        // The flow will come here when
        // any of the radio buttons in the radioGroup
        // has been clicked
        // Check which radio button has been clicked
        radioGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    // Get the selected Radio Button
                    RadioButton radioButton = group.findViewById(checkedId);

                    fillSpinner(group, spinner);

                });
    }

    public void submit(View view) {
        int startSelectedId = startRadioGroup.getCheckedRadioButtonId();
        int endSelectedId = endRadioGroup.getCheckedRadioButtonId();
        if (startSelectedId == -1 || endSelectedId == -1) {
            Toast.makeText(this,
                            "Please select line",
                            Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String startStationAnswer = startStationsSpinner.getSelectedItem().toString();
        String endStationAnswer = endStationsSpinner.getSelectedItem().toString();
        if (startStationAnswer.equalsIgnoreCase("Select station")||endStationAnswer.equalsIgnoreCase("Select station")){
            Toast.makeText(this, "Please select station", Toast.LENGTH_SHORT).show();
            return;
        }
        getAllData(startStationAnswer, endStationAnswer);
//        resultText.setMovementMethod(new ScrollingMovementMethod());

        // Scroll to the top of the ScrollView
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));

        counter = 0;
        firstCount = 0;
        routeStations.clear();

        Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
    }

    int firstCount = 0;
    void getAllData(String startStationAnswer, String endStationAnswer){
        getDirection(startStationAnswer, endStationAnswer);
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
            if(!connectWithLine3(startStationAnswer, endStationAnswer)) {
                printStations(startStationAnswer, endStationAnswer, commonLine);
            }
        }else {
            int startLine = startLines.get(0);
            int endLine = endLines.get(0);
            ArrayList<String> startLineName = getLineName(startLine);

            String interchangeStation = getInterchangeStation(startLine, endLine, startStationAnswer, startLineName);

            resultText.append("\n**Take Line " + startLine + " from -" + startStationAnswer.toUpperCase() + "- to -" + interchangeStation.toUpperCase() + "- :");
            if(!connectWithLine3(startStationAnswer, interchangeStation)){
                printStations(startStationAnswer, interchangeStation, startLine);
            }
            firstCount += routeStations.size();
            routeStations.clear();
            resultText.append("\n");
            resultText.append("\n");
            resultText.append("\n**Change to Line " + endLine + " and from -" + interchangeStation.toUpperCase() + "- to -" + endStationAnswer.toUpperCase() + "- :");
            if(!connectWithLine3(interchangeStation, endStationAnswer)) {
                printStations(interchangeStation, endStationAnswer, endLine);
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
    }

    void printStations(String startStationAnswer, String endStationAnswer, int line){
        getStations(startStationAnswer, endStationAnswer, line);
        resultText.append("\n");
        for (String station : routeStations) {
            if (station.equalsIgnoreCase(routeStations.get(routeStations.size() - 1)))
                resultText.append(station);
            else
                resultText.append(station + " -> ");
        }
        Log.i("print", routeStations.get(0));
    }

    private void fillSpinner(RadioGroup radioGroup, Spinner spinner){
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = radioGroup.findViewById(selectedId);
        ArrayList<String> items = new ArrayList<>();

        if (radioButton.getText().toString().equals("Line 1")){
                items.clear();
                items.addAll(line1);
            }
        else if(radioButton.getText().toString().equals("Line 2")) {
                items.clear();
                items.addAll(line2);
            }
        else if(radioButton.getText().toString().equals("Line 3")) {
                items.clear();
                items.addAll(line3);
                items.addAll(line3new);
            }

        //items->adapter->spinner
        ArrayAdapter adapter=new ArrayAdapter(this
                , android.R.layout.simple_list_item_1,items);
        spinner.setAdapter(adapter);

    }

    public List<Integer> findLines(String station) {
        List<Integer> lines = new ArrayList<>();
        if (line1.contains(station)) {
            lines.add(1);
        }
        if (line2.contains(station)) {
            lines.add(2);
        }
        if (line3.contains(station) || line3new.contains(station)) {
            lines.add(3);
        }
        return lines;  // Return all lines containing the station
    }

    public String shortestPath(int startLine, int endLine, String startStation, ArrayList<String> startLineName) {
        String interchangeStation = "sadat";

        if ((startLine == 1 && endLine == 2) || (startLine == 2 && endLine == 1)) {
            int startStationIndex = startLineName.indexOf(startStation);
            int shohadaIndex = startLineName.indexOf("al shohadaa");

            int interchangeStation1 = Math.abs(startStationIndex - shohadaIndex);
            int sadatIndex = startLineName.indexOf("sadat");
            int interchangeStation2 = Math.abs(startStationIndex - sadatIndex);

            if (interchangeStation1 < interchangeStation2) {
                interchangeStation = "al shohadaa";
            }
        }
        return interchangeStation;
    }

    public String getInterchangeStation(int startLine, int endLine, String startStation, ArrayList<String> startLineName) {
        // Interchange stations for each line
        String interchangeStations = "";
        if ((startLine == 1 || endLine == 1) && (startLine == 2 || endLine == 2)) {
            interchangeStations = shortestPath(startLine, endLine, startStation, startLineName);//1&2
        } else if ((startLine == 2 || endLine == 2) && (startLine == 3 || endLine == 3)) {
            interchangeStations = "ataba";//2&3
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

    boolean connectWithLine3(String startStation, String endStation){
        String kitKatStation = "kit kat";
        String tawfikiaStation = "tawfikia";

        if(line3new.contains(endStation)){
            printStations(startStation, kitKatStation, 3);
            firstCount += routeStations.size();
            routeStations.clear();
            resultText.append(" -> ");
            printStations(tawfikiaStation, endStation, 4);
            return true;
        }
        if(line3new.contains(startStation)){
            printStations(startStation, tawfikiaStation, 4);
            firstCount += routeStations.size();
            routeStations.clear();
            resultText.append(" -> ");
            printStations(kitKatStation, endStation, 3);
            return true;
        }

        return false;
    }

    void getDirection(String startStationAnswer, String endStationAnswer) {

        boolean line1Start = line1.contains(startStationAnswer);
        boolean line1End = line1.contains(endStationAnswer);
        boolean line2Start = line2.contains(startStationAnswer);
        boolean line2End = line2.contains(endStationAnswer);
        boolean line3Start = line3.contains(startStationAnswer);
        boolean line3End = line3.contains(endStationAnswer);

        if (line1Start && line1End) {
            startDirection = getLine1Direction(startStationAnswer, endStationAnswer);
            endDirection = "";
        } else if (line2Start && line2End) {
            startDirection = getLine2Direction(startStationAnswer, endStationAnswer);
            endDirection = "";
        } else if ((line3Start || line3new.contains(startStationAnswer)) && (line3End || line3new.contains(endStationAnswer))) {
            startDirection = getLine3Direction(startStationAnswer, endStationAnswer);
            endDirection = "";
        }
        else if (line1Start && line2End) {
            startDirection = getLine1Direction(startStationAnswer, "al shohadaa");
            endDirection = getLine2Direction("al shohadaa", endStationAnswer);

        } else if (line2Start && line1End) {
            startDirection = getLine2Direction(startStationAnswer, "al shohadaa");
            endDirection = getLine1Direction("al shohadaa", endStationAnswer);

        } else if (line1Start && (line3End||line3new.contains(endStationAnswer))) {
            startDirection = getLine1Direction(startStationAnswer, "sadat");
            endDirection = getLine3Direction("nasser", endStationAnswer);

        } else if ((line3Start||line3new.contains(startStationAnswer)) && line1End) {
            startDirection = getLine3Direction(startStationAnswer, "nasser");
            endDirection = getLine1Direction("nasser", endStationAnswer);

        } else if (line2Start && (line3End||line3new.contains(endStationAnswer))) {
            startDirection = getLine2Direction(startStationAnswer, "ataba");
            endDirection = getLine3Direction("ataba", endStationAnswer);

        } else if ((line3Start||line3new.contains(startStationAnswer)) && line2End) {
            startDirection = getLine3Direction(startStationAnswer, "ataba");
            endDirection = getLine2Direction("ataba", endStationAnswer);
        }
    }

    String getLine1Direction(String startStation, String endStation){
        if (line1.indexOf(startStation) > line1.indexOf(endStation)) {
            return "Helwan";
        }
        else {
            return "El-Marg";
        }
    }

    String getLine2Direction(String startStation, String endStation){
        if (line2.indexOf(startStation) > line2.indexOf(endStation)) {
            return  "El-Mounib";
        }
        else {
            return "Shobra";
        }
    }

    String getLine3Direction(String startStation, String endStation){
        if ((line3.contains(startStation) && line3.contains(endStation))) {
            if (line3.indexOf(startStation) > line3.indexOf(endStation)) {
                return "Adly Mansour";
            }
            else {
                return "Rod El-Farag Corr.";
            }
        }
        else if((line3new.contains(startStation)||line3.contains(startStation)) &&
                (line3new.contains(endStation)||line3.contains(endStation))){
            if(line3new.contains(endStation)){
                return "Cairo University";
            }else if(line3new.contains(startStation) && (line3.indexOf("kit kat") > line3.indexOf(endStation))){
                return "Adly Mansour";
            }
            else {
                return "Rod El-Farag Corr.";
            }
        }
        return "";
    }
    String calculateEstimatedTime(int count) {
        byte arrivalTime = (byte)(count*2);
        while(arrivalTime > 60){
            counter++;
            arrivalTime = (byte)(arrivalTime-60);
        }
        if(counter > 0){
            return (counter + " Hr and " + arrivalTime + " Min");
        }else{
            return (arrivalTime + " min");
        }

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
//                System.out.print(lineStations.get(i));
                routeStations.add(lineStations.get(i));
            }
        }
    }

    public List<String> getNeighbors(String station) {
        List<String> neighbors = new ArrayList<>();

        // Check neighbors on line 1
        if (line1.contains(station)) {
            int index = line1.indexOf(station);
            if (index > 0) {
                neighbors.add(line1.get(index - 1));
            }
            if (index < line1.size() - 1) {
                neighbors.add(line1.get(index + 1));
            }
        }

        // Check neighbors on line 2
        if (line2.contains(station)) {
            int index = line2.indexOf(station);
            if (index > 0) {
                neighbors.add(line2.get(index - 1));
            }
            if (index < line2.size() - 1) {
                neighbors.add(line2.get(index + 1));
            }
        }

        // Check neighbors on line 3
        if (line3.contains(station)) {
            int index = line3.indexOf(station);
            if (index > 0) {
                neighbors.add(line3.get(index - 1));
            }
            if (index < line3.size() - 1) {
                neighbors.add(line3.get(index + 1));
            }
        }

        // Check neighbors on line 3new
        if (line3new.contains(station)) {
            int index = line3new.indexOf(station);
            if (index > 0) {
                neighbors.add(line3new.get(index - 1));
            }
            if (index < line3new.size() - 1) {
                neighbors.add(line3new.get(index + 1));
            }
        }

        return neighbors;
    }

    public List<List<String>> findAllPaths(String start, String end) {
        List<List<String>> paths = new ArrayList<>();
        Deque<String> path = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        findAllPathsDFS(start, end, visited, path, paths);
        return paths;
    }

    private void findAllPathsDFS(String current, String end, Set<String> visited, Deque<String> path, List<List<String>> paths) {
        visited.add(current);
        path.addLast(current);

        if (current.equals(end)) {
            paths.add(new ArrayList<>(path));
        } else {
            for (String neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    findAllPathsDFS(neighbor, end, visited, path, paths);
                }
            }
        }
        path.removeLast();
        visited.remove(current);
    }

}