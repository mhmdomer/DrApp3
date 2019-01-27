package com.mohammedomer.drapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class Main2Activity extends AppCompatActivity {
    private Handler hHandler = new Handler();
    private Handler tHandler = new Handler();
    private Handler oHandler = new Handler();
    private int oValue;
    private int tValue;
    private int hValue;
    private TextView heartRate;
    private TextView temperature;
    private TextView oxegen;
    private Random random = new Random();

    BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        heartRate = (TextView) findViewById(R.id.heart_rate_value);
        temperature = (TextView) findViewById(R.id.temp_value);
        button1();
    }

    private Runnable hUpdate = new Runnable() {
        public void run() {
            hValue = random.nextInt(80) + 50;
            heartRate.setText(hValue + " r.p.m");
            hHandler.postDelayed(this, 2000);
            if (hValue >= 125) addNotification();
        }
    };

    private Runnable tUpdate = new Runnable() {
        public void run() {
            tValue = random.nextInt(9) + 33;
            temperature.setText(tValue + " °C");
            hHandler.postDelayed(this, 3000);
            if (tValue >= 120) addNotification();
        }
    };

    public void call(View view){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:333"));
        startActivity(intent);
    }
    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this);
        builder.setContentTitle("Critical situation !!");
        builder.setContentText("your patient's health is not stable ");
        builder.setSmallIcon(R.drawable.cardiogram);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        long[] v = {500,1500};
        builder.setVibrate(v);

        Intent notificationIntent = new Intent(this, Main2Activity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }



    public void connect(String newAddress) {
        BluetoothDevice device = mBtAdapter.getRemoteDevice(newAddress);

        //Attempt to create a bluetooth socket for comms
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e1) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create Bluetooth socket", Toast.LENGTH_SHORT).show();
            Log.d("MAinActivity", e1.getStackTrace() + "");
        }


        // Establish the connection.

        try {
            btSocket.connect();
            Toast.makeText(getBaseContext(), "Connected", Toast.LENGTH_SHORT).show();
            Log.d("Main2Activity", "Connected Successfully");


        } catch (Exception e) {
            try {
                btSocket.close();
                Toast.makeText(getBaseContext(), "ERROR - Could not connect,socket closed ", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "ERROR - Could not connect,,socket closed because of : " + e.getStackTrace() + "");
                //If IO exception occurs attempt to close socket
            } catch (Exception e2) {
                Toast.makeText(getBaseContext(), "ERROR - Could not close Bluetooth socket", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity", "ERROR - Could not close socket because of : " + e.getStackTrace() + "");
            }
        }

        // Create a data stream so we can talk to the device
        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create bluetooth outStream", Toast.LENGTH_SHORT).show();
        }

        InputStream intmp = null;

        try {
            intmp = btSocket.getInputStream();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ERROR - Could not create bluetooth inStream", Toast.LENGTH_SHORT).show();
        }

        inStream = intmp;

    }

    private void move(String incomingMessage) {
        heartRate.setText(incomingMessage);
    }


    public void runContinuously(int millis){
        final Handler a = new Handler();
        a.post(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];

                int bytes;

                if (inStream != null) {
                    try {
                        bytes = inStream.read(buffer);
                        String incomingMessage = new String(buffer, 0, bytes);
                        String[] a = incomingMessage.split("\n");
                        Log.d("MainActivity", "message = " + incomingMessage);
                        if (a.length == 2) {
                            if(new Double(a[0]) >135 || new Double(a[0])<40)
                                addNotification();
                            heartRate.setText(a[0]+"b.p.m");
                            temperature.setText(a[1]+"°C");
                        }
                    } catch (IOException e1) {
                        Toast.makeText(getBaseContext(), "write : error while reading from inputStream because of " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                a.postDelayed(this,2000);
            }
        });
    }

    public void button1(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices and append to pairedDevices list
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // Add previously paired devices to the array
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                Toast.makeText(getApplicationContext(),device.getName(),Toast.LENGTH_SHORT).show();
                address = device.getAddress();

            }
        }
    }

    public void button2(View v){
        connect(address);
    }

    public void button3(View v){
        runContinuously(2000);
    }
}