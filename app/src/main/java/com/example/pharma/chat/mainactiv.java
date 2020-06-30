package com.example.pharma.chat;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pharma.R;

import androidx.recyclerview.widget.RecyclerView;


class mainactiv {


    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        View mView;

        FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }



        public ImageView getImageView() {
            return mView.findViewById(R.id.message_body_img);
        }

        public void setName(String name) {

            TextView food_name = mView.findViewById(R.id.title);
            food_name.setText(name);

        }



        void setcanteenName(String Q) {
          try {
              TextView food_q = mView.findViewById(R.id.text_message_name);

              food_q.setText(Q);
          }catch (Exception ignored){}
        }



        public void setMessage(String Message) {
            try {

                TextView textView = mView.findViewById(R.id.text_message_body);
                if (!Message.equals("")) {
                    textView.setText(Message);
                    textView.setVisibility(View.VISIBLE);
                }
            } catch (Exception ignored) {
            }

        }
        public void setTime(String time) {
            try {
                TextView textView = mView.findViewById(R.id.text_message_time);
                // time=time.substring(time.indexOf("||")+3,time.length());
                textView.setText(time);
            } catch (Exception ignored) {
            }

        }

        void setDate2(String time) {
            try {
                TextView textView = mView.findViewById(R.id.date);
                textView.setText(time);
            } catch (Exception ignored) {
            }

        }

        void setImageMessage(String image, Context context) {
            try {
                ImageView food_image = mView.findViewById(R.id.message_body_img);
                Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(image).into(food_image);

            } catch (Exception ignored) {
            }

        }

        void setAdminImage() {
            try {
                ImageView img = mView.findViewById(R.id.image_message_profile);
                //Glide.with(mView).load(CanImage).apply(RequestOptions.circleCropTransform()).into(img);

            } catch (Exception ignored) {
            }
        }

        public void setDate(String date_string) {
            TextView textView = mView.findViewById(R.id.date);
            textView.setText(date_string.substring(0, date_string.indexOf("||") - 1));

        }

        void setstatus(boolean status, Context context) {
            if (!status){
                Glide.with(context).applyDefaultRequestOptions(RequestOptions.noTransformation())
                        .load(R.drawable.sent).into((ImageView) itemView.findViewById(R.id.status));
            }
        }
    }
}



