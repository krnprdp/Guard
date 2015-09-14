package com.pradeep.cse664.project3.guard;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Random;

/**
 * Created by Pradeep on 5/7/15.
 */
public class VerifyActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static final String store = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random random = new Random();
    static String password;
    Button btnVerify;
    EditText etPassword;
    GoogleApiClient msgClient;

    @Override
    protected void onStart() {
        super.onStart();
        msgClient.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        msgClient.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lockscreen2);
        msgClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        password = genPassword(5);
        //Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();
        Thread t = new Thread(new sendMsgtoWear("/message", password));
        t.start();
        btnVerify = (Button) findViewById(R.id.btnVerify);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = etPassword.getText().toString();
                if (s.equals(password)) {
                    Intent i3 = new Intent(VerifyActivity.this, UnlockActivity.class);
                    startActivity(i3);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, Try again", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                }
            }
        });

    }

    String genPassword(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(store.charAt(random.nextInt(store.length())));
        return sb.toString();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
