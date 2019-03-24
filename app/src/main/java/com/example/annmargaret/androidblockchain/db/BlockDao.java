package com.example.annmargaret.androidblockchain.db;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.annmargaret.androidblockchain.models.Block;

import java.util.List;

@android.arch.persistence.room.Dao
public interface BlockDao {
    @Insert
    void insertBlock(Block block);
    @Query("SELECT * FROM `block` WHERE `index`= :index")
    Block getBlock(int index);
    @Query("SELECT * FROM `block` ORDER BY `index` ASC")
    List<Block> getAllBlocks();
    @Update
    void updateGroup(Block block);
    @Delete
    void deleteGroup(Block block);
}
