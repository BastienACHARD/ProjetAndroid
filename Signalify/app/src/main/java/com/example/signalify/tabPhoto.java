package com.example.signalify;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class tabPhoto extends Fragment {
    View root;
    int index=0;

    int[] images={R.drawable.accident1,R.drawable.accident3,R.drawable.accident6};
    public tabPhoto() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View root=inflater.inflate(R.layout.fragment_tab_photo, container, false);
       GridView list= root.findViewById(R.id.gridView);
       CustomAdapeter custom=new CustomAdapeter();
       list.setAdapter(custom);
       return root;
    }

    class CustomAdapeter extends BaseAdapter {


        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=getLayoutInflater().inflate(R.layout.image_solo,null);
            ImageView image=(ImageView)(view.findViewById(R.id.imageView));
            image.setImageResource(images[position]);
            return view;
        }
    }
}
