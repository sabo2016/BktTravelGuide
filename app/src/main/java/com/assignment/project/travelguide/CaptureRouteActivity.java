package com.assignment.project.travelguide;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.assignment.project.travelguide.database.model.Place;
import com.assignment.project.travelguide.database.repository.PlaceRepository;
import com.assignment.project.travelguide.location.LocationChangeListener;
import com.assignment.project.travelguide.location.LocationUpdateManager;
import com.assignment.project.travelguide.util.Util;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.strokeWidth;

public class CaptureRouteActivity extends AppCompatActivity implements LocationChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_route);
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

        findViews();
        updateView(counter);
        setListeners();
    }

    Button captureBtn;
    EditText sourceET;
    MapView mapView;
    LinearLayout mapContainer;
    EditText distinationET;

    int counter = 0;

    private void findViews() {
        captureBtn = (Button) findViewById(R.id.start_btn);
        sourceET = (EditText) findViewById(R.id.source_et);
        mapView = (MapView) findViewById(R.id.map);
        mapContainer = (LinearLayout) findViewById(R.id.map_container);
        distinationET = (EditText) findViewById(R.id.distination_et);

        renderMap(mapView);
    }

    ArrayList<Location> locations = new ArrayList<Location>();

    private void updateView(int counter) {
        switch (counter) {
            case 0:
                sourceET.setVisibility(View.VISIBLE);
                mapContainer.setVisibility(View.GONE);
                distinationET.setVisibility(View.GONE);
                break;
            case 1:
                LocationUpdateManager.getInstance().toggleCapture(true);

                sourceET.setVisibility(View.GONE);
                mapContainer.setVisibility(View.VISIBLE);
                distinationET.setVisibility(View.GONE);

                captureBtn.setText("Done");
                break;
            case 2:
                LocationUpdateManager.getInstance().toggleCapture(false);
                locations = (ArrayList<Location>) LocationUpdateManager.getInstance().getCapturedLocations().clone();
                LocationUpdateManager.getInstance().clearCapture();

                captureBtn.setText("Save");

                sourceET.setVisibility(View.GONE);
                mapContainer.setVisibility(View.GONE);
                distinationET.setVisibility(View.VISIBLE);
                break;

            case 3:
                if (locations != null && locations.size() >= 2) {
                    Place placeOne = new Place(1, sourceET.getText().toString().trim(), locations.get(0).getLatitude(), locations.get(0).getLongitude());
                    Place placeTwo = new Place(1, distinationET.getText().toString().trim(), locations.get(locations.size() - 1).getLatitude(), locations.get(locations.size() - 1).getLongitude());

                    PlaceRepository placeRepository = new PlaceRepository(CaptureRouteActivity.this);
                    long p1 = placeRepository.insert(placeOne);
                    long p2 = placeRepository.insert(placeTwo);

//                    RouteRepository routeRepository = new RouteRepository(CaptureRouteActivity.this);
//                    Iterator<Location> locationIterator = locations.iterator();
//                    while (locationIterator.hasNext()) {
//                        Location location = locationIterator.next();
//                        routeRepository.insert(new Route((int) p1, (int) p2, location.getLatitude(), location.getLongitude()));
//
//                    }


                    Toast.makeText(CaptureRouteActivity.this, "Route Saved.", Toast.LENGTH_SHORT).show();

                }
                finish();

                break;
        }
    }

    private void setListeners() {
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                updateView(counter);

            }
        });

        LocationUpdateManager.getInstance().setLocationChangeListener(this);
    }


    private void renderMap(MapView mapView) {
        AndroidGraphicFactory.createInstance(this.getApplication());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setZoomLevelMin((byte) 1);
        mapView.setZoomLevelMax((byte) 17);

        // create a tile cache of suitable size
        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());

        // tile renderer layer using internal render theme
//        getAssets().
        MapDataStore mapDataStore = new MapFile(new File(Environment.getExternalStorageDirectory(), Util.MAP_FILE));
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        mapView.setCenter(new LatLong(27.6779471, 85.4304243));
        mapView.setZoomLevel((byte) 16);
    }


    org.mapsforge.map.layer.overlay.Marker marker;

    private void addMarker(MapView mapView, LatLong latLong) {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
        paint.setColorFilter(new PorterDuffColorFilter(android.graphics.Color.BLUE, PorterDuff.Mode.MULTIPLY));

        Bitmap bitmap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bitmap = AndroidGraphicFactory.convertToBitmap(getDrawable(android.R.drawable.star_on), paint);
        } else {
            bitmap = AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(android.R.drawable.star_on), paint);

        }

        marker = new Marker(latLong, bitmap, 10, 10);
        mapView.getLayerManager().getLayers().add(marker);
    }

    @Override
    public void onLocationChangeCalled(Location location) {
        LatLong latLong = new LatLong(location.getLatitude(), location.getLongitude());
        Toast.makeText(CaptureRouteActivity.this, latLong.toString(), Toast.LENGTH_SHORT).show();
        this.mapView.setCenter(latLong);
        clearMarker(mapView, this.marker);
        addMarker(mapView, latLong);
    }


    private void paintPath(MapView mapView, Color color, Style style, LatLong[] latLongs) {
        // instantiating the paint object
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);

        // instantiating the polyline object
        Polyline polyline = new Polyline(paint, AndroidGraphicFactory.INSTANCE);

        // set lat lng for the polyline
        List<LatLong> coordinateList = polyline.getLatLongs();
        for (int i = 0; i < latLongs.length; i++) {
            coordinateList.add(latLongs[i]);
        }

        // adding the layer to the mapview
        mapView.getLayerManager().getLayers().add(polyline);
    }

    private void clearMarker(MapView mapView, org.mapsforge.map.layer.overlay.Marker marker) {
        if (marker != null) {
            mapView.getLayerManager().getLayers().remove(marker);
        }
    }
}
