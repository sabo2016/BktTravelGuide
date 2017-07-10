package com.assignment.project.travelguide;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.assignment.project.travelguide.database.model.Place;
import com.assignment.project.travelguide.database.model.RouteDetail;
import com.assignment.project.travelguide.database.permission.PermissionInterface;
import com.assignment.project.travelguide.database.permission.PermissionManager;
import com.assignment.project.travelguide.database.repository.PlaceRepository;
import com.assignment.project.travelguide.database.repository.RouteDetailRepository;
import com.assignment.project.travelguide.database.repository.RouteRepository;
import com.assignment.project.travelguide.location.GPSTracker;
import com.assignment.project.travelguide.location.LocationChangeListener;
import com.assignment.project.travelguide.location.LocationUpdateManager;
import com.assignment.project.travelguide.util.ShortestPathFinder;
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
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.strokeWidth;


public class MainActivity extends AppCompatActivity implements LocationChangeListener {
    public static final int CAPTURE_ROUTE_ACTIVITY_RQ = 1;
    public static final int SEARCH_SOURCE_PLACE_ACTIVITY_RQ = 2;
    public static final int SEARCH_DESTINATION_PLACE_ACTIVITY_RQ = 3;

    private MapView mapView;
    View mapContainer;

    int permissionCheckCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mapContainer = findViewById(R.id.map_container);
        if (permissionCheckCounter == 0) {
            permissionCheckCounter++;
            PermissionManager.checkPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, externalPI, PermissionManager.PER_REQ_WRITE_EXTERNAL_STORAGE);
        }
    }

    PermissionInterface externalPI = new PermissionInterface() {
        @Override
        public void onGranted() {
            renderMap(mapContainer);
            PermissionManager.checkPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION, locationPI, PermissionManager.PER_REQ_ACCESS_FINE_LOCATION);

        }

        @Override
        public void onRejected() {
            Toast.makeText(MainActivity.this, "External Storage access is not granted.", Toast.LENGTH_SHORT).show();
            PermissionManager.checkPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION, locationPI, PermissionManager.PER_REQ_ACCESS_FINE_LOCATION);

        }
    };

    PermissionInterface locationPI = new PermissionInterface() {
        @Override
        public void onGranted() {
            Util.checkIfLocationIsEnabled(MainActivity.this);
            if (!Util.isMyServiceRunning(MainActivity.this, GPSTracker.class)) {
                startService(new Intent(MainActivity.this, GPSTracker.class));
            }
            LocationUpdateManager.getInstance().setLocationChangeListener(MainActivity.this);

        }

        @Override
        public void onRejected() {
            Toast.makeText(MainActivity.this, "Location Access is not granted.", Toast.LENGTH_SHORT).show();

        }
    };

    public void replaceView(View replaced, View replaceWith) {
        ViewGroup parent = (ViewGroup) replaced.getParent();
        int index = parent.indexOfChild(replaced);
        parent.removeView(replaced);
        parent.addView(replaceWith, index);
    }

    MenuItem viewDetail = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        viewDetail = menu.findItem(R.id.view_detail);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }

        MenuItem isTracking = menu.findItem(R.id.is_tracking);
        RelativeLayout relativeLayout = (RelativeLayout) isTracking.getActionView();
        SwitchCompat switchCompat = (SwitchCompat) relativeLayout.findViewById(R.id.switchForActionBar);
        switchCompat.setChecked(keepTracking);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                keepTracking = isChecked;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean flag = false;
        switch (item.getItemId()) {
            case R.id.action_search:
//                startActivityForResult(new Intent(MainActivity.this, SearchActivity.class), SEARCH_SOURCE_PLACE_ACTIVITY_RQ);
                Location lastLocation = LocationUpdateManager.getInstance().getLastKnownLocation();
                if (lastLocation != null) {
                    PlaceRepository placeRepository = new PlaceRepository(MainActivity.this);
                    sourceId = placeRepository.findNearestPlace(lastLocation.getLatitude(), lastLocation.getLongitude());

                    Intent destinationIntent = new Intent(MainActivity.this, SearchActivity.class);
                    destinationIntent.putExtra(SearchActivity.SOURCE_ID_KEY, sourceId);
                    startActivityForResult(destinationIntent, SEARCH_DESTINATION_PLACE_ACTIVITY_RQ);
                } else {
                    Toast.makeText(MainActivity.this, "Current Location not found. Please turn on GPS.", Toast.LENGTH_SHORT).show();
                }
                flag = true;
                break;
            case R.id.pick_my_location:
                pickMyLocation();
                flag = true;
                break;
            case R.id.help:

                startActivity(new Intent(MainActivity.this, HelpActivity.class));

                flag = true;
                break;
            case R.id.about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));

                flag = true;
                break;
            case R.id.view_detail:
                startActivity(new Intent(MainActivity.this, PlaceDetailActivity.class));

                break;
        }
        return flag;
    }

    @Override
    protected void onDestroy() {
        this.mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();

        if (Util.isMyServiceRunning(this, GPSTracker.class)) {
            stopService(new Intent(this, GPSTracker.class));
        }
        super.onDestroy();
    }

    boolean keepTracking = true;

    @Override
    public void onLocationChangeCalled(Location location) {
        LatLong latLong = new LatLong(location.getLatitude(), location.getLongitude());
        clearMarker(mapView, marker);
        addMarker(mapView, latLong);
        if (keepTracking) {
            this.mapView.setCenter(latLong);
        }
    }



    private void pickMyLocation() {
        Location lastLocation = LocationUpdateManager.getInstance().getLastKnownLocation();
        if (lastLocation != null) {
            LatLong latLong = new LatLong(lastLocation.getLatitude(), lastLocation.getLongitude());
            this.mapView.setCenter(latLong);
            clearMarker(mapView, marker);
            addMarker(mapView, latLong);
        }
    }



    private void renderMap(View view) {
        AndroidGraphicFactory.createInstance(this.getApplication());
        this.mapView = new MapView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        replaceView(view, mapView);
        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.setZoomLevelMin((byte) 1);
        this.mapView.setZoomLevelMax((byte) 18);

        // create a tile cache of suitable size
        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());

        // tile renderer layer using internal render theme
