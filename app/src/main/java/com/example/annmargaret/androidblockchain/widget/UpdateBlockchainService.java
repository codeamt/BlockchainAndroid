package com.example.annmargaret.androidblockchain.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class UpdateBlockchainService extends IntentService{

    public static String BLOCK_STATS = "BLOCK_STATS";

    public UpdateBlockchainService() {
        super("UpdateBlockchainService");
    }

    public static void startBlockService(Context context, ArrayList<String> blockStats) {
        Intent intent = new Intent(context.getApplicationContext(), UpdateBlockchainService.class);
        intent.putExtra(BLOCK_STATS, blockStats);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.getExtras() != null) {
            ArrayList<String> blockStats = intent.getExtras().getStringArrayList(BLOCK_STATS);
            handleActionUpdateBlockchainWigdets(blockStats);
        }
    }

    private void handleActionUpdateBlockchainWigdets(ArrayList<String> blockStats) {
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        intent.putExtra(BLOCK_STATS, blockStats);
        if(blockStats != null) {
            sendBroadcast(intent);
        }
    }
}
