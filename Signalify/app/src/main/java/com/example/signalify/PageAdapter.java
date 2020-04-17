package com.example.signalify;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private int numofTab;

    public PageAdapter(@NonNull FragmentManager fm, int num) {
        super(fm);
        this.numofTab=num;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch(position)
        {
            case 0: return new tabDescription();
            case 1: return new tabPhoto();
            default: return null;
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
