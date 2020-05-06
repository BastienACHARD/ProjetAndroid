package com.example.signalify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.signalify.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class tabDescription extends Fragment {

   int valeur = 0;
    public tabDescription() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments()!=null) valeur= getArguments().getInt("overlay");
        View root =inflater.inflate(R.layout.fragment_tab_description, container, false);
        ((ImageView)root.findViewById(R.id.appel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        });

        ( ( Button) root.findViewById(R.id.buttonAjout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(getContext(),"A faire bientot ++++"+valeur,Toast.LENGTH_LONG);
                toast.show();
            }
        });
        return root;
    }
}
