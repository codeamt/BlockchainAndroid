package com.example.annmargaret.androidblockchain.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.annmargaret.androidblockchain.activities.BlockchainActivity;
import com.example.annmargaret.androidblockchain.R;

import static com.example.annmargaret.androidblockchain.widget.UpdateBlockchainService.BLOCK_STATS;


import java.util.ArrayList;

public class BlockchainWidgetProvider extends AppWidgetProvider {
    static public ArrayList<String> stats = new ArrayList<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        BlockchainWidgetProvider.updateBlockchainWidget(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {}

    @Override
    public void onDisabled(Context context) {}

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BlockchainWidgetProvider.class));
        final String action = intent.getAction();

        if(action != null && intent.getExtras() != null) {
            ArrayList<String> extras = intent.getExtras().getStringArrayList(BLOCK_STATS);
            if(extras != null) {
                stats = intent.getExtras().getStringArrayList(BLOCK_STATS);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view);
                BlockchainWidgetProvider.updateAppWidget(context, appWidgetManager, 0);
            }

        }


        super.onReceive(context, intent);
    }


    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_layout);
        Intent appIntent = new Intent(context, BlockchainActivity.class);
        appIntent.addCategory(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent appPendingIntent = PendingIntent.getBroadcast(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_grid_view_item, appPendingIntent);
        views.setPendingIntentTemplate(R.id.widget_grid_view, appPendingIntent);
        Intent intent = new Intent(context, GridWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateBlockchainWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            for(int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
    }
}
