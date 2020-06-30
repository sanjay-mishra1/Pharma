package com.example.pharma.extra;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharma.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class InfoBoxBottomSheet extends BottomSheetDialogFragment {
    private int icon;
    private String title;
    private String description;
    private String action;
    private String actionText;
    public InfoBoxBottomSheet(int iconDrawable, String title, String description, String action,String actionText) {
        this.icon = iconDrawable;
        this.title = title;
        this.description = description;
        this.action = action;
        this.actionText=actionText;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL,R.style.CustomBottomSheetTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_box_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      try {
          TextView titleText = view.findViewById(R.id.title);
          titleText.setText(title);
          if (icon != 0)
              titleText.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(icon), null, null, null);
          TextView descText = view.findViewById(R.id.desc);
          descText.setText(description);
          Button actionBt = view.findViewById(R.id.actionButton);
          actionBt.setText(actionText);
          view.findViewById(R.id.cancel).setOnClickListener(v -> dismiss());
          view.findViewById(R.id.actionButton).setOnClickListener(v -> setActionButton());
      }catch (Exception e){e.printStackTrace();
          Log.e("Infobox","->icon "+icon+"->title "+title+"->desc "+description+"->actiontext "+actionText);
            Toast.makeText(getActivity(),"An error occurred",Toast.LENGTH_SHORT).show();
            dismiss();
      }
    }

    private void setActionButton() {
        switch (action){
            case "call_for_test":
                makeACall();
                break;
            default:
                dismiss();
        }
    }

    private void makeACall() {
            if (isHaveCallPermission()) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("+919893909516"));
                startActivity(callIntent);
            }else{
                Toast.makeText(getContext(),"Not have call permission", Toast.LENGTH_SHORT).show();
            }
    }
    private boolean isHaveCallPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);
                return false;
            }else{

                return true;
            }
        }else{
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
