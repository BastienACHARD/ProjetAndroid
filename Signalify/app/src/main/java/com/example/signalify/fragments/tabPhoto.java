package com.example.signalify.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.signalify.R;
import com.example.signalify.activities.IPictureActivity;
import com.example.signalify.activities.ShowDetailActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class tabPhoto extends Fragment implements IPictureActivity {
    int index=0;

    String id;
    FirebaseFirestore firebaseFirestore ;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    ArrayList listImage = new ArrayList() ;

    ArrayList<Bitmap> bitmapList= new ArrayList<Bitmap>();

    public tabPhoto() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getArguments()!=null) id= getArguments().getString("overlay");


        firebaseStorage=FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        // Inflate the layout for this fragment
        View root=inflater.inflate(R.layout.fragment_tab_photo, container, false);
        getAccidentImage(id,root);
       /*GridView list= root.findViewById(R.id.gridView);
        CustomAdapeter custom=new CustomAdapeter();
        list.setAdapter(custom);*/
        return root;
    }

    void displayImage(final ImageView im, String name) {
        StorageReference imgRef = storageReference.child(name);
        long MAXBYTES=1024*1024;
        imgRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

              Bitmap  bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                im.setImageBitmap(bitmap);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    private void getAccidentImage(String id, final View root){
        Log.d("id liste de l'image", ""+id);
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
                    listImage = (ArrayList<String>) doc.getData().get("image");
                   assert listImage != null;

                    GridView list= root.findViewById(R.id.gridView);
                    CustomAdapeter custom=new CustomAdapeter();
                    list.setAdapter(custom);
                }

            }
        });
    }


    class CustomAdapeter extends BaseAdapter {


        @Override
        public int getCount() {
            return listImage.size();
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
            displayImage(image,"images/"+(String)listImage.get(position));


            return view;
        }
    }
}
