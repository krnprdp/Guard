package com.pradeep.cse664.project3.guard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Pradeep on 5/7/15.
 */
public class SecondActivity extends Activity {
    Button btnNext;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        btnNext = (Button) findViewById(R.id.btnNext);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = etPassword.getText().toString();

                if (s.equals("qwerty")) {
                    Intent i2 = new Intent(SecondActivity.this, VerifyActivity.class);
                    startActivity(i2);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect Password, Try again", Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                }


            }
        });


    }
}
