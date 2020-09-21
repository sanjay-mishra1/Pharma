package com.example.pharma.address;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.pharma.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SelectPinCodeActivity extends AppCompatActivity {
    String pincodeApi="https://api.postalpincode.in/pincode/";
    private TextView addressText;
    private EditText pincodeEdit;
    private Button nextBt;
    private RequestQueue volley;
    private StringBuilder pincode;
    private ProgressBar progressBar;
    private Button checkbt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_pincode_screen);
        // enable white status bar with black icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        pincodeEdit=findViewById(R.id.pincode);
        addressText=findViewById(R.id.address);
        nextBt=findViewById(R.id.nextBt);
        volley=Volley.newRequestQueue(this);
        progressBar=findViewById(R.id.progressbar);
        pincode=new StringBuilder();
        checkIfDataReceived();
        checkbt=findViewById(R.id.actionButton);
        nextBt.setOnClickListener(v -> {
            Intent intent=new Intent(SelectPinCodeActivity.this,SelectAddressFromMapActivity.class);
            intent.putExtra("pincode",pincode.toString());
            intent.putExtra("pincode_name",addressText.getText().toString().trim());
            startActivity(intent);
        });
        pincodeEdit.setOnEditorActionListener((v, actionId, event) -> {
            boolean handle=false;
            if(actionId== EditorInfo.IME_ACTION_SEARCH)
            {
                initiatePincodeSearch();
                handle=true;
            }
            return handle;
        });
    }
    private void checkIfDataReceived() {
        Intent intent=getIntent();
        String pin=intent.getStringExtra("pincode");
        if (pin!=null){
            pincode.append(pin);
            pincodeEdit.setText(pincode);
            String name=intent.getStringExtra("pincode_name");
            if (name!=null) {
                addressText.setText(name);
                addressText.setVisibility(View.VISIBLE);
            }
            nextBt.setEnabled(true);
        }
    }

    public void checkPincode(View view) {
        initiatePincodeSearch();
    }

    private void initiatePincodeSearch() {
        pincode.delete(0,pincode.length());
        pincode.append(pincodeEdit.getText().toString().trim());
        if (pincode.length()>0) {
            checkbt.setText("");
            progressBar.setVisibility(View.VISIBLE);
            JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET, pincodeApi + pincode, null, response -> {
                Log.e("Volley", "Response " + response);
                checkPincodeResponse(response);
            }, error -> {
                progressBar.setVisibility(View.GONE);
                checkbt.setText(R.string.check);
                error.printStackTrace();
                showErrorSnackBar("Failed to check the pin code. Please check your internet connection");
                Log.e("Volley", "Checksum not received error occur " + error.toString());
            });
            volley.add(jsonArrayRequest);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (volley!=null)  volley.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (volley!=null) volley.start();
    }

    @Override
    public void finish() {
        super.finish();
        if (volley!=null)
            volley.stop();
    }

    private void checkPincodeResponse(JSONArray response) {
        progressBar.setVisibility(View.GONE);
        checkbt.setText(R.string.check);
        try {
            if (response.getJSONObject(0).get("Status").toString().equals("Error"))
                showErrorSnackBar("Invalid pin code");
            else {
                nextBt.setEnabled(true);
                JSONObject jsonObject=response.getJSONObject(0).getJSONArray("PostOffice").getJSONObject(0);
                String name=jsonObject.getString("Name");
                String district=jsonObject.getString("District");
                addressText.setText(String.format("%s, %s", name, district));
                addressText.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            showErrorSnackBar("An error occurred while checking your pincode.");
            e.printStackTrace();
        }
    }

    private void showErrorSnackBar(String msg){
        nextBt.setEnabled(false);
        addressText.setVisibility(View.GONE);
        Snackbar snackbar=Snackbar.make( findViewById(R.id.parent),msg,Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_FADE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    public void backClicked(View view) {
        finish();
    }
}
