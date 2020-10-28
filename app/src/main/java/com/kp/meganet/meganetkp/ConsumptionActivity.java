package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.pow;

public class ConsumptionActivity extends AppCompatActivity implements iCallback {
    private String _toastMessageToDisplay;
    private boolean _pairDialogIsON;

    private Button connectBtn, disconnectBtn, getConsumptionBtn;
    private TextView dataTextView, connectTextView, inputTV, unitTV, registerTypeTV;
    private RadioGroup dataConvert;
    private Spinner inputSpinner,registerSpinner;

    private Timer _downCountTimer;
    private Integer _timerCount;
    private boolean _timerFlag = false;
    double consumption = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _pairDialogIsON = false;
        getConsumptionBtn = (Button) findViewById(R.id.getConsuptBtn);
        connectBtn = (Button) findViewById(R.id.btnConnect);
        disconnectBtn = (Button) findViewById(R.id.btnDisconnect);
        connectTextView = (TextView) findViewById(R.id.textViewConnect);
        dataConvert = (RadioGroup) findViewById(R.id.convertRadioGroup);
        inputSpinner = (Spinner) findViewById(R.id.inputSpinner);
        inputTV = (TextView) findViewById(R.id.inputTextView);
        registerSpinner = (Spinner) findViewById(R.id.registerSpinner);
        registerTypeTV = (TextView) findViewById(R.id.registerTypeTextView);
        dataTextView = (TextView) findViewById(R.id.dataTextView);
        unitTV = (TextView) findViewById(R.id.unit_textView);
        unitTV.setText("");

        getConsumptionBtn.setVisibility(View.INVISIBLE);
        dataConvert.setVisibility(View.INVISIBLE);
        inputTV.setVisibility(View.INVISIBLE);
        inputSpinner.setVisibility(View.INVISIBLE);
        registerSpinner.setVisibility(View.INVISIBLE);
        registerTypeTV.setVisibility(View.INVISIBLE);
        disconnectBtn.setVisibility(View.INVISIBLE);
        dataTextView.setVisibility(View.INVISIBLE);
        unitTV.setVisibility(View.INVISIBLE);

