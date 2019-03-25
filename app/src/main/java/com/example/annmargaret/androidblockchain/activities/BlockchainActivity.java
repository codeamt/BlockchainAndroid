package com.example.annmargaret.androidblockchain.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.annmargaret.androidblockchain.db.BlocksViewModel;
import com.example.annmargaret.androidblockchain.utils.BlockAdapter;
import com.example.annmargaret.androidblockchain.BlockchainServer;
import com.example.annmargaret.androidblockchain.R;
import com.example.annmargaret.androidblockchain.models.Block;
import com.example.annmargaret.androidblockchain.models.Blockchain;
import com.example.annmargaret.androidblockchain.widget.UpdateBlockchainService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockchainActivity extends AppCompatActivity {

    public List<Block> chain;

    public RecyclerView recycler;
    public BlockAdapter blockAdapter;
    public LinearLayoutManager layoutManager;

    public Blockchain blockchain;
    public static BlockchainServer blockchainServer = BlockchainServer.getInstance();
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    ArrayList<String> blockStats  = new ArrayList<String>();
    BlocksViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blockchain);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        blockchain = blockchainServer.getBlockchain();
        recycler = findViewById(R.id.rvBlocks);
        model = ViewModelProviders.of(this).get(BlocksViewModel.class);
        layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                chain = reverseList(blockchainServer.getBlocks());
            }
        });
        blockAdapter = new BlockAdapter(chain, this);
        recycler.setAdapter(blockAdapter);
        //for home widget
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (chain != null) {
                    String size = R.string.stat_size + Integer.toString(chain.size());
                    String diff = R.string.stat_diff + Integer.toString(blockchain.difficulty);
                    blockStats.add(size);
                    blockStats.add(diff);
                    if (blockchain.blocks.size() != 0 && blockchain.blocks.size() != 1) {
                        Block mostRecent = blockchain.blocks.get(blockchain.blocks.size() - 1);
                        Log.d("LATEST BLOCK: ", mostRecent.toString());
                        blockStats.add(R.string.stat_latest + mostRecent.toString());
                    } else {
                        blockStats.add(getResources().getString(R.string.stat_none));
                    }
                    UpdateBlockchainService.startBlockService(getApplicationContext(), blockStats);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(chain != null) {
                    chain = reverseList(blockchainServer.getBlockDao().getAllBlocks());
                    blockAdapter.setBlocks(chain);
                    recycler.setAdapter(blockAdapter);
                    List<Block> tempList = new ArrayList<Block>(chain);
                    for(Block b : tempList) {
                        Log.d("DB Check", b.toString());
                    }
                }
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            case R.id.item2:
                Intent intent2 = new Intent(this, NewBlockActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public static<Block> List<Block> reverseList(List<Block> list) {
        List<Block> reverse = new ArrayList<>(list.size());

        list.stream()
                .collect(Collectors.toCollection(LinkedList::new))
                .descendingIterator()
                .forEachRemaining(reverse::add);

        return reverse;
    }
}
