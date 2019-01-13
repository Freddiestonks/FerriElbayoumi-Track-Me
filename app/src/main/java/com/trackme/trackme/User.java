package com.trackme.trackme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;

public class User extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private GoogleApiClient mClient = null;
    // OLD
    // private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 0;
    private static final String LOG_TAG = "GOOGLE FIT";
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView nav_email;
    private FirebaseAuth mAuth;
    private View headerView;
    private Double latitude;
    private Double longitude;
    private TextView nav_phone;
    private int total = 0;
    //Constant variables
    private final static String dbLastName = "Last Name";
    private final static String dbCF = "CF";
    private final static String dbName = "Name";
    private final static String dbPhone = "Phone Number";
    private final static String dbYear = "Year";
    private final static String dbMonth = "Month";
    private final static String dbday = "Day";
    private final static String dbGender = "Gender";
    private final static String dbMale = "Male";
    private final static String dbFemale = "Female";
    private final static String dbWeight = "Weight";
    private final static String dbHeight = "Height";

    //String DB variables
    private String cf = "";
    private String phoneNumber = "";
    private String name = "";
    private String lastName = "";
    private String day = "";
    private String month = "";
    private String year = "";
    private String gender = "";
    private String weight = "";
    private String height = "";
    private String type = "";
    private static final int MY_PERMISSIONS_REQUEST = 11;
    private  DBRequestHandler dbRequestHandler;
    private FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("TrackMe");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        dbRequestHandler = new DBRequestHandler();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Data updated successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                if(latitude != null && longitude!= null) {
//                    Toast.makeText(getApplication().getBaseContext(), " current position:\n LA: " + Double.toString(latitude) + " LO: " + Double.toString(longitude), Toast.LENGTH_LONG).show();
//                }
                nav_email = findViewById(R.id.nav_email);
                nav_email.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef;
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);


        nav_phone = headerView.findViewById(R.id.phone_number_header);
        nav_email = headerView.findViewById(R.id.nav_email);
        nav_email.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        UserInfo userInfo = new UserInfo();
        FragmentManager fragmentManager = getSupportFragmentManager();
        //passerUI(userInfo);
        filler();
        //Location Services
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getOldLocation();
        buildFitnessClient();
        weeklyTotal();
        if(currentUser!=null) {
            // OLD
            // docRef = db.collection("users").document(currentUser.getUid());
            docRef = dao.getUserDocument(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document!= null && document.exists()) {
                            Map<String,Object> map = document.getData();
                            if(map != null) {
                                if(map.get("Type") != null){
                                    if(map.get("Type").equals("User")){
                                        navigationView.getMenu().findItem(R.id.nav_makeRequest).setVisible(false);
                                        navigationView.getMenu().findItem(R.id.nav_checkRequests ).setVisible(false);
                                        navigationView.getMenu().findItem(R.id.nav_singleRequest).setVisible(false);
                                    }
                                    if(map.get("Type").equals("Business")){
                                        navigationView.getMenu().findItem(R.id.nav_fitnessLevel).setVisible(false);
                                        navigationView.getMenu().findItem(R.id.nav_requests ).setVisible(false);
                                    }
                                }
                            }
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        fragmentManager.beginTransaction().replace(R.id.fragment, userInfo).commit();

        //subscribeSteps();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mAuth.signOut();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fitnessLevel) {
            FitnessLevel fitnessLevel = new FitnessLevel();
            FragmentManager fragmentManager = getSupportFragmentManager();
            passStepData(fitnessLevel);
            fragmentManager.beginTransaction().replace(R.id.fragment, fitnessLevel).commit();
        } else if (id == R.id.nav_userInfo) {
            UserInfo userInfo = new UserInfo();
            FragmentManager fragmentManager = getSupportFragmentManager();
            passerUI(userInfo);
            fragmentManager.beginTransaction().replace(R.id.fragment, userInfo).commit();

        } else if (id == R.id.nav_requests) {
            Requests requests = new Requests();
            FragmentManager fragmentManager = getSupportFragmentManager();
            passerCF(requests);

            fragmentManager.beginTransaction().replace(R.id.fragment, requests).commit();
        } else if (id == R.id.nav_checkRequests) {
            PastRequests settings = new PastRequests();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, settings).commit();
        }
        else if (id == R.id.nav_makeRequest) {
            GroupRequest groupRequest = new GroupRequest();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, groupRequest).commit();
        }
        else if (id == R.id.nav_singleRequest) {
            SingleRequest singleRequest = new SingleRequest();
            FragmentManager fragmentManager = getSupportFragmentManager();
            passerName(singleRequest);
            fragmentManager.beginTransaction().replace(R.id.fragment, singleRequest).commit();
        }
        else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Hey, i would like you to download TrackMe, to find out more www.TrackMe.com";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TrackMe");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void filler() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = null;

        if (currentUser != null) {
            // OLD
            // docRef = db.collection("users").document(currentUser.getUid());
            docRef = dao.getUserDocument(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Map<String, Object> map = document.getData();
                            if (map != null) {
                                if (map.get(dbPhone) != null) {
                                    nav_phone.setText(map.get(dbPhone).toString());
                                }
                            }
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
    }

    private void getOldLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST);
                return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            dbUpdate();
                            //Toast.makeText(getApplication().getBaseContext(),Double.toString(latitude),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void dbUpdate(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Map<String, Object> user = new HashMap<>();
            user.put("Latitude", latitude);
            user.put("Longitude",longitude);
            // OLD
            // db.collection("users").document(currentUser.getUid()).update(user)
            dao.getUserDocument(currentUser.getUid()).update(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //phoneNumberNavigator.setText(phoneNumber.getText().toString());
                            // Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error writing document", e);
                        }
                    });
        }


    }

    //NEW SECTION

    private void subscribeSteps(){
        if(GoogleSignIn.getLastSignedInAccount(this)!=null) {
            Fitness.getRecordingClient(this, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)))
                    .subscribe(TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Successfully subscribed!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    });
            Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .listSubscriptions(TYPE_STEP_COUNT_DELTA)
                    .addOnSuccessListener(new OnSuccessListener<List<Subscription>>() {
                        @Override
                        public void onSuccess(List<Subscription> subscriptions) {
                            for (Subscription sc : subscriptions) {
                                DataType dt = sc.getDataType();
                                Log.i(TAG, "Active subscription for data type: " + dt.getName());
                            }
                        }
                    });
        }
    }

    private void unsubscribeSteps(){
        Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .unsubscribe(TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully unsubscribed for data type: " );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Subscription not removed
                        Log.i(TAG, "Failed to unsubscribe for data type: " );
                    }
                });

    }
    public void weeklyTotal() {
        readData();
    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Subscribe to some data sources!

                                subscribeSteps();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.w(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.w(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.w(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                    }
                })
                .build();

        //MyGoogleApiClient_Singleton.getInstance(mClient);

    }

    private class VerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            Log.i(TAG, "step count");

            total = 0;

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                total = Objects.requireNonNull(totalSet).isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }

            Log.i(TAG, "Total steps: " + total);



            return null;
        }

    }

    private void readData() {
        new VerifyDataTask().execute();
    }

    private void passStepData(FitnessLevel fitnessLevel){
        Bundle bundle=new Bundle();
        bundle.putInt("steps", total);
        bundle.putString(dbWeight,weight);
        bundle.putString(dbHeight,height);
        //set Fragmentclass Arguments
        fitnessLevel.setArguments(bundle);
    }
    private void passerUI(final UserInfo userInfo){
        Bundle bundle=new Bundle();
        bundle.putString(dbCF, Objects.requireNonNull(cf));
        bundle.putString(dbday, Objects.requireNonNull(day));
        bundle.putString(dbMonth, Objects.requireNonNull(month));
        bundle.putString(dbYear, Objects.requireNonNull(year));
        bundle.putString(dbGender, Objects.requireNonNull(gender));
        bundle.putString(dbPhone, Objects.requireNonNull(phoneNumber));
        bundle.putString(dbLastName, Objects.requireNonNull(lastName));
        bundle.putString(dbName, Objects.requireNonNull(name));
        bundle.putString("Type",Objects.requireNonNull(type));
        userInfo.setArguments(bundle);
    }
    private void passerName(final SingleRequest userInfo){
        Bundle bundle=new Bundle();
        bundle.putString(dbName, Objects.requireNonNull(name));
        userInfo.setArguments(bundle);
    }
    private void passerCF(final Requests userInfo){
        Bundle bundle=new Bundle();
        bundle.putString(dbCF, Objects.requireNonNull(cf));
        bundle.putString(dbday, Objects.requireNonNull(day));
        bundle.putString(dbMonth, Objects.requireNonNull(month));
        bundle.putString(dbYear, Objects.requireNonNull(year));
        bundle.putString(dbGender, Objects.requireNonNull(gender));
        bundle.putString(dbPhone, Objects.requireNonNull(phoneNumber));
        bundle.putString(dbLastName, Objects.requireNonNull(lastName));
        bundle.putString(dbName, Objects.requireNonNull(name));
        userInfo.setArguments(bundle);
    }
    //SETTERS
    public void setCf(String cf) {
        this.cf = cf;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setType(String type) {
        this.type = type;
    }




//    private void setUI(UserInfo userInfo){
//        userInfo.setArguments(bundle);
//    }




}
