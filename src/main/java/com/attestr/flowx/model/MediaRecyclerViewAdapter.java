package com.attestr.flowx.model;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.attestr.flowx.R;
import com.attestr.flowx.listener.MediaImagesListener;
import com.attestr.flowx.utils.CommonUtils;
import com.attestr.flowx.utils.GlobalVariables;

import java.util.ArrayList;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 17/02/23
 **/
public class MediaRecyclerViewAdapter extends RecyclerView.Adapter<MediaRecyclerViewAdapter.MediaViewHolder> {

    private Activity mActivity;
    private ArrayList<MediaDataClass> mDigitalAddressArrayList;
    private String mType;
    private FrameLayout.LayoutParams layoutParams;
    private MediaImagesListener mMediaImagesListener;
    private boolean isTablet;
    private int mSize;

    public MediaRecyclerViewAdapter(Activity activity,
                                    ArrayList<MediaDataClass> digitalAddressDataList,
                                    String type,
                                    MediaImagesListener mediaImagesListener,
                                    int size) {
        this.mActivity = activity;
        this.mDigitalAddressArrayList = digitalAddressDataList;
        this.mType = type;
        this.mSize = size;
        this.mMediaImagesListener = mediaImagesListener;
        isTablet = CommonUtils.isTablet(mActivity);
        if (isTablet){
            layoutParams = new FrameLayout.LayoutParams(80, 80);
        } else {
            layoutParams = new FrameLayout.LayoutParams(60, 60);
        }
    }


    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.digital_address_check_card_view, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaDataClass mediaDataClass = mDigitalAddressArrayList.get(position);

        if (("ADD_IMAGE").equals(mediaDataClass.getmTag())) {
            holder.removeImageView.setVisibility(View.GONE);
            layoutParams.gravity = Gravity.CENTER;
            holder.selectedImageImageView.setBackgroundResource(R.drawable.add);
            holder.selectedImageImageView.setLayoutParams(layoutParams);
        } else if ("SELECTED_IMAGE".equals(mediaDataClass.getmTag())) {
            holder.removeImageView.setColorFilter(Color.parseColor(GlobalVariables.themeColor));
            holder.removeImageView.setVisibility(View.VISIBLE);
            holder.selectedImageImageView.setImageBitmap(mediaDataClass.getSelectedImageBitmap());
            holder.selectedImageImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        holder.itemView.setOnClickListener(view -> {
            if ("ADD_IMAGE".equals(mediaDataClass.getmTag())) {
                if (mDigitalAddressArrayList.size() <= mSize) {
                    mMediaImagesListener.selectImage(mType);
                }
            }
        });

        holder.removeImageView.setOnClickListener(view -> {
            holder.removeImageView.setClickable(false);
            mMediaImagesListener.removeImageAndMediaId(position, mType);
        });
        
    }

    @Override
    public int getItemCount() {
        return mDigitalAddressArrayList.size();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {

        ImageView selectedImageImageView;
        ImageView removeImageView;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedImageImageView = itemView.findViewById(R.id.digital_address_check_card_view_image_view);
            removeImageView = itemView.findViewById(R.id.digital_address_check_card_remove_image_view);
        }

    }

}
