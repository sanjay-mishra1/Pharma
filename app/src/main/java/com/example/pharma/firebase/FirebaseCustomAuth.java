package com.example.pharma.firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.Objects;

public class FirebaseCustomAuth {

    public FirebaseApp loadFirebase(Context context, String name) {
        // Manually configure Firebase Options. The following fields are REQUIRED:
//   - Project ID
//   - App ID
//   - API Key
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId("tablethuts")
                .setApplicationId("1:614759740891:android:b26667cbb306666f71e3fe")
                .setApiKey("AIzaSyA5Rq36vEKQmsaHThrPX69BHHBNIVrKRuo")
                .setDatabaseUrl("https://tablethuts.firebaseio.com/")
                // setStorageBucket(...)
                .build();// Initialize with secondary app
        FirebaseApp.initializeApp(Objects.requireNonNull(context) /* Context */, options, name);
// Retrieve secondary FirebaseApp
        return  FirebaseApp.getInstance(name);
    }
    public FirebaseApp loadCustomFirebase(Context context,String ...dbName) {
        String name= String.valueOf(System.currentTimeMillis());
        FirebaseOptions.Builder builder = new FirebaseOptions.Builder()
                //.setProjectId("tablethuts-medicines")
                .setProjectId(dbName[0])
                .setApplicationId("1:614759740891:android:b26667cbb306666f71e3fe")
                .setApiKey("AIzaSyA5Rq36vEKQmsaHThrPX69BHHBNIVrKRuo")
                .setDatabaseUrl("https://" + dbName[0] + ".firebaseio.com/");
        if (dbName.length>1)
            builder.setStorageBucket(dbName[1]+".appspot.com");

        FirebaseOptions options=builder.build();
        FirebaseApp.initializeApp(Objects.requireNonNull(context) /* Context */, options, name);
// Retrieve secondary FirebaseApp
        return  FirebaseApp.getInstance(name);
    }
}
