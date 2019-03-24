package com.example.annmargaret.androidblockchain.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.annmargaret.androidblockchain.utils.Utils;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Parcel
@Entity(tableName = "block")
public class Block {
    @PrimaryKey(autoGenerate = true)
    public long bId;
    @ColumnInfo(name = "index")
    int index;
    @ColumnInfo(name = "timestamp")
    long timestamp;
    @ColumnInfo(name = "hash")
    String hash;
    @ColumnInfo(name = "previous_hash")
    String previousHash;
    @ColumnInfo(name = "data")
    String data;
    @ColumnInfo(name = "nonce")
    int nonce;
    @ColumnInfo(name="file_url")
    String fileUrl;

    public Block(int index, long timestamp, String previousHash, String data, String fileUrl) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.data = data;
        nonce = 0;
        hash = Block.calculateHash(this);
        this.fileUrl = fileUrl;
    }

    @ParcelConstructor
    public Block() {}

    //Getters + Setters
    public long getbId() {
        return bId;
    }
    public int getIndex() {
        return index;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public String getHash() {
        return hash;
    }
    public String getPreviousHash() {
        return previousHash;
    }
    public String getData() {
        return data;
    }
    public int getNonce() {
        return nonce;
    }
    public String getFileUrl() {
        return fileUrl;
    }


    public void setbId(long bId) {
        this.bId = bId;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }
    public void setFileUrl(String url) {this.fileUrl = url;}


    public String str() {
        return index+timestamp+previousHash+data+nonce+fileUrl;
    }

    //Stringify Block
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Block #")
                .append(index)
                .append(" [previousHash : ")
                .append(previousHash)
                .append(", ")
                .append("timestamp : ")
                .append(new Date(timestamp))
                .append(", ")
                .append("data : ")
                .append(data)
                .append(", ")
                .append("hash : ")
                .append(hash)
                .append(", ")
                .append("fileUrl : ")
                .append(fileUrl)
                .append("]");
        return builder.toString();
    }


    //HashingFunction
    public static String calculateHash(Block block) {
        if(block != null) {
            MessageDigest digest = null;

            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }

            String txt = block.str();
            final byte bytes[] = digest.digest(txt.getBytes());
            final StringBuilder builder = new StringBuilder();

            for(final byte b: bytes) {
                String hex = Integer.toHexString(0xff & b);

                if(hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();
        }
        return null;
    }

    //Mining Function
    public void mineBlock(int difficulty) {
        nonce = 0;
        if(difficulty > 3 ) {
            while (!getHash().substring(0, difficulty).equals(Utils.zeros(difficulty))){
                nonce++;
                hash = Block.calculateHash(this);
            }
        }
    }

}
