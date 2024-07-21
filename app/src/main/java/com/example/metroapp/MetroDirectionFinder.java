package com.example.metroapp;

import static com.example.metroapp.Constants.line1;
import static com.example.metroapp.Constants.line2;
import static com.example.metroapp.Constants.line3;
import static com.example.metroapp.Constants.line3new;

public class MetroDirectionFinder {


    String startDirection = "";
    String endDirection = "";
    String [] directionArr = new String[2];


    String[] getDirection(String startStationAnswer, String endStationAnswer) {

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
            if(line3new.contains(startStationAnswer) && line3new.contains(endStationAnswer)) {
                if (line3new.indexOf(startStationAnswer) > line3new.indexOf(endStationAnswer)) {
                    startDirection = "Adly Mansour";
                    endDirection = "";
                } else {
                    startDirection = "Cairo University";
                    endDirection = "";
                }
            }
            else {
                startDirection = getLine3Direction(startStationAnswer, endStationAnswer);
                endDirection = "";
            }

        } else if (line1Start && line2End) {
            startDirection = getLine1Direction(startStationAnswer, "al shohadaa");
            endDirection = getLine2Direction("al shohadaa", endStationAnswer);

        } else if (line2Start && line1End) {
            startDirection = getLine2Direction(startStationAnswer, "al shohadaa");
            endDirection = getLine1Direction("al shohadaa", endStationAnswer);

        } else if (line1Start && (line3End || line3new.contains(endStationAnswer))) {
            startDirection = getLine1Direction(startStationAnswer, "sadat");
            endDirection = getLine3Direction("nasser", endStationAnswer);

        } else if ((line3Start || line3new.contains(startStationAnswer)) && line1End) {
            startDirection = getLine3Direction(startStationAnswer, "nasser");
            endDirection = getLine1Direction("nasser", endStationAnswer);

        } else if (line2Start && (line3End || line3new.contains(endStationAnswer))) {
            startDirection = getLine2Direction(startStationAnswer, "ataba");
            endDirection = getLine3Direction("ataba", endStationAnswer);

        } else if ((line3Start || line3new.contains(startStationAnswer)) && line2End) {
            startDirection = getLine3Direction(startStationAnswer, "ataba");
            endDirection = getLine2Direction("ataba", endStationAnswer);
        }
        directionArr[0] = startDirection;
        directionArr[1] = endDirection;
        return directionArr;
    }

    String getLine1Direction(String startStation, String endStation) {
        if (line1.indexOf(startStation) > line1.indexOf(endStation)) {
            return "Helwan";
        } else {
            return "El-Marg";
        }
    }

    String getLine2Direction(String startStation, String endStation) {
        if (line2.indexOf(startStation) > line2.indexOf(endStation)) {
            return "El-Mounib";
        } else {
            return "Shobra";
        }
    }

    String getLine3Direction(String startStation, String endStation) {
        if ((line3.contains(startStation) && line3.contains(endStation))) {
            if (line3.indexOf(startStation) > line3.indexOf(endStation)) {
                return "Adly Mansour";
            } else {
                return "Rod El-Farag Corr.";
            }
        } else if ((line3new.contains(startStation) || line3.contains(startStation)) &&
                (line3new.contains(endStation) || line3.contains(endStation))) {
            if (line3new.contains(endStation)) {
                return "Cairo University";
            } else if (line3new.contains(startStation) && (line3.indexOf("kit kat") > line3.indexOf(endStation))) {
                return "Adly Mansour";
            } else {
                return "Rod El-Farag Corr.";
            }
        }
        return "";
    }
}
