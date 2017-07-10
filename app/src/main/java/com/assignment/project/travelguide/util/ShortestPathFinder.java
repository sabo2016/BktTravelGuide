package com.assignment.project.travelguide.util;

import android.content.Context;
import android.util.Log;

import com.assignment.project.travelguide.MainActivity;
import com.assignment.project.travelguide.database.model.Place;
import com.assignment.project.travelguide.database.model.Route;
import com.assignment.project.travelguide.database.repository.PlaceRepository;
import com.assignment.project.travelguide.database.repository.RouteRepository;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ukesh on 5/16/17.
 */

public class ShortestPathFinder {
    Place source;
    Place destination;
    Context context;

    Map<Integer, Double> shortestDistances = new HashMap<Integer, Double>();
    Map<Integer, ArrayList<Integer>> shortestPaths = new HashMap<Integer, ArrayList<Integer>>();

    ArrayList<Place> allPlaces = new ArrayList<Place>();
    ArrayList<Integer> visitedPlaces = new ArrayList<Integer>();
    ArrayList<Integer> unVisitedPlaces = new ArrayList<Integer>();

    public ShortestPathFinder(Place source, Place destination, Context context) {
        this.source = source;
        this.destination = destination;
        this.context = context;
    }

    public void calculate(MainActivity mainActivity) {
        PlaceRepository placeRepository = new PlaceRepository(context);
        RouteRepository routeRepository = new RouteRepository(context);

        this.allPlaces = placeRepository.selectQuery(source);

        Iterator<Place> unVisitedPlaceIterator = this.allPlaces.iterator();

        while (unVisitedPlaceIterator.hasNext()) {
            Place unvisitedPlace = unVisitedPlaceIterator.next();
            shortestDistances.put(unvisitedPlace.getPlaceId(), null);
            shortestPaths.put(unvisitedPlace.getPlaceId(), new ArrayList<Integer>());
            unVisitedPlaces.add(unvisitedPlace.getPlaceId());
        }
        visitedPlaces.add(this.source.getPlaceId());
        shortestDistances.put(this.source.getPlaceId(), new Double(0));
        shortestPaths.put(this.source.getPlaceId(), new ArrayList<Integer>());
        loop(this.source, routeRepository);

        Log.d("Source=>", source.getPlaceId() + "");
        Log.d("Destination=>", destination.getPlaceId() + "");

        Log.d("shortest Distances=>", shortestDistances.toString());
        Log.d("shortest path=>", shortestPaths.toString());

        Log.d("check=>", shortestPaths.get(destination.getPlaceId()).toString());
        ArrayList<Integer> tempPaths = shortestPaths.get(destination.getPlaceId());
        tempPaths.add(0, this.source.getPlaceId());


        double shortestDistance = shortestDistances.get(destination.getPlaceId());

        mainActivity.shortestdistance = shortestDistance;
        mainActivity.shortestPlaces = tempPaths;
    }

    private void loop(Place place, RouteRepository routeRepository) {
        ArrayList<Route> routeList = routeRepository.selectQuery(place);


        for (int i = 0; i < routeList.size(); i++) {
            if (visitedPlaces.contains(routeList.get(i).getPlaceTwo())) {
                double distanceTillNow = shortestDistances.get(place.getPlaceId());
                if (shortestDistances.get(routeList.get(i).getPlaceTwo()) > (distanceTillNow + routeList.get(i).getDistance())) {
                    shortestDistances.put(routeList.get(i).getPlaceTwo(), distanceTillNow + routeList.get(i).getDistance());

                    ArrayList<Integer> routePlaces = (ArrayList<Integer>) shortestPaths.get(place.getPlaceId()).clone();
                    routePlaces.add(routeList.get(i).getPlaceTwo());
                    shortestPaths.put(routeList.get(i).getPlaceTwo(), routePlaces);

                    if (routeList.get(i).getPlaceTwo() == destination.getPlaceId()) {
                        continue;
                    }
                    loop(new Place(routeList.get(i).getPlaceTwo(), null, 0, 0), routeRepository);
                }

            } else {
                double distanceTillNow = shortestDistances.get(place.getPlaceId());
                shortestDistances.put(routeList.get(i).getPlaceTwo(), distanceTillNow + routeList.get(i).getDistance());

                ArrayList<Integer> routePlaces = (ArrayList<Integer>) shortestPaths.get(place.getPlaceId()).clone();
                routePlaces.add(routeList.get(i).getPlaceTwo());
                shortestPaths.put(routeList.get(i).getPlaceTwo(), routePlaces);

                visitedPlaces.add(routeList.get(i).getPlaceTwo());
                unVisitedPlaces.remove(getIndex(unVisitedPlaces, routeList.get(i).getPlaceTwo()));
                if (routeList.get(i).getPlaceTwo() == destination.getPlaceId()) {
                    continue;
                }
                loop(new Place(routeList.get(i).getPlaceTwo(), null, 0, 0), routeRepository);

            }
        }


    }

    private int getIndex(ArrayList<Integer> arrayList, Integer key) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).intValue() == key.intValue()) {
                return i;
            }
        }
        return -1;
    }

}