//        getAssets().
        MapDataStore mapDataStore = new MapFile(new File(Environment.getExternalStorageDirectory(), Util.MAP_FILE));
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                this.mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

        this.mapView.setCenter(new LatLong(27.67085, 85.43902));
        this.mapView.setZoomLevel((byte) 17);

    }

    Polyline polyline = null;

    private void paintPath(MapView mapView, Color color, Style style, LatLong[] latLongs) {
        // instantiating the paint object
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(10);
        paint.setStyle(style);

        // instantiating the polyline object
        polyline = new Polyline(paint, AndroidGraphicFactory.INSTANCE);

        // set lat lng for the polyline
        List<LatLong> coordinateList = polyline.getLatLongs();
        for (int i = 0; i < latLongs.length; i++) {
            coordinateList.add(latLongs[i]);
        }

        // adding the layer to the mapview
        mapView.getLayerManager().getLayers().add(polyline);
    }

    private void clearPath(MapView mapView, org.mapsforge.map.layer.overlay.Polyline path) {
        if (path != null) {
            mapView.getLayerManager().getLayers().remove(path);
        }
    }

    org.mapsforge.map.layer.overlay.Marker marker;
    org.mapsforge.map.layer.overlay.Marker destMarker;

    private void addMarker(MapView mapView, LatLong latLong) {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
//        paint.setColorFilter(new PorterDuffColorFilter(android.graphics.Color.BLUE, PorterDuff.Mode.MULTIPLY));

        Bitmap bitmap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bitmap = AndroidGraphicFactory.convertToBitmap(getDrawable(R.drawable.position), paint);
        } else {
            bitmap = AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.position), paint);

        }

        marker = new Marker(latLong, bitmap, 10, 10);

        mapView.getLayerManager().getLayers().add(marker);
    }

    private void addDestinationMarker(MapView mapView, LatLong latLong) {
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setAntiAlias(true);
//        paint.setColorFilter(new PorterDuffColorFilter(android.graphics.Color.BLUE, PorterDuff.Mode.MULTIPLY));

        Bitmap bitmap;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bitmap = AndroidGraphicFactory.convertToBitmap(getDrawable(R.drawable.dest_marker), paint);
        } else {
            bitmap = AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.dest_marker), paint);

        }

        destMarker = new Marker(latLong, bitmap, 10, 10);

        mapView.getLayerManager().getLayers().add(destMarker);
    }

    private void clearMarker(MapView mapView, org.mapsforge.map.layer.overlay.Marker marker) {
        if (marker != null) {
            mapView.getLayerManager().getLayers().remove(marker);
        }
    }

    int sourceId;
    int destinationId;

    public ArrayList<Integer> shortestPlaces = null;
    public double shortestdistance = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case SEARCH_SOURCE_PLACE_ACTIVITY_RQ:
                sourceId = data.getIntExtra(SearchActivity.RESULT_INT_KEY, -1);

                Intent destinationIntent = new Intent(MainActivity.this, SearchActivity.class);
                destinationIntent.putExtra(SearchActivity.SOURCE_ID_KEY, sourceId);
                startActivityForResult(destinationIntent, SEARCH_DESTINATION_PLACE_ACTIVITY_RQ);

                break;
            case SEARCH_DESTINATION_PLACE_ACTIVITY_RQ:
                destinationId = data.getIntExtra(SearchActivity.RESULT_INT_KEY, -1);
                findShortestPath(new Place(sourceId, null, 0, 0), new Place(destinationId, null, 0, 0));


                LatLong[] latLongs = convertPathsToCompleteRoute(shortestPlaces);
                clearPath(mapView, polyline);
                paintPath(mapView, Color.BLUE, Style.STROKE, latLongs);

                viewDetail.setVisible(true);
                getSupportActionBar().setTitle(Util.getDestination().getPlaceName());

                NumberFormat formatter = new DecimalFormat("#0.00");
                getSupportActionBar().setSubtitle("Distance: " + formatter.format(shortestdistance) + "m");

                LatLong latLong = new LatLong(Util.getDestination().getLatitude(), Util.getDestination().getLongitude());
                clearMarker(mapView, destMarker);
                addDestinationMarker(mapView, latLong);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionManager.PER_REQ_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPI.onGranted();
            } else {
                locationPI.onRejected();
            }

        } else if (requestCode == PermissionManager.PER_REQ_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                externalPI.onGranted();
            } else {
                externalPI.onRejected();
            }
        }

    }

    private void findShortestPath(Place source, Place destination) {
        ShortestPathFinder shortestPathFinder = new ShortestPathFinder(source, destination, MainActivity.this);
        shortestPathFinder.calculate(this);
    }

    private LatLong[] convertPathsToCompleteRoute(ArrayList<Integer> places) {
        RouteRepository routeRepository = new RouteRepository(MainActivity.this);
        RouteDetailRepository routeDetailRepository = new RouteDetailRepository(MainActivity.this);

        ArrayList<Integer> routeList = new ArrayList<Integer>();

        ArrayList<ArrayList<RouteDetail>> routeDetailLists = new ArrayList<ArrayList<RouteDetail>>();
        for (int i = 0; i < places.size() - 1; i++) {
            int routeId = routeRepository.getRouteId(places.get(i), places.get(i + 1));
            routeList.add(routeId);
            routeDetailLists.add(routeDetailRepository.selectQuery(routeId));
        }

        int total = 0;
        for (int i = 0; i < routeDetailLists.size(); i++) {
            total = routeDetailLists.get(i).size() + total;
        }

        int counter = 0;
        LatLong[] latLongs = new LatLong[total];
        for (int i = 0; i < routeDetailLists.size(); i++) {
            ArrayList<RouteDetail> temp = routeDetailLists.get(i);
            for (int j = 0; j < temp.size(); j++) {
                latLongs[counter] = new LatLong(temp.get(j).getLatitude(), temp.get(j).getLongitude());
                counter++;
            }

        }
        Log.d("routeList=>", routeList.toString());
        return latLongs;
    }
}


//
//case SEARCH_SOURCE_PLACE_ACTIVITY_RQ:
//        int placeId = data.getIntExtra(SearchActivity.RESULT_INT_KEY, -1);
//
//        RouteRepository routeRepository = new RouteRepository(MainActivity.this);
//        ArrayList<BaseModel> routes = routeRepository.select(new Route(-1, 13, placeId, 0));
//
//        if (routes.size() >= 1) {
//        Route route = ((Route) routes.get(0));
//        RouteDetailRepository routeDetailRepository = new RouteDetailRepository(MainActivity.this);
//        ArrayList<BaseModel> details = routeDetailRepository.select(new RouteDetail(route.getRouteId(), 0, 0, 0));
//
//        LatLong[] latLongs = new LatLong[details.size()];
//
//        for (int i = 0; i < details.size(); i++) {
//        RouteDetail routeDetail = (RouteDetail) details.get(i);
//        latLongs[i] = new LatLong(routeDetail.getLatitude(), routeDetail.getLongitude());
//        }
//        clearPath(mapView, polyline);
//        paintPath(mapView, Color.BLUE, Style.FILL, latLongs);
//        }