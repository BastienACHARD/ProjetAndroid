package com.example.signalify.activities;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.signalify.models.Notifications;
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

public class AddAccidentActivity extends AppCompatActivity implements LocationListener {


    Accident accident;
    GeoPoint myLocation;
    Random rand = new Random();
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Spinner spinner;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String type = "Accident";
    GeoPoint location = new GeoPoint(43.65620, 7.00517);
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();


    public AddAccidentActivity() { }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_accident);

        getSupportActionBar().setTitle("Ajouter un incident");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        spinner=(Spinner) (findViewById(R.id.spiner)) ;
        Button valid = (Button) findViewById(R.id.valid);
        Button cancel = (Button) findViewById(R.id.cancel);
        final EditText description = (EditText) findViewById(R.id.editText);


        descriptions.add(description.getText().toString().trim());
        images.add("image1");

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Notifications notifications = new Notifications();
                setAccident(type, location, descriptions, images);
                addAccidentDataBase(accident);
                Log.d("show me0", ""+ MainActivity.imageNotifChoice);
                Log.d("show me0", ""+ MainActivity.radarState);
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


    }

    public void setAccident(String type, GeoPoint location, ArrayList<String> description, ArrayList<String> image) {
        accident = new Accident(type, location, description, image);
    }

    public void addAccidentDataBase(Accident accident) {
        db.collection("Accidents").add(accident);
    }

    void showImage( String name) {
        StorageReference imgRef = storageReference.child(name);
        long MAXBYTES=1024*1024;
        imgRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap  bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                if(true)
              sendNotificationChannel("Un nouvel incident a été déclaré.","Cliquez pour plus d'informations sur l'accident.", Notifications.CHANNEL_ID, NotificationCompat.PRIORITY_HIGH,bitmap);
                else
                   sendNotificationChannelNormal("Un nouvel incident a été déclaré.","Cliquez pour plus d'informations sur l'accident.",Notifications.CHANNEL_ID,NotificationCompat.PRIORITY_HIGH);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    public void sendNotificationChannelNormal(String title, String message, String channelId, int priority) {
        NotificationCompat.Builder notification=new NotificationCompat.Builder(getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority);
        NotificationManagerCompat.from(this).notify(1,notification.build());
    }
    public void sendNotificationChannel(String title, String message, String channelId, int priority, Bitmap bitmap) {
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
                .setSmallIcon(R.drawable.alarm)
                .setPriority(priority)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat.from(this).notify(0, notification.build());
    }


    @Override
    public void onLocationChanged(Location location) {
        myLocation = new GeoPoint(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
