package com.example.nistarak;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
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
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Maps2Activity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private RetrofitClient retrofitClient;
    private String serverResponse;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrofitClient = RetrofitClient.getInstance();

        setContentView(R.layout.activity_maps2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapNgo);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        googleMap = mgoogleMap;
        fetch();
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startActivity(new Intent(Maps2Activity.this, AddNGOActivity.class));
                return false;
            }
        });
    }


    void fetch() {
        String dummy = "";
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .ngo_info(dummy);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        serverResponse = response.body().string();
                    } else {
                        Toast.makeText(Maps2Activity.this, "No response from server", Toast.LENGTH_SHORT);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (serverResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(serverResponse);

                        if (serverResponse.contains("errcode")) {
                            retrofitClient.errcode = jsonObject.getInt("errcode");
                        }

                        if (serverResponse.contains("data")) {
                            JSONArray tarr = jsonObject.getJSONArray("data");
                            Log.d("NGO",tarr.toString());
                            for(int i=0; i<tarr.length(); i++) {
                                JSONObject jso = tarr.getJSONObject(i);
                                String location = jso.getString("district");
                                List<Address> addressList = null;
//
                                if (location != null || !location.equals("")) {
                                    Geocoder geocoder = new Geocoder(Maps2Activity.this);
                                    try {
                                        addressList = geocoder.getFromLocationName(location, 1);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Address address = addressList.get(0);
                                    LatLng mLatLng = new LatLng(address.getLatitude(), address.getLongitude());
                                    if(googleMap != null)
                                        googleMap.addMarker(new MarkerOptions().position(mLatLng).title(jso.getString("name")).snippet(location+":"+jso.getString("totalCases")));
                                    Log.d("Msrker Pos",mLatLng.toString()+jso.getString("name")+jso.getString("totalCases"));
                                }
                            }
                        } else {
                            Log.d("Array", "Not found");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Maps2Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }
}
