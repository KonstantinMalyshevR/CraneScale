package ru.malyshev.cranescale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    BluetoothSPP bt;

    Button connect_button;
    Button start_button;
    TextView status_text;
    TextView read_text;
    TextView time_text;
    TextView sec_text;
    ProgressBar progress;

    int counter;

    ResultsClass resultsClass;

    ArrayList<Number> listValuesTemp;
    Boolean run_status;

    Timer periodTimer;
    int seconds = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView txt = (TextView) toolbar.findViewById(R.id.toolbar_title);
            txt.setText("Весы");
        }

        listValuesTemp = new ArrayList<>();
        run_status = false;

        connect_button = (Button) findViewById(R.id.connect_button);
        start_button = (Button) findViewById(R.id.start_button);

        read_text = (TextView) findViewById(R.id.read_text);
        status_text = (TextView) findViewById(R.id.status_text);
        time_text = (TextView) findViewById(R.id.time_text);
        sec_text = (TextView) findViewById(R.id.sec_text);

        counter = 0;
        time_text.setText("Измерений: " + counter);

        seconds = 0;
        sec_text.setText("Секунды: " + seconds + " сек.");

        periodTimer = new Timer();
        periodTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(run_status){
                            seconds++;
                            sec_text.setText("Секунды: " + seconds + " сек.");
                        }
                    }
                });
            }
        }, 0, 1000);

        progress = (ProgressBar) findViewById(R.id.progress);

        bt = new BluetoothSPP(this);

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth не доступен", Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                setProgress(false);
                status_text.setText("Статус: Не подключено!");
                status_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_red));
                connect_button.setText("Подключиться");
            }

            public void onDeviceConnectionFailed() {
                setProgress(false);
                status_text.setText("Статус: Не удалось подключиться");
                status_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_red));
                connect_button.setText("Подключиться");
            }

            public void onDeviceConnected(String name, String address) {
                setProgress(false);
                String str = "Статус : Подключено к " + name;
                status_text.setText(str);
                status_text.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent2));
                connect_button.setText("Отключить");
            }
        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                String digits = message.replaceAll("[^\\.0123456789-]","");

                String digitStr = digits + " кг.";
                read_text.setText(digitStr);

                if(run_status){
                    counter++;
                    time_text.setText("Измерений: " + counter);
                    listValuesTemp.add(Float.parseFloat(digits));
                }
            }
        });

        setProgress(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndReadOrCreateBasket();
    }

    //====================
    public void onClickConnectButton(View v){
        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
            bt.disconnect();
        }else{
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);

			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/

            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }

    //====================
    public void onClickStartButton(View v){
        if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
            if(run_status){
                start_button.setText("Старт");
                run_status = false;

                if(listValuesTemp.size() > 0){
                    makeResultOne();
                }else{
                    seconds = 0;
                    sec_text.setText("Секунды: " + seconds + " сек.");

                    counter = 0;
                    time_text.setText("Измерений: " + counter);
                }
            }else{
                start_button.setText("СТОП!");
                listValuesTemp.clear();
                run_status = true;
            }
        }else{
            SupportClass.ToastMessage(MainActivity.this, "Нет соединения");
        }
    }

    private void makeResultOne(){
        ResultOne resultOne = new ResultOne();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        String dateStr = formatter.format(date);

        String str = "Запись № " + resultsClass.getListItemsCount();
        resultOne.setName(str);
        resultOne.setDate(dateStr);
        resultOne.setList(listValuesTemp);
        resultOne.setCounter(counter);
        resultOne.setSec(seconds);

        resultsClass.addToList(resultOne);

        seconds = 0;
        sec_text.setText("Секунды: " + seconds + " сек.");

        counter = 0;
        time_text.setText("Измерений: " + counter);

        saveResults();
    }

    //====================
    public void onClickResButton(View v){
        if(resultsClass.getList().size() > 0){
            startActivity(new Intent(MainActivity.this, ResultsListActivity.class));
        }else{
            SupportClass.ToastMessage(MainActivity.this, "Таблица результатов пуста");
        }
    }

    //=====================
    private void checkAndReadOrCreateBasket(){
        String jsonText = PreferClass.readSharedSetting(MainActivity.this, PreferClass.CS_RESULTS, "non");
        if(!jsonText.equals("non")){
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            ResultsClass resultsClassTemp = gson.fromJson(jsonText, ResultsClass.class);

            if(resultsClassTemp.getResultsClassId().equals("user")){
                resultsClass = resultsClassTemp;
            }else{
                resultsClass = new ResultsClass("user");
                saveResults();
            }

        }else{
            resultsClass = new ResultsClass("user");
            saveResults();
        }
    }

    public void saveResults(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String str = gson.toJson(resultsClass);
        PreferClass.saveSharedSetting(this, PreferClass.CS_RESULTS, str);
    }

    //=====================
    private void setProgress(Boolean value){
        if(value){
            progress.setVisibility(View.VISIBLE);
            connect_button.setEnabled(false);
            start_button.setEnabled(false);
        }else{
            progress.setVisibility(View.GONE);
            connect_button.setEnabled(true);
            start_button.setEnabled(true);
        }
    }

    //=====================
    public void onStart() {
        super.onStart();
        checkAndStartBluetooth();
    }

    private void checkAndStartBluetooth(){
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                bt.connect(data);
                setProgress(true);
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            } else {
                checkAndStartBluetooth();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }
}