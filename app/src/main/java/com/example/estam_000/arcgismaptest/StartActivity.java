package com.example.estam_000.arcgismaptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.startscreen);
        button = (Button) findViewById(R.id.startButton);
    }

    // needs a fix! this is the method called when clicking the only button on startscreen.xml
    public void startTrip(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



}
