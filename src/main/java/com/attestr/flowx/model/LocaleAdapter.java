package com.attestr.flowx.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.attestr.flowx.R;
import com.attestr.flowx.model.response.LocaleLanguagesData;

import java.util.List;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 13/04/22
 **/
public class LocaleAdapter extends RecyclerView.Adapter<LocaleAdapter.LocaleViewHolder> {

    private static final String TAG = "LocaleAdapter";
    private Context mContext;
    private LocaleLanguagesData[] mLocaleLanguagesData;
    private ItemClickListener onItemClickListener;
    private int lastSelectedPosition = 0;
    private View lastSelectedView;
    private String selectedLocale = "en";

    public LocaleAdapter(Context mContext,
                         LocaleLanguagesData[] localeLanguagesData) {
        this.mContext = mContext;
        this.mLocaleLanguagesData = localeLanguagesData;
    }

    @NonNull
    @Override
    public LocaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.locale_card_view_layout, parent, false);
        return new LocaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocaleViewHolder holder, int position) {

        LocaleLanguagesData localeLanguagesData = mLocaleLanguagesData[position];
        holder.localeNameTextView.setText(localeLanguagesData.getValue());

        if (holder.getLayoutPosition() == 0) {
            View selectedView = holder.cardRelativeLayout;
            selectedView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.locale_grey_background));
            lastSelectedView = selectedView;
        }

        holder.cardRelativeLayout.setOnClickListener(view -> {
            if (holder.getLayoutPosition() != lastSelectedPosition) {
                view.setBackground(ContextCompat.getDrawable(mContext, R.drawable.locale_grey_background));
                lastSelectedView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.locale_white_background));
                lastSelectedView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.card_view_background_gray_boarder));
            }
            lastSelectedView = view;
            lastSelectedPosition = holder.getLayoutPosition();

            selectedLocale = mLocaleLanguagesData[position].getKey();

            onItemClickListener.OnItemClickListener(selectedLocale);

        });

    }

    @Override
    public int getItemCount() {
        return mLocaleLanguagesData.length;
    }

    public static class LocaleViewHolder extends RecyclerView.ViewHolder {

        TextView localeNameTextView;
        RelativeLayout cardRelativeLayout;

        public LocaleViewHolder(@NonNull View itemView) {
            super(itemView);
            localeNameTextView = itemView.findViewById(R.id.locale_name_text_view);
            cardRelativeLayout = itemView.findViewById(R.id.locale_card_relative_layout);
        }

    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void OnItemClickListener(String selectedLanguage);
    }

}
