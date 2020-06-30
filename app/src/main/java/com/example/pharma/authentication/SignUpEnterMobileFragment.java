package com.example.pharma.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.service.autofill.VisibilitySetterAction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharma.Constants;
import com.example.pharma.HomeActivity;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.SignUpModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import static android.content.Context.MODE_PRIVATE;

public class SignUpEnterMobileFragment extends Fragment {
    private EditText editText;
    private ProgressBar progressbar;
    private Button button;
    private Button resendBt;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String phonenumber;
    private Button signinBt;
    private String verificationId;
    private SignUpModel model;
//    private FirebaseApp firebaseApp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signup_fragment_enter_mobile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        firebaseApp=new FirebaseCustomAuth().loadFirebase(getContext(),String.valueOf(System.currentTimeMillis()));
        editText = Objects.requireNonNull(getView()). findViewById(R.id.editTextPhone);
        progressbar= Objects.requireNonNull(getView()). findViewById(R.id.action_button_progressbar);
        button=view.findViewById(R.id.actionButton);
        button.setText(R.string.signup);
        receive();
        button.setOnClickListener(view1 -> {
            String number = editText.getText().toString().trim();
            if (number.isEmpty() || number.length() < 10) {
                showAlertDialog("Invalid mobile number");
                editText.requestFocus();
                return;
            }
            chekUser(getString(R.string._91)+editText.getText().toString().trim());
        });
        view.findViewById(R.id.resetEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOnClick();
            }
        });
    }

    private void receive() {
       model= (SignUpModel) Objects.requireNonNull(getArguments()).getSerializable("SIGNUP_CRED");
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
    }

    private void chekUser(String mobilenumber) {
        button.setText("");
        progressbar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("Customers").
                orderByChild("mobile").startAt(mobilenumber).endAt(mobilenumber+"\uf8ff").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Objects.requireNonNull(getView()). findViewById(R.id.action_button_progressbar).setVisibility(View.GONE);
                            if (dataSnapshot.getChildrenCount() == 0) {
                                getView().findViewById(R.id.otpView).setVisibility(View.VISIBLE);
                                getView().findViewById(R.id.mobileView).setVisibility(View.GONE);

                                loadOTP();
                            }
                        else showAlertDialog("Mobile number already exist.");
                        progressbar.setVisibility(View.GONE);
                        button.setText(R.string.signup);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressbar.setVisibility(View.GONE);
                        button.setText(R.string.signup);
                        showAlertDialog("An error occurred");
                    }
                });
    }

    private void loadOTP() {
        resendBt= Objects.requireNonNull(getView()).findViewById(R.id.resendSignIn);
        signinBt=getView().findViewById(R.id.buttonSignIn);

        mAuth = FirebaseAuth.getInstance();
        progressBar = getView().findViewById(R.id.progressbar);
        editText =getView(). findViewById(R.id.editTextCode);
        sendVerificationCode(phonenumber);
        Button signup=getView().findViewById(R.id.buttonSignIn);
        signup.setOnClickListener(view1 -> {
            String code = editText.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {

                showAlertDialog("Enter code...");
                editText.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);
    });
    }


    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneCredential(credential);
        }catch (Exception e){
            progressBar.setVisibility(View.GONE);
            showAlertDialog("Invalid code...");
        }

    }


    private void signInWithCredential() {

        mAuth.createUserWithEmailAndPassword(model.getEmail(),model.pwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.VISIBLE);
                        signinBt.setEnabled(false);
                        signinBt.setText(R.string.creating_account);
                        storeData(mAuth.getUid());
                    } else
                        showAlertDialog(Objects.requireNonNull(task.getException()).getMessage());
                });
    }
    private void signInWithPhoneCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.VISIBLE);
                        signinBt.setEnabled(false);
                        signinBt.setText(R.string.signing_you_in);
                        signInWithCredential();
                    } else
                        showAlertDialog(Objects.requireNonNull(task.getException()).getMessage());
                });
    }

    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance(mAuth).verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack

        );

    }
    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.e("OTP","Code sent");
            token=forceResendingToken;
            verificationId = s;
            countDown();
            progressBar.setVisibility(View.GONE);
            signinBt.setEnabled(true);
            signinBt.setTextColor(getResources().getColor(R.color.white));
            if(Objects.requireNonNull(getActivity()). getIntent().getBooleanExtra("EXTRA", false))
            {   if(Constants.uid==null)
                Constants.uid= Objects.requireNonNull(getContext()). getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE).getString("UID",null);
                signinBt.setText(R.string.change_mobile);
            }
            else signinBt.setText(R.string.signin);
            resendBt.setVisibility(View.VISIBLE);
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            progressBar.setVisibility(View.GONE);
            if (code != null) {
                Log.e("OTP","Code verfication =>"+code);
                editText.setText(code);

                verifyCode(code);
            }else {
             showAlertDialog("Unable to send the verification code.");

            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            progressBar.setVisibility(View.GONE);
        }
    };
    private void storeData(String uid){
        model.pwd=null;
        model.setMobile(phonenumber);
        FirebaseDatabase.getInstance().getReference("Customers").child(uid).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Intent intent=new Intent(getActivity(), HomeActivity.class);
                    SharedPreferences sharedPreferences= Objects.requireNonNull(getContext()). getSharedPreferences("CRED",MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPreferences.edit();
                    edit.putString("MOBILE", (phonenumber));
                    edit.putString("NAME", model.getName());
                    edit.putString("UID", mAuth.getUid());
                    edit.apply();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    mAuth.signOut();
                    showAlertDialog("An error occurred while creating your account. Please try again after sometime.");
                }
            }
        });
    }
    public void resendOnClick() {
        Objects.requireNonNull(getView()).findViewById(R.id.resendSignIn).setOnClickListener(v -> {
            try {
                progressBar.setVisibility(View.VISIBLE);
                PhoneAuthProvider.getInstance(mAuth).verifyPhoneNumber(
                        phonenumber,
                        60,
                        TimeUnit.SECONDS,
                        TaskExecutors.MAIN_THREAD,
                        mCallBack,
                        token
                );
                signinBt.setTextColor(getResources().getColor(R.color.disable_color));
                signinBt.setText(R.string.sending_code);
                signinBt.setEnabled(false);
                resendBt.setText("00:60");
                resendBt.setTextColor(getResources().getColor(R.color.disable_color));
                resendBt.setEnabled(false);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

    }
    private void countDown(){
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendBt.setText(String.format(Locale.UK,"00:%d", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                resendBt.setEnabled(true);
                resendBt.setTextColor(getResources().getColor(R.color.white));
                resendBt.setText(R.string.resend_code);
            }
        }.start();
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


