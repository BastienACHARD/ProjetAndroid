package com.example.signalify.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.signalify.models.Notifications;
import com.example.signalify.R;
import com.example.signalify.fragments.PictureFragment;
import com.example.signalify.models.Accident;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class AddAccidentActivity extends AppCompatActivity implements LocationListener,IPictureActivity {

    FirebaseStorage firebaseStorage;
    Accident accident;
    GeoPoint myLocation;
    Random rand = new Random();
    StorageReference storageReference;
    Spinner spinner;
    ImageView imageView;
    private LocationManager locationManager;
    EditText description;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String type;
    GeoPoint location = new GeoPoint(43.65620, 7.00517);
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();
    int id = 0;

    StorageReference mStorageRef;

    public Uri imguri;


    public AddAccidentActivity() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_accident);

        imageView = (ImageView) (findViewById(R.id.photo));
        Button photoBtn = (Button) (findViewById(R.id.photobtn));
        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    //ActivityCompat.requestPermissions(, new String[]{Manifest.permission.CAMERA}, IPictureActivity.REQUEST_CAMERA);
                } else {
                    takePicture();
                }
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);

        getSupportActionBar().setTitle("Ajouter un incident");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        spinner = (Spinner) (findViewById(R.id.spiner));
        Button valid = (Button) findViewById(R.id.valid);
        Button cancel = (Button) findViewById(R.id.cancel);
        description = (EditText) findViewById(R.id.multiAutoCompleteTextView);

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUploader();
                chargeData();
                setAccident(type, myLocation, descriptions, images);
                // setAccident(type, location, descriptions, images);
                addAccidentDataBase(accident);
                //   showImage(images.get(rand.nextInt(images.size())));
                //showImage("images/cr7.jpg");
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

    private void chargeData() {
        type = spinner.getSelectedItem().toString();
        descriptions.add(description.getText().toString().trim());
        images.add("image"+imguri.getLastPathSegment());
    }

    private String getExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    private StorageReference getStorageReference(){
        //return mStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));
        return mStorageRef.child("images/image"+imguri.getLastPathSegment());
    }

    private void FileUploader(){
        getStorageReference().putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        images.add(getStorageReference().toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public void setAccident(String type, GeoPoint location, ArrayList<String> description, ArrayList<String> image) {
        accident = new Accident(type, location, description, image);
    }

    public void addAccidentDataBase(Accident accident) {
        db.collection("Accidents").add(accident);
    }

    void showImage(String name) {
        StorageReference imgRef = storageReference.child(name);
        long MAXBYTES = 1024 * 1024;
        imgRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (true)
                    sendNotificationChannel("Un nouvel incident a été déclaré.", "Cliquez pour plus d'informations sur l'accident.", Notifications.CHANNEL_ID, NotificationCompat.PRIORITY_HIGH, bitmap);
                else
                    sendNotificationChannelNormal("Un nouvel incident a été déclaré.", "Cliquez pour plus d'informations sur l'accident.", Notifications.CHANNEL_ID, NotificationCompat.PRIORITY_HIGH);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void sendNotificationChannelNormal(String title, String message, String channelId, int priority) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority);
        NotificationManagerCompat.from(this).notify(1, notification.build());
    }

    public void sendNotificationChannel(String title, String message, String channelId, int priority, Bitmap bitmap) {
        Intent activityIntent = new Intent(this, AddAccidentActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(title)
                .setContentText(message)
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
        myLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast toast = Toast.makeText(getApplicationContext(), "CAMERA authorization granted", Toast.LENGTH_LONG);
                    toast.show();
                    takePicture();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "CAMERA authorization NOT granted", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                //imguri = (Uri) data.getData();
                Bitmap picture = (Bitmap) data.getExtras().get("data");
                imguri = getImageUri(getApplicationContext(), picture);
                setImage(picture);
                Toast toast = Toast.makeText(getApplicationContext(), "Picture charged : "+imguri, Toast.LENGTH_LONG);
                toast.show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast toast = Toast.makeText(getApplicationContext(), "Picture canceled", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "action failed", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setAction("images/*");
        startActivityForResult(intent, IPictureActivity.REQUEST_CAMERA);
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}