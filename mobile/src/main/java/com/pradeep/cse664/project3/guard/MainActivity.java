package com.pradeep.cse664.project3.guard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.w3c.dom.Text;


public class MainActivity extends Activity implements
        SensorEventListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    GoogleApiClient msgClient;
    Boolean flag = false;
    public Detect detect;
    int count1 = 0, count2 = 0;
    Button btnUnlock, btnMode, btnSettings;
    TextView tvStatus;

    @Override
    protected void onStart() {
        super.onStart();
        msgClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        msgClient.disconnect();
        Wearable.DataApi.removeListener(msgClient, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen1);

        btnUnlock = (Button) findViewById(R.id.btnUnlock);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnMode = (Button) findViewById(R.id.btnMode);
        tvStatus = (TextView) findViewById(R.id.tvStatus);

        btnUnlock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    unlock("released");
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    unlock("pressed");

                    return true;
                }

                return false;
            }
        });


        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // unlock("click");
            }
        });

        btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(ii);
                finish();
            }
        });

        detect = new Detect();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msgClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public void unlock(String s) {


        if(s.equals("pressed")){
            //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            tvStatus.setText("Keep Touching the screen and do gesture");
            Thread t = new Thread(new sendMsgtoWear("/start", "start"));
            t.start();


        }else{

            if(flag) {
                Thread t = new Thread(new sendMsgtoWear("/start", "start"));
                t.start();

                Intent i = new Intent(this,UnlockActivity.class);
                startActivity(i);
                finish();

            }


        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(msgClient, this);
        Thread t = new Thread(new sendMsgtoWear("/start", "start"));
        t.start();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                Log.d("*******", "getting here");
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/accelerometer") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    float avalues[] = dataMap.getFloatArray("accelerometer");
                    String s;
                    s = "X: " + Math.abs(avalues[0]) + "\nY: " + Math.abs(avalues[1]) + "\nZ: " + Math.abs(avalues[2]) + "\n";
                    String s2 = detect.sense(avalues);
                    if (s2.equals("Top")) {
                        count2 = 1;
                        mSensorManager.registerListener(MainActivity.this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                    }
//                    s = s + "\t" + s2 + count1 + "\t" + count2;
//                    tv2.setText(s);

                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String s;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        s = "X: " + String.format("%.0f", x) + "\nY: " + String.format("%.0f", y) + "\nZ: " + String.format("%.0f", z);
        String s2 = detect.sense(event.values);
        if (s2.equals("Back")) {
            count1 = 1;
        }
        s = s + "\n" + s2 + "\t " + count1;
        if (count1 == 1 && count2 == 1) {
//            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//            Vibrate for 500 milliseconds
//            v.vibrate(10);
            flag = true;

            Wearable.DataApi.removeListener(msgClient, MainActivity.this);
            mSensorManager.unregisterListener(MainActivity.this);
            Toast.makeText(MainActivity.this, "Gesture Complete", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class sendMsgtoWear implements Runnable {

        String path, message;

        public sendMsgtoWear(String s1, String s2) {
            this.path = s1;
            this.message = s2;
        }

        @Override
        public void run() {

            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(msgClient).await();

            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(msgClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}
