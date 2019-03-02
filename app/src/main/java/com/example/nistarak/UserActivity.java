package com.example.nistarak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity  implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {

    Button changeLoc, viewMap;
    HashMap<String, Integer> datahash = new HashMap<>();
    TextView district;
    WebView webView1, webView2, webView3, webView4, webView5, webView6;

    EditText etSymptoms;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker, patientLocationMarker;
    private Location currentLocation;
    private LatLng patientLocation;
    LinearLayout mapLayout;
    private ViewGroup.LayoutParams params;
    private boolean firstTimeFlag = true;
    RetrofitClient retrofitClient;
    String serverResponse;
    TextView tvNotification1, tvNotification2, tvAlert1, tvAlert2, tvAlert3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        fillDatahash();
        retrofitClient = RetrofitClient.getInstance();

        etSymptoms = findViewById(R.id.etSymptoms);
        viewMap = findViewById(R.id.viewMaps);
        mapLayout = findViewById(R.id.mapLayout);
        changeLoc = findViewById(R.id.changeLoc);
        district = findViewById(R.id.tvDistrict);
        tvNotification1 = findViewById(R.id.textView7);
        tvNotification2 = findViewById(R.id.textView8);
        tvAlert1 = findViewById(R.id.textView4);
        tvAlert2 = findViewById(R.id.textView5);
        tvAlert3 = findViewById(R.id.textView6);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment2);
        supportMapFragment.getMapAsync(this);

        update_reports();

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, MapsActivity.class));
            }
        });

        etSymptoms.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getDiseases();
                    return true;
                }
                return false;
            }
        });
        changeLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params = mapLayout.getLayoutParams();
                if(params.width != 0) {
                    params.width = 0;
                    params.height = 0;
                }
                else {
                    params.width = 1080;
                    params.height = 1850;
                }
                mapLayout.setLayoutParams(params);
            }
        });
    }

    void fillDatahash() {
        datahash.put("Abdominal guarding",188);
        datahash.put("Abdominal pain",10);
        datahash.put("Abdominal pain associated with menstruation",223);
        datahash.put("Absence of a pulse",984);
        datahash.put("Aggressiveness",974);
        datahash.put("Agitation",981);
        datahash.put("Ankle deformity",996);
        datahash.put("Ankle swelling",147);
        datahash.put("Anxiety",238);
        datahash.put("Arm pain",1009);
        datahash.put("Arm swelling",971);
        datahash.put("Back deformity",998);
        datahash.put("Back pain",104);
        datahash.put("Black stools",180);
        datahash.put("Blackening of vision",57);
        datahash.put("Blackhead",24);
        datahash.put("Bleeding from vagina",284);
        datahash.put("Bleeding in the conjunctiva of the eye",176);
        datahash.put("Bloated feeling in the stomach",48);
        datahash.put("Blood in stool",190);
        datahash.put("Bloody cough",233);
        datahash.put("Blue colored skin",991);
        datahash.put("Blue spot on skin",240);
        datahash.put("Blurred vision",77);
        datahash.put("Bold area among hair on the head",239);
        datahash.put("Bone fracture",156);
        datahash.put("Breathing-related pains",250);
        datahash.put("Brittleness of nails",979);
        datahash.put("Bulging abdominal wall",192);
        datahash.put("Burning eyes",75);
        datahash.put("Burning in the throat",46);
        datahash.put("Burning nose",288);
        datahash.put("Burning sensation when urinating",107);
        datahash.put("Changes in the nails",91);
        datahash.put("Cheek swelling",170);
        datahash.put("Chest pain",17);
        datahash.put("Chest tightness",31);
        datahash.put("Chills",175);
        datahash.put("Coarsening of the skin structure",218);
        datahash.put("Cold feet",89);
        datahash.put("Cold hands",978);
        datahash.put("Cold sweats",139);
        datahash.put("Cough",15);
        datahash.put("Cough with sputum",228);
        datahash.put("Cramps",94);
        datahash.put("Cravings",49);
        datahash.put("Crusting",134);
        datahash.put("Curvature of the spine",260);
        datahash.put("Dark urine",108);
        datahash.put("Decreased urine stream",163);
        datahash.put("Delayed start to urination",165);
        datahash.put("Diarrhea",50);
        datahash.put("Difficult defecation",79);
        datahash.put("Difficulty in finding words",126);
        datahash.put("Difficulty in speaking",98);
        datahash.put("Difficulty in swallowing",93);
        datahash.put("Difficulty to concentrate",53);
        datahash.put("Difficulty to learn",1007);
        datahash.put("Difficulty with gait",1005);
        datahash.put("Discoloration of nails",216);
        datahash.put("Disorientation regarding time or place",128);
        datahash.put("Distended abdomen",989);
        datahash.put("Dizziness",207);
        datahash.put("Double vision",71);
        datahash.put("Double vision, acute-onset",270);
        datahash.put("Dribbling after urination",162);
        datahash.put("Drooping eyelid",244);
        datahash.put("Drowsiness",43);
        datahash.put("Dry eyes",273);
        datahash.put("Dry mouth",272);
        datahash.put("Dry skin",151);
        datahash.put("Earache",87);
        datahash.put("Early satiety",92);
        datahash.put("Elbow pain",1011);
        datahash.put("Enlarged calf",1006);
        datahash.put("Eye blinking",242);
        datahash.put("Eye pain",287);
        datahash.put("Eye redness",33);
        datahash.put("Eyelid swelling",208);
        datahash.put("Eyelids sticking together",209);
        datahash.put("Face pain",219);
        datahash.put("Facial paralysis",246);
        datahash.put("Facial swelling",970);
        datahash.put("Fast, deepened breathing",153);
        datahash.put("Fatty defecation",83);
        datahash.put("Feeling faint",982);
        datahash.put("Feeling of foreign body in the eye",76);
        datahash.put("Feeling of pressure in the ear",86);
        datahash.put("Feeling of residual urine",164);
        datahash.put("Feeling of tension in the legs",145);
        datahash.put("Fever",11);
        datahash.put("Finger deformity",995);
        datahash.put("Finger pain",1013);
        datahash.put("Finger swelling",1012);
        datahash.put("Flaking skin",214);
        datahash.put("Flaking skin on the head",245);
        datahash.put("Flatulence",154);
        datahash.put("Foot pain",255);
        datahash.put("Foot swelling",1002);
        datahash.put("Forgetfulness",125);
        datahash.put("Formation of blisters on a skin area",62);
        datahash.put("Foul smelling defecation",84);
        datahash.put("Frequent urination",59);
        datahash.put("Genital warts",110);
        datahash.put("Hair loss",152);
        datahash.put("Hallucination",976);
        datahash.put("Halo",72);
        datahash.put("Hand pain",186);
        datahash.put("Hand swelling",148);
        datahash.put("Hard defecation",80);
        datahash.put("Hardening of the skin",184);
        datahash.put("Headache",9);
        datahash.put("Hearing loss",206);
        datahash.put("Heart murmur",985);
        datahash.put("Heartburn",45);
        datahash.put("Hiccups",122);
        datahash.put("Hip deformity",993);
        datahash.put("Hip pain",196);
        datahash.put("Hoarseness",121);
        datahash.put("Hot flushes",149);
        datahash.put("Immobilization",197);
        datahash.put("Impaired balance",120);
        datahash.put("Impaired hearing",90);
        datahash.put("Impaired light-dark adaptation",70);
        datahash.put("Impairment of male potency",113);
        datahash.put("Incomplete defecation",81);
        datahash.put("Increased appetite",131);
        datahash.put("Increased drive",262);
        datahash.put("Increased salivation",204);
        datahash.put("Increased thirst",40);
        datahash.put("Increased touch sensitivity",220);
        datahash.put("Increased urine quantity",39);
        datahash.put("Involuntary movements",257);
        datahash.put("Irregular heartbeat",986);
        datahash.put("Irregular mole",65);
        datahash.put("Itching eyes",73);
        datahash.put("Itching in the ear",88);
        datahash.put("Itching in the mouth or throat",973);
        datahash.put("Itching in the nose",96);
        datahash.put("Itching of skin",21);
        datahash.put("Itching of the anus",999);
        datahash.put("Itching on head",247);
        datahash.put("Itching or burning in the genital area",268);
        datahash.put("Joint effusion",194);
        datahash.put("Joint instability",198);
        datahash.put("Joint pain",27);
        datahash.put("Joint redness",230);
        datahash.put("Joint swelling",193);
        datahash.put("Joylessness",47);
        datahash.put("Knee deformity",994);
        datahash.put("Knee pain",256);
        datahash.put("Leg cramps",146);
        datahash.put("Leg pain",1010);
        datahash.put("Leg swelling",231);
        datahash.put("Leg ulcer",143);
        datahash.put("Less than 3 defecations per week",82);
        datahash.put("Limited mobility of the ankle",992);
        datahash.put("Limited mobility of the back",167);
        datahash.put("Limited mobility of the fingers",178);
        datahash.put("Limited mobility of the hip",1000);
        datahash.put("Limited mobility of the leg",195);
        datahash.put("Lip swelling",35);
        datahash.put("Lockjaw",205);
        datahash.put("Loss of eye lashes",210);
        datahash.put("Lower abdominal pain",174);
        datahash.put("Lower-back pain",263);
        datahash.put("Lump in the breast",261);
        datahash.put("Malposition of the testicles",266);
        datahash.put("Marked veins",232);
        datahash.put("Memory gap",235);
        datahash.put("Menstruation disorder",112);
        datahash.put("Missed period",123);
        datahash.put("Moist and softened skin",215);
        datahash.put("Mood swings",85);
        datahash.put("Morning stiffness",983);
        datahash.put("Mouth pain",135);
        datahash.put("Mouth ulcers",97);
        datahash.put("Muscle pain",177);
        datahash.put("Muscle stiffness",119);
        datahash.put("Muscle weakness",987);
        datahash.put("Muscular atrophy in the leg",252);
        datahash.put("Muscular atrophy of the arm",202);
        datahash.put("Muscular weakness in the arm",168);
        datahash.put("Muscular weakness in the leg",253);
        datahash.put("Nausea",44);
        datahash.put("Neck pain",136);
        datahash.put("Neck stiffness",234);
        datahash.put("Nervousness",114);
        datahash.put("Night cough",133);
        datahash.put("Night sweats",1004);
        datahash.put("Non-healing skin wound",63);
        datahash.put("Nosebleed",38);
        datahash.put("Numbness in the arm",221);
        datahash.put("Numbness in the leg",254);
        datahash.put("Numbness of the hands",200);
        datahash.put("Oversensitivity to light",137);
        datahash.put("Overweight",157);
        datahash.put("Pain in the bones",155);
        datahash.put("Pain in the calves",142);
        datahash.put("Pain in the limbs",12);
        datahash.put("Pain of the anus",990);
        datahash.put("Pain on swallowing",203);
        datahash.put("Pain radiating to the arm",251);
        datahash.put("Pain radiating to the leg",103);
        datahash.put("Pain when chewing",286);
        datahash.put("Painful defecation",189);
        datahash.put("Painful urination",109);
        datahash.put("Pallor",150);
        datahash.put("Palpitations",37);
        datahash.put("Paralysis",140);
        datahash.put("Physical inactivity",118);
        datahash.put("Problems with the sense of touch in the face",129);
        datahash.put("Problems with the sense of touch in the feet",130);
        datahash.put("Protrusion of the eyes",258);
        datahash.put("Purulent discharge from the urethra",172);
        datahash.put("Purulent discharge from the vagina",173);
        datahash.put("Rebound tenderness",191);
        datahash.put("Reduced appetite",54);
        datahash.put("Ringing in the ear",78);
        datahash.put("Runny nose",14);
        datahash.put("Sadness",975);
        datahash.put("Scalp redness",269);
        datahash.put("Scar",1001);
        datahash.put("Sensitivity to cold",60);
        datahash.put("Sensitivity to glare",69);
        datahash.put("Sensitivity to noise",102);
        datahash.put("Shiny red tongue",264);
        datahash.put("Shortness of breath",29);
        datahash.put("Side pain",183);
        datahash.put("Skin lesion",26);
        datahash.put("Skin nodules",25);
        datahash.put("Skin rash",124);
        datahash.put("Skin redness",61);
        datahash.put("Skin thickening",217);
        datahash.put("Skin wheal",34);
        datahash.put("Sleepiness with spontaneous falling asleep",241);
        datahash.put("Sleeplessness",52);
        datahash.put("Sneezing",95);
        datahash.put("Sore throat",13);
        datahash.put("Sputum",64);
        datahash.put("Stomach burning",179);
        datahash.put("Stress-related leg pain",185);
        datahash.put("Stuffy nose",28);
        datahash.put("Sweating",138);
        datahash.put("Swelling in the genital area",236);
        datahash.put("Swelling of the testicles",267);
        datahash.put("Swollen glands in the armpit",248);
        datahash.put("Swollen glands in the groin",249);
        datahash.put("Swollen glands in the neck",169);
        datahash.put("Tears",211);
        datahash.put("Testicular pain",222);
        datahash.put("Tic",243);
        datahash.put("Tingling",201);
        datahash.put("Tiredness",16);
        datahash.put("Toe deformity",997);
        datahash.put("Toe swelling",1003);
        datahash.put("Tongue burning",980);
        datahash.put("Tongue swelling",977);
        datahash.put("Toothache",1008);
        datahash.put("Tremor at rest",115);
        datahash.put("Tremor on movement",132);
        datahash.put("Trouble understanding speech",988);
        datahash.put("Unconsciousness, short",144);
        datahash.put("Uncontrolled defecation",265);
        datahash.put("Underweight",116);
        datahash.put("Urge to urinate",160);
        datahash.put("Urination during the night",161);
        datahash.put("Vision impairment",68);
        datahash.put("Vision impairment for far objects",213);
        datahash.put("Vision impairment for near objects",166);
        datahash.put("Visual field loss",66);
        datahash.put("Vomiting",101);
        datahash.put("Vomiting blood",181);
        datahash.put("Weakness or numbness on right or left side of body",972);
        datahash.put("Weight gain",23);
        datahash.put("Weight loss",22);
        datahash.put("Wheezing",30);
        datahash.put("Wound",187);
        datahash.put("Yellow colored skin",105);
        datahash.put("Yellowish discoloration of the white part of the eye",106);
    }

    void get_alerts(String district) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        String endDate = Calendar.YEAR+"-"+Calendar.MONTH+"-"+Calendar.DATE;
        cal.add(Calendar.DATE, -7);
        String startDate = Calendar.YEAR+"-"+Calendar.MONTH+"-"+Calendar.DATE;
        startDate = "2018-10-30";
        endDate = "2019-03-03";
        Log.d("date",startDate+" "+endDate);

        String District = district.toLowerCase();
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
                        Toast.makeText(UserActivity.this, "No response from server", Toast.LENGTH_SHORT);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


                tvAlert1.setText("Disease 1");
                tvAlert2.setText("Disease 2");
                tvAlert3.setText("Disease 3");
                if(serverResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(serverResponse);

                        if(serverResponse.contains("errcode")){
                            retrofitClient.errcode = jsonObject.getInt("errcode");
                        }

                        if(serverResponse.contains("data")) {
                            Log.d("Alert",jsonObject.getJSONArray("data").toString());
                            JSONArray reader = jsonObject.getJSONArray("data");
                            Log.d("No : ",reader.length()+"");
                            if (reader.length() > 0) {
                                JSONObject temp = reader.getJSONObject(0);
                                JSONObject temp2 = temp.getJSONObject("_id");
                                String temp3 = temp2.getString("diseaseName").toUpperCase()+"\nCount:"+temp.getString("diseaseCount");
                                tvAlert1.setText(temp3);
//                                Log.d("Array1",temp.getString("message")+" "+temp.getString("senderName"));
                            }
                            if (reader.length() > 1) {
                                JSONObject temp = reader.getJSONObject(1);
                                JSONObject temp2 = temp.getJSONObject("_id");
                                String temp3 = temp2.getString("diseaseName")+"\nCount:"+temp.getString("diseaseCount");
                                tvAlert2.setText(temp3);
//                                Log.d("Array2",temp.getString("message")+" "+temp.getString("senderName"));
                            }
                            if (reader.length() > 2) {
                                JSONObject temp = reader.getJSONObject(2);
                                JSONObject temp2 = temp.getJSONObject("_id");
                                String temp3 = temp2.getString("diseaseName")+"\nCount:"+temp.getString("diseaseCount");
                                tvAlert3.setText(temp3);
//                                Log.d("Array3",temp.getString("message")+" "+temp.getString("senderName"));
                            }
                        }
                        else {
                            Log.d("Array","Not found");
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void get_notifications(String district) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .notification(district);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.body() != null) {
                        serverResponse = response.body().string();
                    }
                    else {
                        Toast.makeText(UserActivity.this, "No response from server", Toast.LENGTH_SHORT);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


                tvNotification1.setText("No notifications available for you");
                tvNotification2.setText("No notifications available for you");
                if(serverResponse != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(serverResponse);

                        if(serverResponse.contains("errcode")){
                            retrofitClient.errcode = jsonObject.getInt("errcode");
                        }

                        if(serverResponse.contains("data")) {
                            JSONArray reader = jsonObject.getJSONArray("data");
                            if (reader.length() >= 0) {
                            JSONObject temp = reader.getJSONObject(0);
                                tvNotification1.setText(temp.getString("message")+"\n-"+temp.getString("senderName"));
                                Log.d("Array",temp.getString("message")+" "+temp.getString("senderName"));
                            }
                            if (reader.length() >= 1) {
                                JSONObject temp = reader.getJSONObject(1);
                                tvNotification2.setText(temp.getString("message")+"\n-"+temp.getString("senderName"));
                                Log.d("Array",temp.getString("message")+" "+temp.getString("senderName"));
                            }
                        }
                        else {
                            Log.d("Array","Not found");
                        }

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    void getDiseases() {
        final String symptomsText = etSymptoms.getText().toString();
        String []symp = symptomsText.split(" ");
        ArrayList<Integer> symptoms = new ArrayList<>();
        for(String name:symp) {
            if(datahash.containsKey(name)) {
                symptoms.add(datahash.get(name));
            }
        }
        Log.d("Check",symptoms.toString());

        String dob="1990";
        String gender="male";

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .predict_disease(symptoms,gender,dob);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if(response.body() != null) {
                        serverResponse = response.body().string();
                        Toast.makeText(UserActivity.this, serverResponse+" received", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(UserActivity.this, "No response from server", Toast.LENGTH_SHORT);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UserActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
//
    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.currentLocationImageButton && googleMap != null && currentLocation != null) {
                UserActivity.this.animateCamera(currentLocation);
            }
        }
    };
//

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserActivity.this,
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
//
    @Override
    public void onMarkerDragEnd(Marker marker) {
        patientLocation = marker.getPosition();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        final String dist_name = getAddress(patientLocation.latitude, patientLocation.longitude);
        district.setText(dist_name);
        get_notifications(dist_name);
        get_alerts(dist_name);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    void update_reports() {
        webView1 = findViewById(R.id.wvStat1);
        webView1.getSettings().setJavaScriptEnabled(true);
        webView1.loadUrl("https://thingspeak.com/channels/710959/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView2 = findViewById(R.id.wvStat2);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView2.loadUrl("https://thingspeak.com/apps/plugins/275063?fbclid=IwAR3iyoKA5dXfwfkbAGvVULNQVW3FcyJUl-MmkhfsMjAt7MzOl1b_Ft14QNI");

        webView3 = findViewById(R.id.wvStat3);
        webView3.getSettings().setJavaScriptEnabled(true);
        webView3.loadUrl("https://thingspeak.com/channels/710959/charts/5?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView4 = findViewById(R.id.wvStat4);
        webView4.getSettings().setJavaScriptEnabled(true);
        webView4.loadUrl("https://thingspeak.com/channels/710959/charts/6?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView5 = findViewById(R.id.wvStat5);
        webView5.getSettings().setJavaScriptEnabled(true);
        webView5.loadUrl("https://thingspeak.com/channels/710965/charts/2?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&type=line&update=15");

        webView6 = findViewById(R.id.wvStat6);
        webView6.getSettings().setJavaScriptEnabled(true);
        webView6.loadUrl("https://thingspeak.com/apps/plugins/275063?fbclid=IwAR3iyoKA5dXfwfkbAGvVULNQVW3FcyJUl-MmkhfsMjAt7MzOl1b_Ft14QNI");

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerDragListener(this);
    }
}
