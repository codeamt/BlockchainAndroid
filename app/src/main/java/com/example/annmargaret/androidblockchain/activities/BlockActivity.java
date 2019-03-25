package com.example.annmargaret.androidblockchain.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.annmargaret.androidblockchain.R;
import com.example.annmargaret.androidblockchain.models.Block;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.io.IOException;
import java.text.DateFormat;

public class BlockActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView index, timestamp, hash, prevHash, blockData;
    ImageView blockFile;
    Block blockInDetail;
    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.block_toolbar_title);
        }

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        index = (TextView) findViewById(R.id.block_details_index);
        timestamp = (TextView) findViewById(R.id.block_details_timestamp);
        hash = (TextView) findViewById(R.id.block_details_hash);
        prevHash = (TextView) findViewById(R.id.block_details_prev_hash);
        blockData = (TextView) findViewById(R.id.block_details_data);
        blockFile = (ImageView) findViewById(R.id.blockFile);
        blockInDetail = (Block) Parcels.unwrap(getIntent().getParcelableExtra("block"));
        index.setText(String.valueOf(blockInDetail.getIndex()));
        timestamp.setText(DateFormat.getInstance().format(blockInDetail.getTimestamp()));
        hash.setText(blockInDetail.getHash());
        prevHash.setText(blockInDetail.getPreviousHash());
        blockData.setText(blockInDetail.getData());
        if(blockInDetail.getFileUrl() != null && blockInDetail.getIndex() > 2) {
            Log.d("IMG REF", blockInDetail.getFileUrl());
            StorageReference imgRef = storageReference.child(blockInDetail.getFileUrl());
            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("URI", uri.toString());
                    Glide.with(getApplicationContext())
                            .load(uri.toString())
                            .placeholder(R.drawable.ic_action_name)
                            .into(blockFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.uri_not_found, Toast.LENGTH_SHORT).show();
                    Glide.with(getApplicationContext())
                            .load(R.drawable.ic_action_name)
                            .into(blockFile);
                }
            });
        } else {
            if(blockInDetail.getIndex() < 2) {
                Glide.with(this)
                        .load(blockInDetail.getFileUrl())
                        .placeholder(R.drawable.ic_action_name)
                        .into(blockFile);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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

}
