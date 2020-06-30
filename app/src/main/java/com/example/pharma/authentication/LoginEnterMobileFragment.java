package com.example.pharma.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class LoginEnterMobileFragment extends Fragment {
    private EditText editText;
    private ProgressBar progressbar;
    private Button button;
//    private FirebaseApp firebaseApp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_mobile_fragment_enter_mobile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        firebaseApp=new FirebaseCustomAuth().loadFirebase(getContext(),String.valueOf(System.currentTimeMillis()));
        editText = Objects.requireNonNull(getView()). findViewById(R.id.editTextPhone);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle=false;
                if(actionId== EditorInfo.IME_ACTION_NEXT)
                {
                    checkFields();
                    handle=true;
                }
                return handle;
            }
        });
        progressbar= Objects.requireNonNull(getView()). findViewById(R.id.action_button_progressbar);
        button=view.findViewById(R.id.actionButton);
        button.setOnClickListener(view1 -> checkFields());
        View country_code=getView().findViewById(R.id.country_code);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }@Override  public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().isEmpty())
                    country_code.setVisibility(View.GONE);
                else country_code.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.signup).setOnClickListener(v -> startActivity(new Intent(getActivity(),SignupActivity.class)));
        view.findViewById(R.id.google).setOnClickListener(v -> startActivity(new Intent(getActivity(),GoogleAuthentication.class)));
        view.findViewById(R.id.facebook).setOnClickListener(v -> startActivity(new Intent(getActivity(),FacebookAuthentication.class)));
        view.findViewById(R.id.mail).setOnClickListener(v -> startActivity(new Intent(getActivity(),LoginEmailActivity.class)));
    }
    private void checkFields(){
        String number = editText.getText().toString().trim();
        if (number.isEmpty() || number.length() < 10) {
            showAlertDialog("Invalid mobile number");
            editText.requestFocus();
            return;
        }
        boolean extra= Objects.requireNonNull(getActivity()).getIntent().getBooleanExtra("EXTRA",false);
        chekUser(getString(R.string._91)+editText.getText().toString().trim(),extra);
    }
    private void chekUser(String phonenumber,boolean isChangeMobile) {
        button.setText("");
        progressbar.setVisibility(View.VISIBLE);
        Log.e("Phonenumber",phonenumber+" extra "+isChangeMobile);
        FirebaseDatabase.getInstance().getReference("Customers").
                orderByChild("mobile").startAt(phonenumber).endAt(phonenumber).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       Objects.requireNonNull(getView()). findViewById(R.id.action_button_progressbar).setVisibility(View.GONE);
                       if(isChangeMobile) {
                           if (dataSnapshot.getChildrenCount() >= 1) {
                               showAlertDialog("Mobile number already exist. Please enter a new mobile number.");
                           }else
                            {       Bundle bundle = new Bundle();
                               bundle.putString("MOBILE", phonenumber);
                               NavHostFragment.findNavController(LoginEnterMobileFragment.this)
                                       .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
                                }
                       }else{
                           if (dataSnapshot.getChildrenCount() == 1) {
                               Bundle bundle = new Bundle();
                               bundle.putString("MOBILE", phonenumber);
                               NavHostFragment.findNavController(LoginEnterMobileFragment.this)
                                       .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
                           }else showAlertDialog("Mobile number not found.");
                       }
                       progressbar.setVisibility(View.GONE);
                       button.setText(R.string.login);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showAlertDialog(String msg) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(msg);
        alert.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            Log.e("Alert dialog","onclick***********************");
        });
        alert.setOnDismissListener(dialogInterface -> Log.e("Alert dialog","Dismiss***********************"));

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }


}
