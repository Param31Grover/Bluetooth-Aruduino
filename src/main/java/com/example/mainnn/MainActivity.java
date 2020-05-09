package com.example.cortex_part1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText input;
    private Button ok,btndis;
    private int angle = 0;

    String address = null;

    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static  final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  //doubt in UUID..


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spinner Drop down list of fingers....
        final Spinner spinner =findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter =ArrayAdapter.createFromResource(this,R.array.Finger,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // taking input angle 0 to 180 and showing toast of the angle moved..

        input = findViewById(R.id.editText);
        ok = findViewById(R.id.ok);
        btndis = findViewById(R.id.disconnect);

        new ConnectBT().execute();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(input.getText().toString());

                if(val<0||val>180){
                    Toast.makeText(getApplicationContext(),"Invalid input. Please enter angle between 0 to 180",Toast.LENGTH_LONG).show();
                }
                else{
                    String s1=  Integer.toString(spinner.getSelectedItemPosition()) + val;
                    Toast.makeText(getApplicationContext(),s1,Toast.LENGTH_LONG).show();

                    SendSignal(s1);  //sending the string via bluetooth.
                }
            }
        });
        btndis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text= parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void SendSignal(String number){
        if(btSocket!=null){
            try {
                btSocket.getOutputStream().write(number.toString().getBytes());
            }
            catch (IOException e){
                msg("Error");

            }
        }

    }

    private void Disconnect(){  //for disconnecting from the device
        if(btSocket!=null){
            try {
                btSocket.close();
            }
            catch (IOException e){
                msg("Error");
            }

        }
        finish();

    }

    private void msg(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
    @SuppressLint("StaticFieldLeak")  //static leak avoid.
    private class ConnectBT extends AsyncTask<Void , Void , Void>{
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progress =  ProgressDialog.show(MainActivity.this,"Connecting...", "Please Wait!!!");
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try{
                if(btSocket!=null||!isBtConnected){
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();

                }

            }

            catch (IOException e){
                ConnectSuccess = false;
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess){
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();

            }
            else {
                msg("Connected");
                isBtConnected = true;
            }
            progress.dismiss();
        }



    }


}
