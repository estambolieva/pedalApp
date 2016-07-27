package com.example.estam_000.arcgismaptest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.startscreen);
        button = (Button) findViewById(R.id.startButton);
    }

    public void startTrip(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);

        Context context = getApplicationContext();
        CharSequence text = "A começar gravação e a 'aquecer' o gps\nBoa pedalada!";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // what happens when returning to this activity?

}
