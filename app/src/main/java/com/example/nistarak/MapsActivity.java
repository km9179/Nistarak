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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{


    String _selectedDisease;
    String ageRange;
    private String serverResponse = null;
    private RetrofitClient retrofitClient;
    private Spinner mDiseaseSpinner, mAgeSpinner;
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

    private LatLng mLatLng;
    private ArrayList<String> mPlacesArraylist = new ArrayList<>();
    private ArrayList<LatLng> mLatlngArraylist = new ArrayList<>();
    private ArrayList<WeightedLatLng> mWeightedLatlngArraylist = new ArrayList<>();
    ArrayList<Marker> mMarkerArrayList= new ArrayList<>();
    HashMap<String,Marker> mMarkerMap = new HashMap<>();

    Map<String, Integer> dis = new HashMap<String, Integer>();

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    //Spinner
    private String selectedDisease,selectedAge;

    //stats
    HashMap<String,HashMap<String,Integer> > mdistrictStatHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        msearchBar=(EditText)findViewById(R.id.input_search);

        retrofitClient = RetrofitClient.getInstance();

        mdistrictStatHashMap=new HashMap<>();

        getLocationPermission();

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        get_stats("");
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
            mMap.setOnMarkerDragListener(this);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                startActivity(new Intent(MapsActivity.this,Maps2Activity.class));
                return false;
            }
        });
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
        int[] colors = {
                Color.rgb(255, 0, 0), // red
                Color.rgb(0, 0, 0)    // white
        };

        float[] startPoints = {
                0.2f, 1f
        };

        Gradient gradient = new Gradient(colors, startPoints);

        if(mWeightedLatlngArraylist.size() > 0) {
            mProvider = new HeatmapTileProvider.Builder()
                    .weightedData(mWeightedLatlngArraylist)
                    .gradient(gradient)
                    .build();
            mProvider.setRadius(100);
            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
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

                Toast.makeText(MapsActivity.this, "\n Disease: \t " + selectedDisease +
                        "\n Age: \t" + selectedAge, Toast.LENGTH_LONG).show();
                get_stats("");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // can leave this empty
            }
        });
    }

    void get_stats(String district) {
        dis.clear();
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        String endDate = Calendar.YEAR+"-"+Calendar.MONTH+"-"+Calendar.DATE;
        cal.add(Calendar.DATE, -7);
        String startDate = Calendar.YEAR+"-"+Calendar.MONTH+"-"+Calendar.DATE;
        startDate = "2018-10-30";
        endDate = "2019-03-03";
        Log.d("date",startDate+" "+endDate);


        _selectedDisease = selectedDisease;
        ageRange = selectedAge;
        if(ageRange == null) ageRange="all";
        if(_selectedDisease == null) _selectedDisease="all";

        String District = "";
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .disease(District,startDate,endDate);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.body() != null) {
                        serverResponse = response.body().string();
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

                        if (serverResponse.contains("errcode")) {
                            retrofitClient.errcode = jsonObject.getInt("errcode");
                        }

                        if (serverResponse.contains("data")) {
                            JSONArray reader = jsonObject.getJSONArray("data");
                            for (int i = 0; i < reader.length(); i++) {
                                JSONObject temp = reader.getJSONObject(i);
                                JSONObject temp2 = temp.getJSONObject("_id");
                                if (!dis.containsKey(temp2.getString("city").toLowerCase())) {
                                    dis.put(temp2.getString("city").toLowerCase(), 0);
                                }

                                if (_selectedDisease.toLowerCase().equals("all")) {
                                    if (ageRange.equals("all")) {
                                        dis.put(temp2.getString("city").toLowerCase(), dis.get(temp2.getString("city").toLowerCase()) + temp.getInt("diseaseCount"));
                                    } else if (ageRange.equals("0-18")) {
                                        dis.put(temp2.getString("city").toLowerCase(), dis.get(temp2.getString("city").toLowerCase()) + temp.getInt("age0to18"));
                                    } else if (ageRange.equals("18-34")) {
                                        dis.put(temp2.getString("city").toLowerCase(), dis.get(temp2.getString("city").toLowerCase()) + temp.getInt("age18to34"));
                                    } else if (ageRange.equals("34-55")) {
                                        Log.d("Maps", "here");
                                        dis.put(temp2.getString("city").toLowerCase(), dis.get(temp2.getString("city").toLowerCase()) + temp.getInt("age34to55"));
                                    } else if (ageRange.equals("age55")) {
                                        dis.put(temp2.getString("city").toLowerCase(), dis.get(temp2.getString("city").toLowerCase()) + temp.getInt("age55"));
                                    }
                                } else {
                                    Log.d("Hey", "sorry");
                                    if (temp2.getString("diseaseName").toLowerCase().equals(_selectedDisease)) {
                                        Log.d("Matched", temp2.getString("diseaseName"));
                                        if (ageRange.equals("all")) {
                                            dis.put(temp2.getString("city").toLowerCase(), temp.getInt("diseaseCount"));
                                        } else if (ageRange.equals("0-18")) {
                                            dis.put(temp2.getString("city").toLowerCase(), temp.getInt("age0to18"));
                                        } else if (ageRange.equals("18-34")) {
                                            dis.put(temp2.getString("city").toLowerCase(), temp.getInt("age18to34"));
                                        } else if (ageRange.equals("34-55")) {
                                            dis.put(temp2.getString("city").toLowerCase(), temp.getInt("age34to55"));
                                        } else if (ageRange.equals("age55")) {
                                            dis.put(temp2.getString("city").toLowerCase(), temp.getInt("age55"));
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("Array", "Not found");
                        }
                        mWeightedLatlngArraylist.clear();
                        mLatlngArraylist.clear();
                        mPlacesArraylist.clear();
                        mLatLng = null;
                        for (String key : dis.keySet()) {
                            String location = key;
                            List<Address> addressList = null;

                            if (location != null || !location.equals("")) {
                                Geocoder geocoder = new Geocoder(MapsActivity.this);
                                try {
                                    addressList = geocoder.getFromLocationName(location, 1);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (addressList.size() > 0) {
                                    Address address = addressList.get(0);
                                    mLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                                    mLatlngArraylist.add(mLatLng);
                                    mPlacesArraylist.add(key);
                                    mWeightedLatlngArraylist.add(new WeightedLatLng(mLatLng, dis.get(key)));
                                }
                            }
                        }
                        for(int i=0; i<mLatlngArraylist.size(); i++) {
                            System.out.println(mLatlngArraylist.get(i)+" "+mPlacesArraylist.get(i)+" "+dis.get(mPlacesArraylist.get(i)));
                        }
                        if (mWeightedLatlngArraylist.size() > 0) {
//                            addHeatMap();
                            if(mOverlay != null)
                                mOverlay.remove();
                            addHeatMap();
                        }
                    } catch (JSONException e) {
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

}
