package com.example.pharma.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharma.Constants;
import com.example.pharma.HomeActivity;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
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

public class LoginOTPFragment extends Fragment {
    private String verificationId;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText editText;
    private String phonenumber;
    private Button resendBt;
    private Button signinBt;
    private PhoneAuthProvider.ForceResendingToken token;
//    private FirebaseApp firebaseApp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_mobile_fragment_enter_otp, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resendBt=view.findViewById(R.id.resendSignIn);
        signinBt=view.findViewById(R.id.buttonSignIn);
//        firebaseApp=new FirebaseCustomAuth().loadFirebase(getContext(),String.valueOf(System.currentTimeMillis()));
        mAuth = FirebaseAuth.getInstance();
        progressBar = view.findViewById(R.id.progressbar);
        editText =view. findViewById(R.id.editTextCode);

        phonenumber = getArguments().getString("MOBILE");
        sendVerificationCode(phonenumber);

        view.findViewById(R.id.buttonSignIn).setOnClickListener(view1 -> {
            String code = editText.getText().toString().trim();

            if (code.isEmpty() || code.length() < 6) {
                editText.setError("Enter code...");
                editText.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);
        });
        view.findViewById(R.id.resendSignIn).setOnClickListener(v -> resendOnClick());
    }
    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithCredential(credential);
        }catch (Exception e){
            progressBar.setVisibility(View.GONE);
            editText.setError("Invalid code...");
        }

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.VISIBLE);
                        signinBt.setEnabled(false);
                        signinBt.setText(R.string.signing_you_in);
                        checkUser();


                    } else {
                        Toast.makeText(getContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
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
                automaticLogin();

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
    private void automaticLogin(){
        progressBar.setVisibility(View.VISIBLE);
        signinBt.setEnabled(false);
        resendBt.setEnabled(false);
        editText.getLayoutParams().height=dpToPx(35);
        editText.getLayoutParams().width=dpToPx(10);
        editText.requestLayout();
        TextView textView= Objects.requireNonNull(getView()).findViewById(R.id.textView1);
        textView.setText(String.format("Mobile Number %s Detected Automatically", phonenumber));
        signinBt.setText(R.string.signing_you_in);
        editText.setEnabled(false);
        editText.setText(phonenumber);
        FirebaseAuth.getInstance().signInWithEmailAndPassword("lcxzfcko1gdewpbf0nbthpbljag2@snjchilledwater.com"
                ,"Nntd7Ha4C4SkY2cEs2fMdJhZVg73").addOnSuccessListener(authResult -> FirebaseDatabase.getInstance().getReference().child("Customers").orderByChild("MOBILE").
                        startAt(phonenumber).endAt(phonenumber+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            loadData(dataSnapshot);
                        else{mAuth.signOut();
                            showAlertDialog("Mobile number does not exist. Please enter a valid mobile number." +
                                    "\nIf this message looks like an error then please contact your admin for further assistance for your account retrieval.",false);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                })).addOnFailureListener(e ->
                showAlertDialog("An Error occurred while signing you in the app. Please try again after sometime.",false));
    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void showAlertDialog(String msg, boolean b) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(msg);
        alert.setPositiveButton("OK", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            if (b){
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }//else finish();
        });
        alert.setOnDismissListener(dialogInterface -> Log.e("Alert dialog","Dismiss***********************"));

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void checkUser(){
        FirebaseDatabase.getInstance().getReference().child("Customers").orderByChild("mobile").
                startAt(phonenumber).endAt(phonenumber+"\uf8ff").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e(getClass().getCanonicalName(),"user check");

                        if(!dataSnapshot.exists())
                        {    //if new mobile is used
                            if(Objects.requireNonNull(getActivity()).getIntent().getBooleanExtra("EXTRA",false)) changeMobile();
                            //else ();
                        }else{//if mobile number exist and requested to change mobile
                            if(Objects.requireNonNull(getActivity()). getIntent().getBooleanExtra("EXTRA",false)){
                                //if changed mobile number already exist
                                showAlertDialog("Mobile number already exist. Please enter a new mobile number.",true);
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {//if already exist
                                loadData(dataSnapshot);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),"An error occured",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        //finish();
                    }
                });
    }
    private void loadData(DataSnapshot dataSnapshot){
        for(DataSnapshot d:dataSnapshot.getChildren()) {
            SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()). getSharedPreferences("USER_CREDENTIALS", MODE_PRIVATE);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("MOBILE_NUMBER", phonenumber);
            edit.putString("NAME", (String) d.child("name").getValue());
            edit.putString("UID", d.getKey());
            edit.putString("IMG", (String) d.child("user_img").getValue());
            edit.apply();
        }
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    private void changeMobile(){
        SharedPreferences sharedPreferences= Objects.requireNonNull(getActivity()). getSharedPreferences("USER_CREDENTIALS",MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("MOBILE_NUMBER",phonenumber);
        DatabaseReference databaseReference= FirebaseDatabase.
                getInstance().getReference("Customers/"+ Constants.uid);
        databaseReference.child("MOBILE").setValue(phonenumber);
        edit.apply();
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
                Log.e("ResendError","Mo="+phonenumber+" token"+token);
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

}
