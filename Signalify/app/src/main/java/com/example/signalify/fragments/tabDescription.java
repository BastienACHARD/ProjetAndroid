package com.example.signalify.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.signalify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class tabDescription extends Fragment {

   String id;
    ArrayList<String> description =new ArrayList<>();


    public tabDescription() {
    }

   private void getAccidentDescription(String id, final View root){
       FirebaseFirestore.getInstance().collection("Accidents").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful())
               {

               }
               if(task.isComplete())
               {
                   DocumentSnapshot doc = task.getResult();
                   assert doc != null;
                  description = (ArrayList<String>) doc.getData().get("description");
                   assert description != null;
                   description.add("oklm yfkuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
                   description.add("babe");
                   description.add("tsrvvvvvvvvvvvvvvvvukgimuibhipuvydftyfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdddddddddddddddddddddddddddddddddddddddddddddddddd");
                   description.add("tsrvvvvvvvvvvvvvvvvukgimuibhipuvydftyfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdddddddddddddddddddddddddddddddddddddddddddddddddd");
                   description.add("tsrvvvvvvvvvvvvvvvvukgimuibhipuvydftyfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdddddddddddddddddddddddddddddddddddddddddddddddddd");
                   description.add("tsrvvvvvvvvvvvvvvvvukgimuibhipuvydftyfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdddddddddddddddddddddddddddddddddddddddddddddddddd");
                   description.add("tsrvvvvvvvvvvvvvvvvukgimuibhipuvydftyfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdddddddddddddddddddddddddddddddddddddddddddddddddd");
                   description.add("tsrvvvvvvvvvvvvvvvvukgimuibhipuvydftyfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffdddddddddddddddddddddddddddddddddddddddddddddddddd");


                   GridView list= root.findViewById(R.id.gridViewDescription);
                  CustomAdapeter custom = new CustomAdapeter();
                  list.setAdapter(custom);


               }

           }
       });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments()!=null) id= getArguments().getString("overlay");
        View root =inflater.inflate(R.layout.fragment_tab_description, container, false);
        getAccidentDescription(id,root);
       if(description.size()>0)
        ((ImageView)root.findViewById(R.id.appel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
            }
        });

        ( (Button) root.findViewById(R.id.buttonAjout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast=Toast.makeText(getContext(),id,Toast.LENGTH_LONG);
                toast.show();
            }
        });
        return root;
    }

    class CustomAdapeter extends BaseAdapter {

        @Override
        public int getCount() {
            return description.size();
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
            View view=getLayoutInflater().inflate(R.layout.card_item,null);
            TextView text=(TextView)(view.findViewById(R.id.card_text));
                text.setText(description.get(position));
            return view;
        }
    }
}
