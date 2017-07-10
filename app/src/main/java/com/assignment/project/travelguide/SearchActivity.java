package com.assignment.project.travelguide;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.assignment.project.travelguide.database.model.Place;
import com.assignment.project.travelguide.database.repository.PlaceRepository;
import com.assignment.project.travelguide.location.LocationUpdateManager;
import com.assignment.project.travelguide.util.Util;

import org.mapsforge.core.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    public static final String RESULT_INT_KEY = "result_int_key";
    public static final String SOURCE_ID_KEY = "source_id_key";

    private String sourceTitle = "Select Source";
    private String destinationTitle = "Select Destination";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        int sourceId = getIntent().getIntExtra(SOURCE_ID_KEY, -1);

        PlaceRepository placeRepository = new PlaceRepository(SearchActivity.this);
        final ArrayList placeArrayList;

        if (sourceId != -1) {
            getSupportActionBar().setTitle(destinationTitle);
//            placeArrayList = placeRepository.selectQuery(new Place(sourceId, null, 0, 0));
            placeArrayList = Util.getStaticPlaces();

        } else {
            getSupportActionBar().setTitle(sourceTitle);
            placeArrayList = placeRepository.select(null);
        }

        String[] itemData = new String[placeArrayList.size()];
        for (int i = 0; i < placeArrayList.size(); i++) {
            itemData[i] = ((Place) placeArrayList.get(i)).getPlaceName();
        }

        ListView placeList = (ListView) findViewById(R.id.place_list);


        placeList.setAdapter(new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, itemData));
        placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Util.setDestination(((Place) placeArrayList.get(position)).getPlaceId());

                PlaceRepository placeRepository = new PlaceRepository(SearchActivity.this);
                int placeId = placeRepository.findNearestPlace(((Place) placeArrayList.get(position)).getLatitude(), ((Place) placeArrayList.get(position)).getLongitude());

                intent.putExtra(RESULT_INT_KEY, placeId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }


        return true;
    }

}
