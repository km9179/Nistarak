package com.example.nistarak;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportDiseaseCase extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {
    EditText etNameOfPatient, etNameOfDisease, etAgeOfPatient, etAdhaarOfPatient;
    Button btnAddCase;
    private RetrofitClient retrofitClient;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker, patientLocationMarker;
    private Location currentLocation;
    private LatLng patientLocation;
    private boolean firstTimeFlag = true;

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
        setContentView(R.layout.activity_report_disease_case);

        etNameOfPatient = (EditText) findViewById(R.id.etNameOfPatient);
        etNameOfDisease = (EditText) findViewById(R.id.etNameOfDisease);
        etAdhaarOfPatient = (EditText) findViewById(R.id.etAdhaarOfPatient);
        etAgeOfPatient = (EditText) findViewById(R.id.etAgeOfPatient);

        mLatlngArraylist.addAll(mLatlngList);

        mWeightedLatlngArraylist.addAll(mWeightedLatlngList);

        mMarkerArrayList.addAll(mMarkerList);
        mPlacesArraylist.addAll(namesList);

        btnAddCase = (Button) findViewById(R.id.btnAddCase);
        btnAddCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddress(patientLocation.latitude,patientLocation.longitude);
                addCase(etNameOfPatient.toString(),etNameOfDisease.toString(),
                        Integer.parseInt(etAgeOfPatient.getText().toString()), etAdhaarOfPatient.toString(),
                        patientLocation.latitude,patientLocation.longitude,getAddress(patientLocation.latitude,patientLocation.longitude));
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);
        findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);

        if(patientLocation != null)
        Log.i("Map",patientLocation.toString());
    }

    String getAddress(double lat, double lng) {
        String city = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("IGA", "Address" + add);

            city = obj.getSubAdminArea();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return city;
    }



    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && googleMap != null) {
                animateCamera(currentLocation);
                firstTimeFlag = false;
            }
            showMarker(currentLocation);
        }
    };

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.currentLocationImageButton && googleMap != null && currentLocation != null) {
                ReportDiseaseCase.this.animateCamera(currentLocation);
            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mAddMarkerToMap(mLatlngArraylist,mMarkerArrayList,mPlacesArraylist);
        googleMap.setOnMarkerDragListener(this);
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
        mOverlay = googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }


    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ReportDiseaseCase.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Permission denied by uses", Toast.LENGTH_SHORT).show();
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCurrentLocationUpdates();
        }
    }

    private void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void mAddMarkerToMap(ArrayList<LatLng> mArraylistLatlng,ArrayList<Marker> mMarkerArrayList,ArrayList<String> mPlacesArraylist)
    {
        Log.d("MapActivity","Markers Added");
        for( int i=0;i<mArraylistLatlng.size();i++)
        {
            if(!mMarkerMap.containsKey(mPlacesArraylist.get(i))) {
                mMarkerMap.put(mPlacesArraylist.get(i), googleMap.addMarker(new MarkerOptions().position(mArraylistLatlng.get(i))
                        .title(mPlacesArraylist.get(i))
                        .snippet("Population: 4,137,400")
                        .draggable(true)));

                Toast.makeText(this, "Size: " + mMarkerMap.size(), Toast.LENGTH_LONG).show();
            }

        }

    }

    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng patientLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if (currentLocationMarker == null) {
            currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng).title("Your Location"));
        }

        if (patientLocationMarker == null) {
            patientLocationMarker = googleMap.addMarker(new MarkerOptions().position(patientLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.loc_marker)).draggable(true).title("Patient Location"));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient = null;
        googleMap = null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        patientLocation = marker.getPosition();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }




    private void addCase(String patienName, String diseaseName, Integer age, String adhaaar,
                         Double lat, Double lang, String city) {
        Toast.makeText(this,"Called ",Toast.LENGTH_LONG);
//        Call<ResponseBody> call = RetrofitClient
//                .getInstance()
//                .getApi()
//                .newDiseaseCase(retrofitClient.token, patienName, diseaseName, lat, lang, age, adhaaar, city);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    if(response.body() != null) {
//                        String s = response.body().string();
//                        Toast.makeText(ReportDiseaseCase.this, s, Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        Toast.makeText(ReportDiseaseCase.this,"No response from server",Toast.LENGTH_SHORT);
//                    }
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Toast.makeText(ReportDiseaseCase.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }


}
