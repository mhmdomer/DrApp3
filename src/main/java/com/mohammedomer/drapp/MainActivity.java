package com.mohammedomer.drapp;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    LinearLayout l1,l2 ;
    Button btnsub ;
    Animation uptodown ,downtoup;
    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnsub = (Button)findViewById(R.id.buttonsub);
        l1=(LinearLayout)findViewById(R.id.l1);
        l2=(LinearLayout)findViewById(R.id.l2);
        uptodown= AnimationUtils.loadAnimation(this,R.anim.uptodown);
        l1.setAnimation(uptodown);
        downtoup =AnimationUtils.loadAnimation(this,R.anim.downtoup);
        l2.setAnimation(downtoup);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBTState();
    }

    public void onButtonClick(View v){
        if(v.getId()==R.id.buttonsub){
            Intent i = new Intent(MainActivity.this,Main2Activity.class);
            startActivity(i);
        }
    }

    //method to check if the device has Bluetooth and if it is on.
    //Prompts the user to turn it on if it is off
    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if (mBtAdapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!mBtAdapter.isEnabled()) {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

}
