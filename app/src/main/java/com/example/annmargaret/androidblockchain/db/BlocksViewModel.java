package com.example.annmargaret.androidblockchain.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.example.annmargaret.androidblockchain.BlockchainServer;
import com.example.annmargaret.androidblockchain.models.Block;

import java.util.List;

public class BlocksViewModel extends ViewModel {
    public List<Block> blockList;
    public BlockchainServer blockchainServer = BlockchainServer.getInstance();


    public List<Block> getBlocks() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (blockchainServer.getBlockDao().getAllBlocks() != null) {
                    blockList = blockchainServer.getBlockDao().getAllBlocks();
                } else {
                    blockList = blockchainServer.blockchain.blocks;
                }
            }
        });
        return blockList;
    }
}
