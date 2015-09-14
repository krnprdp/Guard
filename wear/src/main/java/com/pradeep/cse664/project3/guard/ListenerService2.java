package com.pradeep.cse664.project3.guard;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Pradeep on 5/7/15.
 */
public class ListenerService2 extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals("/start")) {
            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v("myTag", "Message received on watch is: " + message);

            Intent messageIntent = new Intent(this, MainActivity.class);
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            startActivity(messageIntent);
            // LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        } else if (messageEvent.getPath().equals("/stop")) {
            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v("myTag", "Message received on watch is: " + message);
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);


        } else if (messageEvent.getPath().equals("/message")) {
            final String message = new String(messageEvent.getData());
            Log.v("myTag", "Message path received on watch is: " + messageEvent.getPath());
            Log.v("myTag", "Message received on watch is: " + message);
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);

        } else {
            super.onMessageReceived(messageEvent);
        }


    }
}
