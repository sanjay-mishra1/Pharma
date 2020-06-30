package com.example.pharma.orders;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.imgeslider.AdminSliderAdapter;
import com.example.pharma.model.ImageBasedModel;
import com.example.pharma.view_image_layout;
import com.google.android.material.button.MaterialButton;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class ImageDataDetailActivity extends AppCompatActivity{
    private ArrayList<String> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_prescription_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        findViewById(R.id.uploadFirstImageBt).setVisibility(View.GONE);
        findViewById(R.id.dataField).setVisibility(View.VISIBLE);
        ImageBasedModel model= (ImageBasedModel) getIntent()
                .getSerializableExtra("DATA");
        if (model != null) {
            list=model.getImages();
        String from=getIntent().getStringExtra("FROM");
        if (from.contains("Test"))
            loadTestReportData(model);
        else loadPrescriptionData(model);

        loadViewPager();
        }else Log.i("ImageDataDetail","Received no data");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void loadPrescriptionData(ImageBasedModel model) {
        TextView time=findViewById(R.id.time_bottom);
        time.setVisibility(View.VISIBLE);
        time.setText(getFormattedTime(Long.parseLong(model.getID())));
        TextView id=findViewById(R.id.id);
        id.setText("#"+model.getID());
        setPresStatus(model.getStatus());
        if (model.getNote()!=null){
            TextView note=findViewById(R.id.note);
            note.setText((String) model.getNote());
            note.setVisibility(View.VISIBLE);
        }
        if (model.getOrder()!=null){
            findViewById(R.id.orderListTitle).setVisibility(View.VISIBLE);
            findViewById(R.id.doneUploadBt).setVisibility(View.GONE);
            setUpRecycler(model.getOrder());
        }else{MaterialButton button=findViewById(R.id.doneUploadBt);
            button.setText("Delete");
            button.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    private void setUpRecycler(String orders) {
        SimpleAdapter adapter=new SimpleAdapter(orders.split(";"));
        RecyclerView recyclerView= findViewById(R.id.listview);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }
    private String getFormattedTime(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy hh:mm aa", Locale.UK);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return "Added on "+dateFormat.format(calendar.getTime());
    }
    private void setPresStatus(long status) {
        MaterialButton button=findViewById(R.id.status);
        button.setVisibility(View.VISIBLE);
        if (ImageBasedModel.Approved==status) {
            button.setText(R.string.approved);
            button.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            button.setTextColor(getResources().getColor(R.color.colorAccent));
        }else if (ImageBasedModel.Pending==status) {
            button.setText(R.string.pending);
            button.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            button.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else if (ImageBasedModel.Cancelled==status) {
            button.setText(R.string.cancelled);
            button.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
            button.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void loadTestReportData(ImageBasedModel model) {
        TextView time=findViewById(R.id.time_top);
        time.setVisibility(View.VISIBLE);
        time.setText(getFormattedTime(Long.parseLong(model.getID())));
        TextView id=findViewById(R.id.id);
        id.setText(String.format("#%s", model.getID()));
        TextView name=findViewById(R.id.name);
        name.setText(model.getName());
        name.setVisibility(View.VISIBLE);
        if (model.getNote()!=null){
            TextView note=findViewById(R.id.note);
            note.setText((String)model.getNote());
            note.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.doneUploadBt).setVisibility(View.GONE);
    }

    private void loadViewPager() {
        ViewPager viewPager=findViewById(R.id.frame);
            AdminSliderAdapter adapter = new AdminSliderAdapter(this,
                    list);
            viewPager.setAdapter(adapter);
    }

    public void seeImages(View view) {
        Intent intent=new Intent(this, view_image_layout.class);
        intent.putExtra("DATA",list);
        startActivity(intent);
    }

    public  class SimpleAdapter extends RecyclerView.Adapter<ViewHolder>{
        private String[] list;

        public SimpleAdapter(String[] list){
            this.list=list;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(ImageDataDetailActivity.this)
                    .inflate(R.layout.list_button_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
           TextView textView= holder.itemView.findViewById(R.id.text);
           textView.setText(list[position]);
        }

        @Override
        public int getItemCount() {
            return list.length;
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
