package com.example.annmargaret.androidblockchain.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.annmargaret.androidblockchain.BlockchainServer;
import com.example.annmargaret.androidblockchain.R;
import com.example.annmargaret.androidblockchain.models.Block;
import com.example.annmargaret.androidblockchain.models.Blockchain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class NewBlockActivity extends AppCompatActivity {

    Button mineButton;
    EditText newData;
    BlockchainServer blockchainServer = (BlockchainServer) BlockchainActivity.blockchainServer;
    public Blockchain blockchain;
    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Button btnChoose;
    private ImageView imgPreview;
    private Uri filepath;
    String persistedURL;
    StorageReference ref;
    StorageReference imgRef;
    private final int PICK_IMAGE_REQUEST = 71;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_block);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.new_toolbar_title);
        }
        imgPreview = (ImageView) findViewById(R.id.img_preview);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnChoose = (Button) findViewById(R.id.chooseFile);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        blockchain = blockchainServer.getBlockchain();


        //grab views
        newData = findViewById(R.id.newBlockData);
        mineButton = findViewById(R.id.mineBlock);
        persistedURL = null;

        //
        mineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //GET IMAGE URI
                if (TextUtils.isEmpty(newData.getText())) {
                    Intent intent = new Intent(getApplicationContext(), NewBlockActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), R.string.nb_no_data, Toast.LENGTH_SHORT).show();
                }

                if (filepath != null) {
                    if(filepath.toString() == null) {
                        Log.d("FP inside mine button listener: ", filepath.toString());
                        persistedURL = null;
                    } else {
                        Log.d("non-null FP inside mine button listener: ", filepath.toString());
                        uploadImage();
                    }
                }

                Toast.makeText(getApplicationContext(), R.string.nb_mine, Toast.LENGTH_SHORT).show();

                if (blockchainServer!= null &&  imgRef != null) {
                    persistedURL = imgRef.getPath();
                    if(!persistedURL.isEmpty()) {

                        Log.d("Final URL: ", persistedURL);
                        Block block = blockchain.newBlock(newData.getText().toString().trim(), persistedURL) ;
                        Log.d("block-url:", block.getFileUrl());

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                if(blockchainServer != null) {
                                    blockchainServer.addBlock(block);
                                }
                            }
                        });
                        Intent intent = new Intent(getApplicationContext(), BlockchainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.nb_block_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.nb_block_create_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.intent_title)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        filepath = null;
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            if(data.getData() != null && filepath != null && imgPreview != null) {
                filepath = data.getData();
                Log.d("FILE PATH: ",  filepath.toString());
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                    if(bitmap != null) {
                        imgPreview.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void uploadImage() {
        if(filepath.toString() != null && mAuth.getCurrentUser() != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.nb_upload_dialog);
            progressDialog.show();

            ref = storageReference.child("images/" + mAuth.getCurrentUser().getUid());
            imgRef = ref.child(filepath.getLastPathSegment());
            UploadTask uploadTask = imgRef.putFile(filepath);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle unsuccessful uploads
                    progressDialog.dismiss();
                    Toast.makeText(NewBlockActivity.this, R.string.nb_upload_fail + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    if(taskSnapshot.getMetadata() != null) {
                        //persistedURL = imgRef.getPath();
                        Log.d("URL", imgRef.getPath());
                        progressDialog.dismiss();
                        Toast.makeText(NewBlockActivity.this, R.string.nb_upload_success, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
