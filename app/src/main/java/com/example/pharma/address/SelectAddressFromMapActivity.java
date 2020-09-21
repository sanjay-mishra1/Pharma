package com.example.pharma.address;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.model.AddressModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public class SelectAddressFromMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager manager;
    private boolean isMapLoaded=false;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private MaterialButton gpsBt;
    private double lat;
    private double lng;
    private Marker marker;
    private String pincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address_from_map);
        if (Constants.uid==null)
            Constants.uid=  getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        TextInputEditText pincodeEdit=findViewById(R.id.pincode);
        ((TextInputLayout)findViewById(R.id.pincode_layout)).setHelperText(getIntent().getStringExtra("pincode_name")==null? "Not found":getIntent().getStringExtra("pincode_name"));
        pincode=getIntent().getStringExtra("pincode");
        pincodeEdit.setText(pincode==null? "Not found":pincode);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        gpsBt= findViewById(R.id.enable_gps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        RelativeLayout addressCard =findViewById(R.id.card_view);
        bottomSheetBehavior = BottomSheetBehavior.from(addressCard);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (!isMapLoaded){
                    if (newState==BottomSheetBehavior.STATE_DRAGGING){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }else{
                    if (newState==BottomSheetBehavior.STATE_DRAGGING){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_DRAGGING);
                    }else if (newState==BottomSheetBehavior.STATE_EXPANDED){
                        gpsBt.setVisibility(View.INVISIBLE);
                    }else gpsBt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,SelectPinCodeActivity.class);
        intent.putExtra("pincode",pincode);
        intent.putExtra("pincode_name",getIntent().getStringExtra("pincode_name"));
        startActivity(intent);
        super.onBackPressed();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[]permission={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this,
                    permission,
                    101);

            return;
        }
        LatLng latLng=new LatLng(0,0);
        MarkerOptions markerOptions=new MarkerOptions().position(latLng);
        marker = mMap.addMarker(markerOptions);
        requestLocation();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                requestLocation();

                //findViewById(R.id.doneBt).setVisibility(View.VISIBLE);
            }catch (Exception e){
                Toast.makeText(this,"Not have Location permissions=>"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("MissingPermission")
    void requestLocation(){
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {   findViewById(R.id.enable_gps).setVisibility(View.VISIBLE);
            Toast.makeText(this,"Please on your location",Toast.LENGTH_LONG).show();
        }else{
            isMapLoaded=true;
            bottomSheetBehavior.setPeekHeight(800,true);
            gpsBt.setText(R.string.add_address);
            gpsBt.setIcon(getResources().getDrawable(R.drawable.ic_add_location_black_24dp));
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        Log.e("LOC","manager=>"+manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc!=null){
            loadMap(loc.getLatitude(),loc.getLongitude());
        }else{
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
            Log.e("LOC","manager=>"+manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc!=null) {
                loadMap(loc.getLatitude(),loc.getLongitude());
            }else
                Log.e("LOC","Network also. It is null");
        }
    }
    void loadMap(double lat,double lng){
        manager.removeUpdates(this);
        this.lat=lat;
        this.lng=lng;
        //LatLng marker = new LatLng(lat, lng);
        marker.setPosition(new LatLng(lat, lng));

        mMap.setOnMarkerClickListener(null);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng),15));
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc!=null){
            Log.e("LOC","Found location changed "+loc);
            requestLocation();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.e("LOC","Found status changed s=>"+s+"=>"+bundle);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.e("LOC","Provider enabled s=>"+s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.e("LOC","Provider disabled s=>"+s);
    }

    public void enableGPSClicked(View view) {
        if (!isMapLoaded) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }else { bottomSheetBehavior.setFitToContents(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
    public void addNewAddressClicked(View view) {
        manager=null;
        TextInputEditText houseNoEdit=findViewById(R.id.flat_no);
        TextInputEditText line1Edit=findViewById(R.id.address1);
        TextInputEditText line2Edit=findViewById(R.id.address2);
        TextInputEditText landMarkEdit=findViewById(R.id.nearestLandMark);
        String flat= Objects.requireNonNull(houseNoEdit.getText()).toString().trim(),
                line1= Objects.requireNonNull(line1Edit.getText()).toString().trim(),
                line2= Objects.requireNonNull(line2Edit.getText()).toString().trim(),
                landmark= Objects.requireNonNull(landMarkEdit.getText()).toString().trim();
//                pincode= getIntent().getStringExtra("pincode");
        if (flat.isEmpty()){
            TextInputLayout layout=findViewById(R.id.flat_no_layout);
            layout.setError("House Number required");
            layout.requestFocus();
            return;
        }
        if (line1.isEmpty()){
            TextInputLayout layout=findViewById(R.id.address1_layout);
            layout.setError("Address required");
            layout.requestFocus();
            return;
        }
        if (landmark.isEmpty()){
            TextInputLayout layout=findViewById(R.id.landmarks_layout);
            layout.setError("Nearest landmark is required");
            layout.requestFocus();
            return;
        }
        if (pincode==null || pincode.isEmpty()){
            TextInputLayout layout=findViewById(R.id.pincode_layout);
            layout.setError("Pin code is required");
            layout.requestFocus();
            return;
        }
        String address=flat+" , "+line1+" , "+line2+" Pincode : "+pincode+"\nLandmark : "+landmark;
        AddressModel model=new AddressModel(address,lat,lng);
        ((MaterialButton)view).setText(R.string.saving);
        view.setEnabled(false);

        FirebaseDatabase.getInstance().getReference("Customers").child(Constants.uid).child("Address")
                .child(String.valueOf(System.currentTimeMillis())).setValue(model).addOnCompleteListener(task -> {
                    if (task.isCanceled())
                    {
                        Snackbar.make(findViewById(R.id.parent), R.string.address_error_message,Snackbar.LENGTH_LONG).show();
                        ((MaterialButton)view).setText(R.string.add_new_address);
                        view.setEnabled(true);

                    }
                    else{
                        SharedPreferences sharedPreferences=getSharedPreferences("Address",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("Address_Text",address);
                        editor.putString("Address_Coordinate",lat+","+lng);
                        editor.apply();
                        finish();
                    }
                });
    }

    public void reLocateClicked(View view) {
        requestLocation();
    }
}
