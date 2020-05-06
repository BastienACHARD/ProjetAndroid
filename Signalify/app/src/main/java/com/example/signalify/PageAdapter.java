package com.example.signalify;

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
    private int index=0;

    public PageAdapter(@NonNull FragmentManager fm, int num, int index) {

        super(fm);
        this.index=index;
        this.numofTab=num;
        Log.d("ok",""+index);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        Bundle args=new Bundle();
        args.putInt("overlay",index);
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
