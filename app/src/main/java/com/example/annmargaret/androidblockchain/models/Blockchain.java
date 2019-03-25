package com.example.annmargaret.androidblockchain.models;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.annmargaret.androidblockchain.BlockchainServer;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Blockchain {
    public int difficulty;
    public List<Block> blocks;


    @ParcelConstructor
    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        this.blocks = new ArrayList<>();
    }

    public Blockchain() {}

    public int getDifficulty() {
        return difficulty;
    }

    public Block latestBlock(){
        if(this.blocks.size() > 0) {
            return this.blocks.get(this.blocks.size() - 1);
        }
        return null;
    }

    public Block newBlock(String data, String url) {
        Block latestBlock = this.latestBlock();
        return new Block(latestBlock.getIndex() + 1, System.currentTimeMillis(), latestBlock.getHash(), data, url);
    }

    public void addBlock(Block b) {
        if (b != null) {
            b.mineBlock(difficulty);
            blocks.add(b);
            if(BlockchainServer.server != null) {
                BlockchainServer.server.addBlock(b);
            }
        }
    }

    public boolean isFirstBlockValid(){
        Block firstBlock = blocks.get(0);

        if (firstBlock.getIndex() != 0) {
            return false;
        }

        if (firstBlock.getPreviousHash() != null){
            return false;
        }

        if(firstBlock.getHash() == null ||
                !Block.calculateHash(firstBlock).equals(firstBlock.getHash())) {
            return false;
        }
        return true;
     }


     public boolean isValidNewBlock(Block newBlock, Block previousBlock) {
        if(newBlock != null && previousBlock != null) {
            if (previousBlock.getIndex() + 1 != newBlock.getIndex()) {
                return false;
            }

            if(newBlock.getPreviousHash() == null ||
                    !newBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }

            if(newBlock.getHash() == null ||
                    !Block.calculateHash(newBlock).equals(newBlock.getHash())) {
                return false;
            }

            return true;
        }
        return false;
     }



     public boolean isBlockChainValid() {
        if (!isFirstBlockValid()) {
            return false;
        }

        for (int i = 1; i<blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);

            if(!isValidNewBlock(currentBlock,previousBlock)) {
                return false;
            }
        }

        return true;
     }


     public String toString() {
        StringBuilder builder = new StringBuilder();

        for(Block block:blocks) {
            builder.append(block).append("\n");
        }

        return builder.toString();
     }

}
