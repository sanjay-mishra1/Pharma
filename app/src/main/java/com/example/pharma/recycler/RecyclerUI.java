package com.example.pharma.recycler;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.model.MedicineModel;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerUI {
    private   FilterAdapter adapter;
    private List<MedicineModel> data;

       public RecyclerUI(Context context, RecyclerView recyclerView, ArrayList<MedicineModel> data){
           this.data=data;
           RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
           recyclerView.setLayoutManager(mLayoutManager);
           recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0,context), true));
           recyclerView.setItemAnimator(new DefaultItemAnimator());
           recyclerView.setAdapter(adapter);

    }
    public static int dpToPx(int dp, Context context) {
        Resources r =context. getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static class FilterAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
        List<MedicineModel> dataList;
        Context context;
        Fragment fragment;
        public FilterAdapter(ArrayList<MedicineModel> data, Context context){
            this.dataList=data;
            this.context=context;
            this.fragment=fragment;
        }
        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new CategoryViewHolder(LayoutInflater.from(context).
                        inflate(R.layout.medicine_layout,viewGroup,false));

        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            MedicineModel model=dataList.get(position);
            holder.setImage(model.getMedicine_img());
            holder.setMedicine_name(model.getMedicine_name());
            holder.setPrice(model.getMedicine_price());
            holder.setCompanyName(model.getMedicine_company());
            holder.setMedicineBoxListener(model,fragment);
        }

        @Override
        public int getItemCount() {
            if (dataList==null)
                return 0;
            else
                return (int) dataList.size();
        }
    }
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

          public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            }
            else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

}
