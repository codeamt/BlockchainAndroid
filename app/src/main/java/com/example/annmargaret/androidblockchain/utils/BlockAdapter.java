package com.example.annmargaret.androidblockchain.utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.annmargaret.androidblockchain.R;
import com.example.annmargaret.androidblockchain.activities.BlockActivity;
import com.example.annmargaret.androidblockchain.models.Block;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.BlockViewHolder>  {

    List<Block> blocks;
    Context context;

    public BlockAdapter(List<Block> chain, Context context) {
        this.blocks = chain;
        this.context = context;
    }

    @NonNull
    @Override
    public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_card, parent, false);
        return new BlockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockAdapter.BlockViewHolder holder, final int position) {
        Block block = blocks.get(position);
        if(block != null) {
            Long timestamp = block.getTimestamp();
            assert timestamp.toString() != null;
            String formattedTimeStamp = DateFormat.getInstance().format(timestamp);
            holder.index.setText(String.valueOf(block.getIndex()));
            holder.timestamp.setText(formattedTimeStamp);
            holder.hash.setText(block.getHash());
            holder.data.setText(block.getData());
            if (block.getFileUrl() == null) {
                Glide.with(context)
                        .load(R.drawable.ic_action_name)
                        .into(holder.icon);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_with_attach)
                        .into(holder.icon);
            }
            final Intent intent = new Intent(context, BlockActivity.class);
            intent.putExtra("block", Parcels.wrap(block));
            holder.blockWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(intent);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if(blocks != null) {
            return blocks.size();
        }
        return 0;
    }

    class BlockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView index;
        TextView timestamp;
        TextView hash;
        TextView data;
        ImageView icon;
        LinearLayout blockWrapper;
        private BlockViewHolder(View view) {
            super(view);
            index = (TextView) view.findViewById(R.id.blockIndex);
            timestamp = (TextView) view.findViewById(R.id.blockTimestamp);
            hash = (TextView) view.findViewById(R.id.blockHash);
            data = (TextView) view.findViewById(R.id.blockData);
            icon = (ImageView) view.findViewById(R.id.attachmentIcon);
            blockWrapper = (LinearLayout) view.findViewById(R.id.blockCardWrapper);
        }

        @Override
        public void onClick(View v) {
            //Context context = v.getContext();
            Intent intent = new Intent(v.getContext(), BlockActivity.class);
            context.startActivity(intent);
        }
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
        notifyDataSetChanged();
    }

    public void add(Block b) { blocks.add(b); notifyItemInserted(getItemCount() - 1); }
    public void clearItems() { blocks.clear(); notifyDataSetChanged(); }
}
