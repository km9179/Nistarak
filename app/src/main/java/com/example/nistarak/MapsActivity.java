package com.example.nistarak;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //Places
    private ArrayList<String> mPlacesArraylist = new ArrayList<>();
    List<String> namesList = Arrays.asList( "Brisbane", "Sydney", "Melbourne", "Perth" ,"Darwin");

    //Location
    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);

    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

    private static final LatLng DARWIN = new LatLng(-12.4634, 130.8456);

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);

    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);

    private static final LatLng ALICE_SPRINGS = new LatLng(-24.6980, 133.8807);

    private ArrayList<LatLng> mLatlngArraylist = new ArrayList<>();
    List<LatLng> mLatlngList = Arrays.asList(BRISBANE,SYDNEY,MELBOURNE,PERTH,DARWIN);


    private static final WeightedLatLng wBRISBANE = new WeightedLatLng(BRISBANE,50);

    private static final WeightedLatLng wSYDENEY = new WeightedLatLng(SYDNEY,100);

    private static final WeightedLatLng wMELBOURNE = new WeightedLatLng(MELBOURNE,150);

    private static final WeightedLatLng wPERTH = new WeightedLatLng(PERTH,80);

    private static final WeightedLatLng wDARWIN = new WeightedLatLng(DARWIN,200);

    private ArrayList<WeightedLatLng> mWeightedLatlngArraylist = new ArrayList<>();

    List<WeightedLatLng> mWeightedLatlngList = Arrays.asList(wBRISBANE,wSYDENEY,wMELBOURNE,wPERTH,wDARWIN);

    //Diseases
    ArrayList<String> mDiseaseArraylist = new ArrayList<>();
    List<String> mDiseaseList = Arrays.asList("Malaria");

    //Age
    ArrayList<Integer> mAgeArraylist = new ArrayList<>();

    //Marker
    private Marker mPerth;

    private Marker mSydney;

    private Marker mBrisbane;

    private Marker mDarwin;

    private Marker mMelbourne;
    ArrayList<Marker> mMarkerArrayList= new ArrayList<>();
    List<Marker> mMarkerList = Arrays.asList(mBrisbane,mSydney,mMelbourne,mPerth,mDarwin);
    HashMap<String,Marker> mMarkerMap = new HashMap<>();

    //HeatMap
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mLatlngArraylist.addAll(mLatlngList);

        mWeightedLatlngArraylist.addAll(mWeightedLatlngList);

        mMarkerArrayList.addAll(mMarkerList);

        mPlacesArraylist.addAll(namesList);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mAddMarkerToMap(mLatlngArraylist,mMarkerArrayList,mPlacesArraylist);
        addHeatMap();
    }

    private void addHeatMap() {
        Log.d("MapActivity: ","Heat Map.");
        /*List<LatLng> list = null;

        // Get the data: latitude/longitude positions of police stations.
        try {
            list = readItems(R.raw.police_stations);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }*/

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(mWeightedLatlngArraylist)
                .gradient(gradient)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mProvider.setRadius(50);
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private void mAddMarkerToMap(ArrayList<LatLng> mArraylistLatlng,ArrayList<Marker> mMarkerArrayList,ArrayList<String> mPlacesArraylist)
    {
        Log.d("MapActivity","Markers Added");
        for( int i=0;i<mArraylistLatlng.size();i++)
        {
            if(!mMarkerMap.containsKey(mPlacesArraylist.get(i))) {
                mMarkerMap.put(mPlacesArraylist.get(i), mMap.addMarker(new MarkerOptions().position(mArraylistLatlng.get(i))
                        .title(mPlacesArraylist.get(i))
                        .snippet("Population: 4,137,400")
                        .draggable(true)));

                Toast.makeText(this, "Size: " + mMarkerMap.size(), Toast.LENGTH_LONG).show();
            }

        }

    }


}
