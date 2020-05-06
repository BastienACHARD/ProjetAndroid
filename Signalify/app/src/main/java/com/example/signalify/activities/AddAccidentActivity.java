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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.signalify.Notifications;
import com.example.signalify.R;
import com.example.signalify.models.Accident;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class AddAccidentActivity extends AppCompatActivity {


    Accident accident;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_accident);


        Button valid = (Button) findViewById(R.id.valid);
        Button cancel = (Button) findViewById(R.id.cancel);
        EditText description = (EditText) findViewById(R.id.editText);
        ImageButton back = (ImageButton) findViewById(R.id.back);

        descriptions.add(description.getText().toString().trim());
        images.add("image1");

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAccident(type, location, descriptions, images);
                addAccidentDataBase(accident);
                    sendNotificationChannel("","Un nouvent accident a été déclaré", Notifications.CHANNEL_3_ID, NotificationCompat.PRIORITY_HIGH);
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

    private void sendNotificationChannel(String title, String message, String channelId, int priority) {
        Intent activityIntent = new Intent(this, AddAccidentActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);
        Bitmap picture = BitmapFactory.decodeResource(getResources(), R.drawable.accident1);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText( message)
                .setPriority(priority)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(picture)
                        .bigLargeIcon(null))
                 .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logoaccident)
                .setPriority(priority)
                .setOnlyAlertOnce(true);

        NotificationManagerCompat.from(this).notify(++id, notification.build());
    }


}
