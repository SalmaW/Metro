package com.example.metroapp;

import static com.example.metroapp.Constants.line1;
import static com.example.metroapp.Constants.line2;
import static com.example.metroapp.Constants.line3;
import static com.example.metroapp.Constants.line3new;

import android.util.Log;

import java.util.*;

public class MetroPathFinder {

//    private Map<String, List<String>> metroLines; // Map to store metro lines (lists of stations)

//    public MetroPathFinder(Map<String, List<String>> metroLines) {
//        this.metroLines = metroLines;
//    }

    // Function to find all paths between two branches (lists) in the metro system
//    public List<List<String>> findAllPathsBetweenBranches(String branch1, String branch2) {
//        List<List<String>> paths = new ArrayList<>();
//
//        // Iterate through each station in branch1 and find paths to branch2
//        for (String station : metroLines.get(branch1)) {
//            // Perform DFS from current station in branch1 to find paths to branch2
//            Set<String> visited = new HashSet<>();
//            Deque<String> path = new ArrayDeque<>();
//            findAllPathsDFS(station, branch2, visited, path, paths);
//        }
//
//        return paths;
//    }

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

            // Check if this station is "kit kat" to add neighbors from line3new
            if (station.equals("kit kat")) {
                neighbors.addAll(line3new); // Add all stations from line3new as neighbors
            }
        }


        // Check neighbors on line 3new
        if (line3new.contains(station)) {
            int index = line3new.indexOf(station);
            Log.i("index", index+"");
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
        if (start.equalsIgnoreCase("cairo university") || end.equalsIgnoreCase("cairo university")){

        }
        findAllPathsDFS(start, end, visited, path, paths);
        return paths;
    }

    // Recursive DFS function to find paths between two branches
    private void findAllPathsDFS(String current, String end, Set<String> visited, Deque<String> path, List<List<String>> paths) {
        visited.add(current);
        path.addLast(current);

        if (current.equals(end)) {
            paths.add(new ArrayList<>(path));
        } else {
            for (String neighbor : getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    if (!(path.contains("kit kat") && line3new.contains(current) && line3.contains(neighbor))
                            || !(path.contains("kit kat") && line3new.contains(neighbor) && line3.contains(current))) {
                        findAllPathsDFS(neighbor, end, visited, path, paths);
                    }
                }
            }
        }
        path.removeLast();
        visited.remove(current);
    }
//    private void findAllPathsDFS(String current, String endBranch, Set<String> visited,
//                                 Deque<String> path, List<List<String>> paths) {
//        visited.add(current);
//        path.addLast(current);
//
//        // Check if current station is in the end branch
//        if (metroLines.get(endBranch).contains(current)) {
//            paths.add(new ArrayList<>(path));
//        } else {
//            // Explore neighbors (stations in other branches)
//            for (Map.Entry<String, List<String>> entry : metroLines.entrySet()) {
//                String branch = entry.getKey();
//                List<String> stations = entry.getValue();
//
//                if (!branch.equals(endBranch) && stations.contains(current)) {
//                    for (String neighbor : stations) {
//                        if (!visited.contains(neighbor)) {
//                            findAllPathsDFS(neighbor, endBranch, visited, path, paths);
//                        }
//                    }
//                }
//            }
//        }
//
//        // Backtrack
//        path.removeLast();
//        visited.remove(current);
//    }
}