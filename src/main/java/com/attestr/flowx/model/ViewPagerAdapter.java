package com.attestr.flowx.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.attestr.flowx.utils.HandleException;

import java.util.List;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 *
 * @Author Gaurav Naresh Pandit
 * @Since 28/08/21
 **/

public class ViewPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> mFragmentList;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
        super(fragmentActivity);
        this.mFragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        try {
            mFragmentList.add(fragment);
        } catch (Exception e){
            HandleException.handleInternalException(e.toString());
        }
    }



}
