package com.example.annmargaret.androidblockchain.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.annmargaret.androidblockchain.R;
import com.example.annmargaret.androidblockchain.activities.BlockchainActivity;

import static com.example.annmargaret.androidblockchain.widget.UpdateBlockchainService.BLOCK_STATS;


import java.util.ArrayList;


public class GridWidgetService extends RemoteViewsService {

    ArrayList<String> blockStats;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext(), intent);
    }



    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        Context mContext;

        public  GridRemoteViewsFactory(Context context, Intent intent) {
            this.mContext = context;
            blockStats = intent.getStringArrayListExtra(BLOCK_STATS);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            blockStats = BlockchainWidgetProvider.stats;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }


        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
            views.setTextViewText(R.id.widget_grid_view_item, blockStats.get(position));
            Intent fillInIntent = new Intent(getApplicationContext(), BlockchainActivity.class);
            views.setOnClickFillInIntent(R.id.widget_grid_view_item, fillInIntent);
            return views;

        }

        @Override
        public int getCount() {
            return blockStats.size();
        }


        @Override
        public int getViewTypeCount() {
            return 1;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