        _downCountTimer = new Timer();
        _downCountTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 1000);

        String[] inputArraySpinner = new String[] {
                "3", "4", "5", "6", "7", "8", "9", "10"};

        ArrayAdapter<String> inputAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, inputArraySpinner);
        inputAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputSpinner.setAdapter(inputAdapter);

        String[] registerArraySpinner = new String[] {
                "MBUS-Sensus HRI-Mei", "MBUS-Sensus Abs.Encoder", "MBUS-Elster Falcon", "MODBUS registers"};

        ArrayAdapter<String> registerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, registerArraySpinner);
        registerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        registerSpinner.setAdapter(registerAdapter);

        MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);
        MeganetInstances.getInstance().GetMeganetEngine().InitProgramming(this, MeganetInstances.getInstance().GetMeganetDb().getSetting(1).GetKeyValue());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                MeganetInstances.getInstance().GetMeganetEngine().SetFrequency();
            }
        }, 2000);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitTV.setText("");
                dataTextView.setText("");
                MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.TEN_CHR_PAIRING, "E");
            }
        });

        disconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MeganetInstances.getInstance().GetMeganetEngine().MeterPowerOff()) {
                    disconnectBtn.setVisibility(View.INVISIBLE);
                    getConsumptionBtn.setVisibility(View.INVISIBLE);
                    dataConvert.setVisibility(View.INVISIBLE);
                    inputTV.setVisibility(View.INVISIBLE);
                    inputSpinner.setVisibility(View.INVISIBLE);
                    registerSpinner.setVisibility(View.INVISIBLE);
                    registerTypeTV.setVisibility(View.INVISIBLE);
                    disconnectBtn.setVisibility(View.INVISIBLE);
                    dataTextView.setVisibility(View.INVISIBLE);
                    unitTV.setVisibility(View.INVISIBLE);

                    connectTextView.setText("לא מחובר");
                    _timerFlag = false;
                    _timerCount = 0;
                }
            }
        });

        getConsumptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object selectedItem = inputSpinner.getSelectedItem();
                MeganetInstances.getInstance().GetMeganetEngine().getConsumption(Integer.parseInt(selectedItem.toString()));
                unitTV.setText("");
                _timerFlag = true;
                _timerCount = 0;
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void reverseArray(byte[] arr)
    {
        int i=0, j=arr.length-1;
        byte temp;

        while(i<j)
        {
            temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;

            i++;
            j--;
        }
    }

    public double getRegisterType()
    {
        double ret = 1;
        switch (registerSpinner.getSelectedItem().toString())
        {
            case "MBUS-Sensus HRI-Mei":
                ret = 100;
                break;
            case "MBUS-Sensus Abs.Encoder":
                break;
            case "MBUS-Elster Falcon":
                ret = 1000;
                break;
            case "MODBUS registers":
                break;
        }
        return ret;
    }

    public double ConvertByteToNumber(byte[] bytes)
    {
        double number = 0;
        for(int i = 0; i<bytes.length; i++)
        {
            number += number * 255 + (bytes[i] & 0xFF);
        }

        return number;
    }

    public int dataType(byte x)
    {
        int num;
        String str = String.format("%8s", Integer.toBinaryString(x & 0xFF)).replace(' ', '0');
        num = Integer.parseInt(str.substring(4), 2);
        if (num == 5) return 4;
        return num;
    }

    public double unitConsumption(byte b, double consumption)
    {
        String str = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        switch (str.substring(5))
        {
            case "000":
                consumption = consumption * pow(10,-6);
                //unitTV.setText(" mL/h");
                break;
            case "001":
                consumption = consumption * pow(10,-5);
                //unitTV.setText("10 mL/h");
                break;
            case "010":
                consumption = consumption * pow(10,-4);
                //unitTV.setText("100 mL/h");
                break;
            case "011":
                consumption = consumption * pow(10,-3);
                //unitTV.setText("L/h");
                break;
            case "100":
                consumption = consumption * pow(10,-2);
                //unitTV.setText("10 L/h");
                break;
            case "101":
                consumption = consumption * pow(10,-1);
                //unitTV.setText("100 L/h");
                break;
            case "110":
                consumption = consumption * pow(10,0);
                //unitTV.setText("m³/h");
                break;
            case "111":
                consumption = consumption * pow(10,1);
                //unitTV.setText("10 m³/h");
                break;
        }
        unitTV.setText("m³/h");
        dataTextView.setText(String.format("%.02f", consumption));
        return consumption;
    }

    public void Oncheck(View v){
    }

    private String PromptConvert(String displayPrompt) {
        String convertedPrompt = "";

        if (displayPrompt.equals("MT2W\\MT2PIT\\MC"))
            return "M";
////////////////////////////////////////

        if (displayPrompt.equals("M"))
            return "MT2W\\MT2PIT\\MC";

        return convertedPrompt;
    }

    public void SetReadData(Map<String, QryParams> data_prm) {

    }

    @Override
    public void ReadData(byte[] dataArr_prm) {
        if(dataArr_prm != null)
        {
            /*int num = dataType(dataArr_prm[7]);
            if (num>0)
            {
                byte[] subArray = Arrays.copyOfRange(dataArr_prm, dataArr_prm.length-4, dataArr_prm.length-4+num);
                reverseArray(subArray);
                consumption = ConvertByteToNumber(subArray);
                consumption = unitConsumption(dataArr_prm[8], consumption);
            }
            else {
                dataTextView.setText("No data!");
                unitTV.setText("");
            }*/
            byte[] subArray = Arrays.copyOfRange(dataArr_prm, dataArr_prm.length-4, dataArr_prm.length);
            reverseArray(subArray);
            consumption = ConvertByteToNumber(subArray);
            double register = getRegisterType();
            consumption = consumption/register;
            unitTV.setText("m³/h");
            dataTextView.setText(String.format("%.03f", consumption));
            //consumption = unitConsumption(dataArr_prm[8], consumption);
            _timerFlag = false;
        }
     }

    @Override
    public void GetTime(byte[] dataArr_prm) {

    }

    private void TimerMethod() {
        try {
            if(_timerFlag)
            {

                this.runOnUiThread(new Runnable() {
                    public void run() {
                        // Access/update UI here
                        Integer val = 60 - _timerCount;
                        dataTextView.setText(val.toString());
                        consumption = 0;
                        if(_timerCount > 60)
                        {
                            Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                                    Toast.LENGTH_SHORT).show();
                            connectTextView.setText("לא מחובר");
                            //powerOffButton.setVisibility(View.INVISIBLE);

                            getConsumptionBtn.setVisibility(View.INVISIBLE);
                            dataConvert.setVisibility(View.INVISIBLE);
                            inputTV.setVisibility(View.INVISIBLE);
                            inputSpinner.setVisibility(View.INVISIBLE);
                            registerSpinner.setVisibility(View.INVISIBLE);
                            registerTypeTV.setVisibility(View.INVISIBLE);
                            disconnectBtn.setVisibility(View.INVISIBLE);
                            dataTextView.setVisibility(View.INVISIBLE);
                            unitTV.setVisibility(View.INVISIBLE);

                            _timerFlag = false;
                            _timerCount = 0;
                            MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);
                        }
                    }
                });
                _timerCount++;
            }
            else
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        // Access/update UI here
                    }
                });

        } catch (Exception e) {

        }
    }

    public boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly) {
        if (!_pairDialogIsON) {
            _pairDialogIsON = true;
            PairingDialot();
        }

        return true;
    }

    public void OnParameters(String deviceName_prm, List<QryParams> parameters) {

    }

    public void OnRead(String deviceName_prm, String ndevice_pam) {

    }

    public void OnPowerOff(boolean result_prm, String err_prm) {

    }

    public void OnSleep(boolean result_prm, String err_prm) {

    }

    public void OnProgramm(boolean result_prm, final String err_prm) {

    }

    public void OnErrorCb(String error_prm) {

    }

    public void OnMessageCb(String message_prm) {
        if (message_prm.length() > 0) {
            _toastMessageToDisplay = message_prm;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void PairingDialot() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("התחבר עם מכשיר: " + MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + " ?")
                .setCancelable(false)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "היחידה התחברה",
                                Toast.LENGTH_SHORT).show();
                        //powerOffButton.setVisibility(View.VISIBLE);
                        MeganetInstances.getInstance().GetMeganetEngine().PairingDevice(true, false);
                        connectTextView.setText("התחבר עם מכשיר: " + MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress());
                        dialog.dismiss();

                        getConsumptionBtn.setVisibility(View.VISIBLE);
                        //dataConvert.setVisibility(View.VISIBLE);
                        inputTV.setVisibility(View.VISIBLE);
                        inputSpinner.setVisibility(View.VISIBLE);
                        registerSpinner.setVisibility(View.VISIBLE);
                        registerTypeTV.setVisibility(View.VISIBLE);
                        disconnectBtn.setVisibility(View.VISIBLE);
                        dataTextView.setVisibility(View.VISIBLE);
                        unitTV.setVisibility(View.VISIBLE);

                        _pairDialogIsON = false;
                    }
                })
                .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // some code if you want
                        Toast.makeText(getApplicationContext(), "ההתחברות ליחידה התבטלה",
                                Toast.LENGTH_SHORT).show();
                        connectTextView.setText("לא מחובר");
                        MeganetInstances.getInstance().GetMeganetEngine().Disconnect();
                        dialog.dismiss();
                        _pairDialogIsON = false;

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        Button bq = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);

        bq = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consumption, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (item.getItemId()) {
/*
            case R.id.menu_consumption_field_verif:
                super.onBackPressed();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
                intent = new Intent(ConsumptionActivity.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_consumption_ranman:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();
                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;

            case R.id.menu_consumption_read_meter:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
                intent = new Intent(ConsumptionActivity.this, ReadsActivity.class);
                startActivity(intent);
                break;
*/
            case R.id.menu_consumption_ftp:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "הגדרות FTP", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ConsumptionActivity.this, FTP_Controll.class);
                startActivity(intent);
                break;

            case R.id.menu_consumption_getlog:
                Toast.makeText(getApplicationContext(), "יומן היסטוריה", Toast.LENGTH_LONG).show();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ConsumptionActivity.this, History_Log_1.class);
                startActivity(intent);
                break;

            case R.id.menu_consumption_settings:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "הגדרות", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ConsumptionActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
/*
            case R.id.menu_consumption_program:
                Toast.makeText(getApplicationContext(), "Programming", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ConsumptionActivity.this, ChooseDeviceActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_consumption_rdm:
                Toast.makeText(getApplicationContext(), "RDM Control", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ConsumptionActivity.this, RDM_Controll.class);
                startActivity(intent);
                break;
                
 */
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RDM_Controll Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kp.meganet.meganetkp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RDM_Controll Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kp.meganet.meganetkp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
