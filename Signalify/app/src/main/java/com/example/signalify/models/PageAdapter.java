package com.example.signalify.models;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.signalify.fragments.tabDescription;
import com.example.signalify.fragments.tabPhoto;

public class PageAdapter extends FragmentPagerAdapter {
    private int numofTab;
    private String accidentId ;

    public PageAdapter(@NonNull FragmentManager fm, int num, String id) {

        super(fm);
        this.accidentId =id;
        this.numofTab=num;
        Log.d("ok",""+id);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        Bundle args=new Bundle();
        args.putString("overlay", accidentId);
        switch(position)
        {
            case 0: {
               tabDescription tabDes = new tabDescription();
               tabDes.setArguments(args);
               return  tabDes;
            }
            case 1: {
               tabPhoto tabTof = new tabPhoto();
               tabTof.setArguments(args);
               return tabTof;
            }
            default: {
                return null;
            }
        }

    }

    @Override
    public int getCount() {
        return this.numofTab;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
