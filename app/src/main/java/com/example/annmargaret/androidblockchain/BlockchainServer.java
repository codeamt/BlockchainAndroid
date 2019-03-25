package com.example.annmargaret.androidblockchain;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.util.Log;

import com.example.annmargaret.androidblockchain.db.BlockDao;
import com.example.annmargaret.androidblockchain.db.BlockchainDB;
import com.example.annmargaret.androidblockchain.models.Block;
import com.example.annmargaret.androidblockchain.models.Blockchain;

import java.util.ArrayList;
import java.util.List;


public class BlockchainServer extends Application {
    public Blockchain blockchain;
    public static BlockchainServer server;
    BlockchainDB blockDB;
    BlockDao blockDao;

    public static BlockchainServer getInstance() {
        return server;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.blockchain = new Blockchain(4);

        this.blockDB = Room.databaseBuilder(BlockchainServer.this, BlockchainDB.class, BlockchainDB.NAME)
                .fallbackToDestructiveMigration()
                .build();

        this.blockDao = this.blockDB.daoAccess();
        server = this;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                BlockDao blockDao = server.getBlockDB().daoAccess();

                if(blockDao.getAllBlocks().size() == 0 ) {
                    Block genesis = new Block(0, System.currentTimeMillis(), null, getResources().getString(R.string.genesis_data), null);
                    genesis.mineBlock(blockchain.difficulty);
                    blockchain.blocks.add(genesis);


                    Block seed1 = new Block(1, System.currentTimeMillis(), genesis.getHash(), getResources().getString(R.string.seed1_data), "https://firebasestorage.googleapis.com/v0/b/android-blockchain.appspot.com/o/image_00005.jpg?alt=media&token=3d87f1cc-d0f6-47fd-9e98-ab8978bec713");
                    seed1.mineBlock(blockchain.difficulty);
                    if(blockchain.isValidNewBlock(seed1, blockchain.latestBlock())
                            && blockchain.isBlockChainValid()) {
                        blockchain.blocks.add(seed1);
                    }


                    Block seed2 = new Block(2, System.currentTimeMillis(), seed1.getHash(), getResources().getString(R.string.seed2_data), "https://firebasestorage.googleapis.com/v0/b/android-blockchain.appspot.com/o/image_08127.jpg?alt=media&token=efe1112f-66f4-405a-931e-aa99d257f02e");
                    seed2.mineBlock(blockchain.difficulty);
                    if(blockchain.isValidNewBlock(seed2, blockchain.latestBlock())
                            && blockchain.isBlockChainValid()) {
                        blockchain.blocks.add(seed2);
                    }


                    for (Block b : server.getBlockchain().blocks) {
                        blockDao.insertBlock(b);
                        Log.d("SEEDS TO DB", getBlockDao().getBlock(b.getIndex()).toString());
                    }
                }
            }
        });
    }

    public BlockchainDB getBlockDB() {
        return this.blockDB;
    }

    public BlockDao getBlockDao() {
        BlockDao blockDao = null;
        if(this.getBlockDB() != null) {
            blockDao = this.getBlockDB().daoAccess();
        }
        return blockDao;
    }

    public Blockchain getBlockchain() {
        return this.blockchain;
    }

    public void addBlock(Block block) {
        if(block != null && this.getBlockchain() != null) {
            if(this.getBlockchain()
                    .isValidNewBlock(block, this.getBlockchain().latestBlock())
                    && this.getBlockchain()
                    .isBlockChainValid()) {

                this.getBlockchain().addBlock(block);
            }

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    server.getBlockDao().insertBlock(block);
                }
            });
        }
    }

    public Block getBlock(int index) {
        return this.getBlockchain().blocks.get(index);
    }

    public List<Block> getBlocks(){
        List<Block> tempList = new ArrayList<Block>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if(server.getBlockDao() != null ) {
                    tempList.addAll(server.getBlockDao().getAllBlocks());
                }
            }
        });

        return tempList;
    }

}
