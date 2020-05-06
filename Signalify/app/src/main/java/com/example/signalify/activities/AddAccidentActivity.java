package com.example.signalify.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.signalify.Notifications;
import com.example.signalify.R;
import com.example.signalify.models.Accident;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;

public class AddAccidentActivity extends AppCompatActivity {


    Accident accident;
    Random rand = new Random();
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String type = "Accident";
    GeoPoint location = new GeoPoint(43.61020, 7.00517);
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    int id = 0;


    public AddAccidentActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_accident);


        Button valid = (Button) findViewById(R.id.valid);
        Button cancel = (Button) findViewById(R.id.cancel);
        final EditText description = (EditText) findViewById(R.id.editText);
        ImageButton back = (ImageButton) findViewById(R.id.back);

        descriptions.add(description.getText().toString().trim());
        images.add("image1");

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAccident(type, location, descriptions, images);
                addAccidentDataBase(accident);
             //   showImage(images.get(rand.nextInt(images.size())));
                showImage("images/cr7.jpg");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void setAccident(String type, GeoPoint location, ArrayList<String> description, ArrayList<String> image) {
        accident = new Accident(type, location, description, image);
    }

    public void addAccidentDataBase(Accident accident) {
        db.collection("Accidents").add(accident);
    }

    private void sendNotificationChannel(String title, String message, String channelId, int priority, Bitmap bitmap) {
        Intent activityIntent = new Intent(this, AddAccidentActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);



        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText( message)
                .setPriority(priority)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null))
                 .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logoaccident)
                .setPriority(priority)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat.from(this).notify(++id, notification.build());
    }
    void showImage( String name) {
        StorageReference imgRef = storageReference.child(name);
        long MAXBYTES=1024*1024;
        imgRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap  bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                sendNotificationChannel("","Un nouvent accident a été déclaré", Notifications.CHANNEL_3_ID, NotificationCompat.PRIORITY_HIGH,bitmap);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


}
