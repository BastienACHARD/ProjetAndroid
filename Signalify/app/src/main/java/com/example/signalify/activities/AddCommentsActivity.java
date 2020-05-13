package com.example.signalify.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.signalify.R;
import com.example.signalify.models.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Locale;

import static com.example.signalify.activities.IPictureActivity.REQUEST_CAMERA;

public class AddCommentsActivity extends AppCompatActivity implements Utilities {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MultiAutoCompleteTextView description;
    Button btnDicter, btnPhoto, btnCancel, btnSubmit;
    StorageReference mStorageRef;
    String accidentId;
    public Uri imguri;

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Dites quelque chose");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        setContentView(R.layout.activity_add_comments);
        Intent intent = getIntent();
        accidentId = intent.getStringExtra("code");
        getSupportActionBar().setTitle("Ajouter un commentaire");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        description = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView);
        btnDicter = (Button) findViewById(R.id.buttonDicter);
        btnPhoto = (Button) findViewById(R.id.photoBtn);
        btnCancel = (Button) findViewById(R.id.cancel);
        btnSubmit = (Button) findViewById(R.id.submit);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(description.getText().toString().equals(""))
                {
                   noNull();
                }
                else
                    callDialog();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();

            }
        });


        btnDicter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }

    private void back() {
        Intent intent = new Intent(getApplicationContext(), ShowDetailActivity.class);
        intent.putExtra("code", accidentId);
        startActivity(intent);
    }

    private void chargeData() {
        DocumentReference accidentRef = db.collection("Accidents").document(accidentId);
        accidentRef.update("description", FieldValue.arrayUnion(description.getText().toString()));
        accidentRef.update("image", FieldValue.arrayUnion("image" + imguri.getLastPathSegment()));
        //washingtonRef.update("regions", FieldValue.arrayRemove("east_coast"));
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setAction("images/*");
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                imguri = (Uri) data.getData();
                Bitmap picture = (Bitmap) data.getExtras().get("data");
                imguri = getImageUri(getApplicationContext(), picture);

            } else if (resultCode == RESULT_CANCELED) {

            } else {

            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000, true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    private StorageReference getStorageReference() {
        //return mStorageRef.child(System.currentTimeMillis()+"."+getExtension(imguri));
        return mStorageRef.child("images/image" + imguri.getLastPathSegment());
    }

    private void FileUploader() {
        getStorageReference().putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
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

    public void noNull()
    {
        AlertDialog confirm = new AlertDialog.Builder(this).create();
        confirm.setTitle("Champ requis");
        confirm.setMessage("Veuillez renseigner tous les champs");
        confirm.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        confirm.show();


    }
    public void callDialog()
    {
        AlertDialog confirm = new AlertDialog.Builder(this).create();
        confirm.setTitle("Ajout d'un nouvel incident");
        confirm.setMessage("Etes vous sûr(e) de vouloir ajouter cet commentaire ?");
        confirm.setButton(AlertDialog.BUTTON_POSITIVE, "Oui", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(description.getText().toString().equals(""))

                FileUploader();
                chargeData();
                back();
                }
        });
        confirm.setButton(AlertDialog.BUTTON_NEGATIVE, "Non", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirm.show();

    }


}
