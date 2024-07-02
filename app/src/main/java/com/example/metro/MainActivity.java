package com.example.metro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    Spinner startSpinner, endSpinner;
    TextView resultText;

    ArrayList<String> line1 = new ArrayList<>(Arrays.asList(
            "helwan", "ain helwan", "helwan university", "wadi hof", "hadayek helwan",
            "el-maasara", "tora el-asmant", "kozzika", "tora el-balad", "sakanat el-maadi", "el-maadi",
            "hadayek el-maadi", "dar el-salam", "el-zahraa", "mar girgis", "el-malek el-saleh",
            "al-sayeda zeinab", "saad zaghloul", "sadat", "nasser", "orabi", "al shohadaa",
            "ghamra", "el-demerdash", "manshiet el-sadr", "kobri el-qobba", "hammamat el-qobba",
            "saray el-qobba", "hadayek el-zaitoun", "helmeyet el-zaitoun", "el-matareyya",
            "ain shams", "ezbet el-nakhl", "el-marg", "new el-marg"
    ));
    ArrayList<String> line2 = new ArrayList<>(Arrays.asList(
            "el mounib", "sakiat mekki", "omm el misryeen", "giza", "faisal",
            "cairo university", "bohooth", "dokki", "opera", "sadat", "naguib",
            "ataba", "al shohadaa", "massara", "road el-farag", "sainte teresa",
            "khalafawy", "mezallat", "koliet el-zeraa", "shobra el kheima"
    ));
    ArrayList<String> line3 = new ArrayList<>(Arrays.asList(
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

    ArrayList<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startSpinner = findViewById(R.id.startSpinner);
        endSpinner = findViewById(R.id.endSpinner);
        resultText = findViewById(R.id.resultText);

        items.add(0,"select station");
        items.addAll(line1);
        items.addAll(line2);
        items.addAll(line3);
        items.addAll(line3new);

        //items->adapter->spinner
        ArrayAdapter startAdapter = new ArrayAdapter<>(this
                , android.R.layout.simple_list_item_1, items);
        startSpinner.setAdapter(startAdapter);

        ArrayAdapter endAdapter = new ArrayAdapter<>(this
                , android.R.layout.simple_list_item_1, items);
        endSpinner.setAdapter(endAdapter);

    }

    public void submit(View view) {

        String start = startSpinner.getSelectedItem().toString();
        String end = endSpinner.getSelectedItem().toString();

        if (start.equalsIgnoreCase("select station") || end.equalsIgnoreCase("select station")) {
            Toast.makeText(this, "please select station", Toast.LENGTH_SHORT).show();
            return;
        }


        resultText.setText(start +"\n"+end);
    }

    int calculateEstimatedTime(int count) {
        return (count * 2);
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
}