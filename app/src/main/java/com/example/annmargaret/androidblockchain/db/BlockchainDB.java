package com.example.annmargaret.androidblockchain.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.annmargaret.androidblockchain.models.Block;

@Database(entities = {Block.class}, version = 5, exportSchema = false)
public abstract class BlockchainDB extends RoomDatabase {
    public abstract BlockDao daoAccess();
    public static final String NAME = "blockchain_db";
}
