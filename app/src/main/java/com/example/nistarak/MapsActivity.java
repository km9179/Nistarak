package com.example.nistarak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{

    private String serverResponse = null;
    private RetrofitClient retrofitClient;
    Spinner mDiseaseSpinner, mAgeSpinner;
    private EditText msearchBar;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double longitude;
    private double latitude;
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //Places
    private ArrayList<String> mPlacesArraylist = new ArrayList<>();
    List<String> namesList = Arrays.asList( "Brisbane", "Sydney", "Melbourne", "Perth" ,"Darwin");

    //Location
    private LatLng mLatLng;
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

    //Spinner
    private String selectedDisease,selectedAge;

    //CustomInfoWindow
    private CustomInfoWindowAdapter adapter;

    //stats
    HashMap<String,HashMap<String,Integer> > mdistrictStatHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        msearchBar=(EditText)findViewById(R.id.input_search);
        adapter = new CustomInfoWindowAdapter(MapsActivity.this);
        mdistrictStatHashMap=new HashMap<>();

        retrofitClient = RetrofitClient.getInstance();

        mLatlngArraylist.addAll(mLatlngList);

        mWeightedLatlngArraylist.addAll(mWeightedLatlngList);

        mMarkerArrayList.addAll(mMarkerList);

        mPlacesArraylist.addAll(namesList);

        getLocationPermission();
        get_all_district_data();
    }

    private void get_all_district_data() {

        String dummy = "dummy";
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .get_district_stats(dummy);
//
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.body() != null) {
                        serverResponse = response.body().string();
                        Toast.makeText(MapsActivity.this, serverResponse+" received", Toast.LENGTH_SHORT).show();
                        Log.d("District",serverResponse);
                    }
                    else {
                        Toast.makeText(MapsActivity.this, "No response from server", Toast.LENGTH_SHORT);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                if(serverResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(serverResponse);
                        JSONArray jsonArray=jsonObject.getJSONArray("data");
                        Log.d("District",jsonArray.toString());
                        HashMap<String,Integer> hm = new HashMap<>();
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            jsonObject= jsonArray.getJSONObject(i);
                            String district=jsonObject.getString("district");
                            JSONArray jsonArray1=jsonObject.getJSONArray("frequency");
                            for(int j=0;j<jsonArray1.length();j++)
                            {
                                 jsonObject=jsonArray1.getJSONObject(j);
                                 String k= jsonObject.keys().next();
                                 hm.put(k,jsonObject.getInt(k));
                                 Log.d("District",k+" "+jsonObject.getString(k));
                            }
                            mdistrictStatHashMap.put(district,hm);
                            //districtStat.put(jsonArray.getString(i),)
                        }
                        retrofitClient.reset();
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMarkerDragListener(this);}
        mAddMarkerToMap(mLatlngArraylist,mMarkerArrayList,mPlacesArraylist);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startActivity(new Intent(MapsActivity.this,Maps2Activity.class));
                return false;
            }
        });
        addHeatMap();
        designSpinner();

        //SearchBar
        msearchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onMapSearch(msearchBar);
                    return true;
                }
                return false;
            }
        });
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
                        .snippet("Population: 4,137,400\nMalaria-50\nTyphoid-70\n")
                        .draggable(true)));


                mMap.setInfoWindowAdapter(adapter);
                mMarkerMap.get(mPlacesArraylist.get(i)).setTag(mArraylistLatlng.get(i));
                //mMarkerMap.get(mPlacesArraylist.get(i)).showInfoWindow();

                Toast.makeText(this, "Size: " + mMarkerMap.size(), Toast.LENGTH_LONG).show();
            }

        }

    }



    //SearchBar
    public void onMapSearch(View view) {
        Log.d("MapActivity","MapSearchBar");
        String location = msearchBar.getText().toString();
        List<Address>addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            mLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            Toast.makeText(this,"the searched loaction is"+ mLatLng,Toast.LENGTH_LONG).show();
            mMap.animateCamera(CameraUpdateFactory.newLatLng(mLatLng));
            if(!mMarkerMap.containsKey(location))
                mMap.addMarker(new MarkerOptions().position(mLatLng).title(location).snippet("Lat: "+address.getLatitude()+"\nLon:"+address.getLongitude())).showInfoWindow();
            else
            {
                mMarkerMap.get(location).showInfoWindow();
            }

        }
    }

    //Current Location
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            mLatLng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(mLatLng));
                            mMap.addMarker(new MarkerOptions().position(mLatLng).title("My Location"));
                            Log.d(TAG, "onComplete: current location is fetched");


                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //Initialising MAp
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);


    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        //mLatlngArraylist.add(new LatLng(latitude,longitude));
        Toast.makeText(this,"New: Lat: "+latitude+" long: "+longitude,Toast.LENGTH_LONG).show();

    }

    private void designSpinner()
    {
        mDiseaseSpinner = (Spinner) findViewById(R.id.DiseaseSpinner);
        mAgeSpinner= (Spinner) findViewById(R.id.AgeSpinner);



        mDiseaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedDisease = parent.getItemAtPosition(position).toString();

                mAgeSpinner.setAdapter(new ArrayAdapter<String>(MapsActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.items_Age)));
                //set divSpinner Visibility to Visible
                mAgeSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });

        mAgeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedAge = parent.getItemAtPosition(position).toString();
                selectedAge = parent.getItemAtPosition(position).toString();

                Toast.makeText(MapsActivity.this, "\n Disease: \t " + selectedDisease +
                        "\n Age: \t" + selectedAge, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });
    }


}
