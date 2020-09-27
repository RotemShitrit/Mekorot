package com.kp.meganet.meganetkp;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;


public class Ber1Activity extends AppCompatActivity {

    private Spinner meterSpinner, sizeSpinner, flowUnitSpinner, accUnitSpinner, resolutionSpinner, pulseWidthSpinner;
    private EditText id, factor, Q3, Q03, Q2, Q1, positive, negative, accumulation;
    private Button programBtn,saveBtn, loadBtn;
    ArrayList<String> meters, sizes, flows, accUnit, resolution;
    Double acc, pos, neg, res;
    String fileName;
    ArrayAdapter<String> meterSpinnerArrayAdapter,sizeSpinnerArrayAdapter,flowSpinnerArrayAdapter,
            resolutionArrayAdapter, accUnitArrayAdapter, pulseWidthSpinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ber1_programming);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        meterSpinner = (Spinner) findViewById(R.id.MeterSpinner);
        sizeSpinner = (Spinner) findViewById(R.id.SpinnerSize);
        flowUnitSpinner = (Spinner) findViewById(R.id.SpinnerFlowUnit);
        accUnitSpinner = (Spinner) findViewById(R.id.SpinnerAccUnit);
        resolutionSpinner = (Spinner) findViewById(R.id.SpinnerResolution);
        pulseWidthSpinner = (Spinner) findViewById(R.id.SpinnerPulseWidth);
        positive = (EditText) findViewById(R.id.editTextPositive);
        negative = (EditText) findViewById(R.id.editTextNegative);
        accumulation = (EditText) findViewById(R.id.editTextAccomulation);
        id = (EditText) findViewById(R.id.editTextID);
        factor = (EditText) findViewById(R.id.editTextFactor);
        Q3 = (EditText) findViewById(R.id.editTextQ3);
        Q03 = (EditText) findViewById(R.id.editTextQ03);
        Q2 = (EditText) findViewById(R.id.editTextQ2);
        Q1 = (EditText) findViewById(R.id.editTextQ1);
        programBtn = (Button) findViewById(R.id.programBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        loadBtn = (Button) findViewById(R.id.loadBtn);

        programBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = "ID_"+id.getText().toString()+ ".csv";
                String data = getData();

                if(isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/saved_ber1_files");
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    File file = new File(myDir, fileName);
                    if (file.exists())
                        file.delete();

                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        out.write(data.getBytes());
                        out.close();
                        Toast.makeText(getApplicationContext(), "File saved in saved_ber1_files folder" , Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Cannot save file on external storage!",Toast.LENGTH_LONG).show();
                }
            }
        });

        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                Intent fileintent =new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setDataAndType(uri,"*/*");
                try {
                    startActivityForResult(fileintent, 10);
                } catch (ActivityNotFoundException e) {
                    Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                }
            }
        });

        meters = new ArrayList<String>();
        sizes = new ArrayList<String>();
        flows = new ArrayList<String>();
        accUnit = new ArrayList<String>();
        resolution = new ArrayList<String>();

        meters.add("900");
        meters.add("TurboBAR");
        meters.add("TurboIR");

        // Initializing an ArrayAdapter
        meterSpinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.checked, meters);
        meterSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        meterSpinner.setAdapter(meterSpinnerArrayAdapter);

        meterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sizes.clear();
                String meter = (String) meterSpinner.getSelectedItem();
                if(meter.equals("900")) {
                    //sizes = new String[]{"2\"", "3\"", "4\"", "6\"", "8\""};
                    sizes.add("2\"");
                    sizes.add("3\"");
                    sizes.add("4\"");
                    sizes.add("6\"");
                    sizes.add("8\"");
                } else if(meter.equals("TurboBAR")){
                    //sizes = new String[]{"2.5\"", "3\"", "4\"", "5\"", "6\"", "8\"", "10\"", "11\"", "12\""};
                    sizes.add("2.5\"");
                    sizes.add("3\"");
                    sizes.add("4\"");
                    sizes.add("5\"");
                    sizes.add("6\"");
                    sizes.add("8\"");
                    sizes.add("10\"");
                    sizes.add("11\"");
                    sizes.add("12\"");
                } else if(meter.equals("TurboIR")){
                    //sizes = new String[]{"1.5\"", "2\"", "2.5\"", "3\"", "4\"", "5\"", "6\"", "8\"", "10\"", "12\"", "16\"", "20\""};
                    sizes.add("1.5\"");
                    sizes.add("2\"");
                    sizes.add("2.5\"");
                    sizes.add("3\"");
                    sizes.add("4\"");
                    sizes.add("5\"");
                    sizes.add("6\"");
                    sizes.add("8\"");
                    sizes.add("10\"");
                    sizes.add("12\"");
                    sizes.add("16\"");
                    sizes.add("20\"");
                }

                // Initializing an ArrayAdapter
                sizeSpinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checked, sizes);
                sizeSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                sizeSpinner.setAdapter(sizeSpinnerArrayAdapter);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        id.setText("100");
        factor.setText("25000");
        Q3.setText("-9.99");
        Q03.setText("1.44");
        Q2.setText("-0.8");
        Q1.setText("1.23");
        accumulation.setText("11111");
        negative.setText("1100");
        positive.setText(String.valueOf(Integer.valueOf(String.valueOf(accumulation.getText())) + Integer.valueOf(String.valueOf(negative.getText()))));

        accumulation.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(res<1 && acc*res == Double.valueOf(String.valueOf(accumulation.getText())))
                    {
                        //Do Nothing
                    }
                    else {
                        acc = Double.valueOf(String.valueOf(accumulation.getText()));
                        pos = acc + neg;

                        if (accUnitSpinner.getSelectedItem().toString().equals("M3")) {
                            res = Double.valueOf(resolutionSpinner.getSelectedItem().toString());
                            if (res < 1) {
                                accumulation.setText(String.valueOf(acc * res));
                                negative.setText(String.valueOf(neg * res));
                                positive.setText(String.valueOf(pos * res));
                            }
                        }
                    }
                }
            }
        });

        negative.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(res<1 && neg*res == Double.valueOf(String.valueOf(negative.getText())))
                    {
                        //Do Nothing
                    }
                    else {
                        neg = Double.valueOf(String.valueOf(negative.getText()));
                        pos = acc + neg;

                        if (accUnitSpinner.getSelectedItem().toString().equals("M3")) {
                            res = Double.valueOf(resolutionSpinner.getSelectedItem().toString());
                            if (res < 1) {
                                accumulation.setText(String.valueOf(acc * res));
                                negative.setText(String.valueOf(neg * res));
                                positive.setText(String.valueOf(pos * res));
                            }
                        }
                    }
                }
            }
        });

        flows.add("M3/h");
        flows.add("L/S");
        flows.add("GPM");
        flows.add("CFS");

        // Initializing an ArrayAdapter
        flowSpinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.checked, flows);
        flowSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        flowUnitSpinner.setAdapter(flowSpinnerArrayAdapter);

        flowUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                accUnit.clear();
                resolution.clear();
                String flowUnit = (String) flowUnitSpinner.getSelectedItem();

                if (flowUnit.equals("M3/h") || flowUnit.equals("L/S")) {
                    //AccUnit = new String[]{"M3"};
                    accUnit.add("M3");
                    //resolution = new String[]{"0.001","0.01","0.1","1","10","100"};
                    resolution.add("0.001");
                    resolution.add("0.01");
                    resolution.add("0.1");
                    resolution.add("1");
                    resolution.add("10");
                    resolution.add("100");

                } else if (flowUnit.equals("GPM")) {
                    //AccUnit = new String[]{"GAL", "AF"};
                    accUnit.add("GAL");
                    accUnit.add("AF");
                    //resolution = new String[]{"1","10","100","1000","10000"};
                    resolution.add("1");
                    resolution.add("10");
                    resolution.add("100");
                    resolution.add("1000");
                    resolution.add("10000");

                } else if (flowUnit.equals("CFS")) {
                   //AccUnit = new String[]{"AF","CFT"};
                    accUnit.add("AF");
                    accUnit.add("CFT");
                    //resolution = new String[]{"0.1","1","10","100","1000","10000"};
                    resolution.add("0.1");
                    resolution.add("1");
                    resolution.add("10");
                    resolution.add("100");
                    resolution.add("1000");
                    resolution.add("10000");
                }

                accUnitArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checked, accUnit);
                accUnitArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                accUnitSpinner.setAdapter(accUnitArrayAdapter);

                resolutionArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checked, resolution);
                resolutionArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                resolutionSpinner.setAdapter(resolutionArrayAdapter);

                resolutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        res = Double.valueOf(resolutionSpinner.getSelectedItem().toString());
                        if(res<1) {
                            accumulation.setText(String.valueOf(acc * res));
                            negative.setText(String.valueOf(neg * res));
                            positive.setText(String.valueOf(pos * res));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                accUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(accUnitSpinner.getSelectedItem().toString().equals("M3"))
                        {
                            res = Double.valueOf(resolutionSpinner.getSelectedItem().toString());
                            if(res<1) {
                                accumulation.setText(String.valueOf(acc * res));
                                negative.setText(String.valueOf(neg * res));
                                positive.setText(String.valueOf(pos * res));
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] pulseWidths = {"off", "50", "100", "200", "300", "400", "500"};
        pulseWidthSpinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checked, pulseWidths);
        pulseWidthSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        pulseWidthSpinner.setAdapter(pulseWidthSpinnerArrayAdapter);
        pulseWidthSpinner.setSelection(0);

        acc = Double.valueOf(String.valueOf(accumulation.getText()));
        neg = Double.valueOf(String.valueOf(negative.getText()));
        pos = Double.valueOf(String.valueOf(positive.getText()));

        meterSpinner.setSelection(0);
        flowUnitSpinner.setSelection(0);
    }
    private String getData()
    {
        String data = "";
        data += "ID," + id.getText().toString() + "\n";
        data += "Accumulation," + accumulation.getText().toString() + "\n";
        data += "Negative," + negative.getText().toString() + "\n";
        data += "Meter Type," + (String) meterSpinner.getSelectedItem() + "\n";
        data += "Size," + (String) sizeSpinner.getSelectedItem() + "\n";
        data += "Flow unit," + (String) flowUnitSpinner.getSelectedItem() + "\n";
        data += "Accumulation unit," + (String) accUnitSpinner.getSelectedItem() + "\n";
        data += "Resolution," + (String) resolutionSpinner.getSelectedItem() + "\n";
        data += "Pulse Width," + (String) pulseWidthSpinner.getSelectedItem() + "\n";
        data += "Factor Q4," + factor.getText().toString() + "\n";
        data += "Q3," + Q3.getText().toString() + "\n";
        data += "Q03," + Q03.getText().toString() + "\n";
        data += "Q2," + Q2.getText().toString() + "\n";
        data += "Q1," + Q1.getText().toString() + "\n";
        return data;
    }

    public boolean checkPermission(String permmission){
        int check = ContextCompat.checkSelfPermission(this,permmission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

    private boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.i("State", "Yes, it is writable!");
            return true;
        }   else {
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 10:
                if(resultCode == RESULT_OK) {
                    try {
                        fileName = data.getData().getPath().substring(data.getData().getPath().lastIndexOf("/") + 1);
                        Map<String, String> fileData = new HashMap<String, String>();
                        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/saved_ber1_files");
                        if (dir.exists()) {
                            File file = new File(dir, fileName);
                            FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                            OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
                            StringBuilder text = new StringBuilder();
                            try {
                                BufferedReader br = new BufferedReader(new FileReader(file));
                                String line;
                                while ((line = br.readLine()) != null) {
                                    text.append(line);
                                    text.append('\n');
                                }
                                br.close();
                                outputWriter.write(String.valueOf(text));
                                outputWriter.close();
                                setData(text.toString());

                            } catch (IOException e) {
                                //You'll need to add proper error handling here
                            }
                        }
                    } catch (IOException e){

                    }
                }
                break;
        }
    }

    private void setData(String data)
    {
        int i;
        String[] dataArr = data.split("\n");
        for(i=0;i<dataArr.length;i++)
        {
            String[] line = dataArr[i].split(",");
            switch (line[0])
            {
                case "ID":
                    id.setText(line[1]);
                    break;
                case "Accumulation":
                    accumulation.setText(line[1]);
                    break;
                case "Negative":
                    negative.setText(line[1]);
                    break;
                case "Meter Type":
                    sizes.clear();
                    switch (line[1])
                    {
                        case "900":
                            meterSpinner.setSelection(0);
                            sizes.add("2\"");
                            sizes.add("3\"");
                            sizes.add("4\"");
                            sizes.add("6\"");
                            sizes.add("8\"");
                            break;
                        case "TurboBAR":
                            meterSpinner.setSelection(1);
                            sizes.add("2.5\"");
                            sizes.add("3\"");
                            sizes.add("4\"");
                            sizes.add("5\"");
                            sizes.add("6\"");
                            sizes.add("8\"");
                            sizes.add("10\"");
                            sizes.add("11\"");
                            sizes.add("12\"");
                            break;
                        case "TurboIR":
                            meterSpinner.setSelection(2);
                            sizes.add("1.5\"");
                            sizes.add("2\"");
                            sizes.add("2.5\"");
                            sizes.add("3\"");
                            sizes.add("4\"");
                            sizes.add("5\"");
                            sizes.add("6\"");
                            sizes.add("8\"");
                            sizes.add("10\"");
                            sizes.add("12\"");
                            sizes.add("16\"");
                            sizes.add("20\"");
                            break;
                    }
                    sizeSpinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checked, sizes);
                    sizeSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    sizeSpinner.setAdapter(sizeSpinnerArrayAdapter);
                    break;
                case "Size":
                    switch (meterSpinner.getSelectedItem().toString())
                    {
                        case "900":
                            switch (line[1])
                            {
                                case "2\"":
                                    sizeSpinner.setSelection(0);
                                    break;
                                case "3\"":
                                    sizeSpinner.setSelection(1);
                                    break;
                                case "4\"":
                                    sizeSpinner.setSelection(2);
                                    break;
                                case "6\"":
                                    sizeSpinner.setSelection(3);
                                    break;
                                case "8\"":
                                    sizeSpinner.setSelection(4);
                                    break;
                            }
                            break;
                        case "TurboBAR":
                            switch (line[1])
                            {
                                case "2.5\"":
                                    sizeSpinner.setSelection(0);
                                    break;
                                case "3\"":
                                    sizeSpinner.setSelection(1);
                                    break;
                                case "4\"":
                                    sizeSpinner.setSelection(2);
                                    break;
                                case "5\"":
                                    sizeSpinner.setSelection(3);
                                    break;
                                case "6\"":
                                    sizeSpinner.setSelection(4);
                                    break;
                                case "8\"":
                                    sizeSpinner.setSelection(5);
                                    break;
                                case "10\"":
                                    sizeSpinner.setSelection(6);
                                    break;
                                case "11\"":
                                    sizeSpinner.setSelection(7);
                                    break;
                                case "12\"":
                                    sizeSpinner.setSelection(8);
                                    break;
                            }
                            break;
                        case "TurboIR":
                            switch (line[1])
                            {
                                case "1.5\"":
                                    sizeSpinner.setSelection(0);
                                    break;
                                case "2\"":
                                    sizeSpinner.setSelection(1);
                                    break;
                                case "2.5\"":
                                    sizeSpinner.setSelection(2);
                                    break;
                                case "3\"":
                                    sizeSpinner.setSelection(3);
                                    break;
                                case "4\"":
                                    sizeSpinner.setSelection(4);
                                    break;
                                case "5\"":
                                    sizeSpinner.setSelection(5);
                                    break;
                                case "6\"":
                                    sizeSpinner.setSelection(6);
                                    break;
                                case "8\"":
                                    sizeSpinner.setSelection(7);
                                    break;
                                case "10\"":
                                    sizeSpinner.setSelection(8);
                                    break;
                                case "12\"":
                                    sizeSpinner.setSelection(9);
                                    break;
                                case "16\"":
                                    sizeSpinner.setSelection(10);
                                    break;
                                case "20\"":
                                    sizeSpinner.setSelection(11);
                                    break;
                            }
                            break;
                    }
                    break;
                case "Flow unit":
                    accUnit.clear();
                    resolution.clear();
                    switch (line[1])
                    {
                        case "M3/h":
                            flowUnitSpinner.setSelection(0);
                            accUnit.add("M3");
                            resolution.add("0.001");
                            resolution.add("0.01");
                            resolution.add("0.1");
                            resolution.add("1");
                            resolution.add("10");
                            resolution.add("100");
                            break;
                        case "L/S":
                            flowUnitSpinner.setSelection(1);
                            accUnit.add("M3");
                            resolution.add("0.001");
                            resolution.add("0.01");
                            resolution.add("0.1");
                            resolution.add("1");
                            resolution.add("10");
                            resolution.add("100");
                            break;
                        case "GPM":
                            flowUnitSpinner.setSelection(2);
                            accUnit.add("GAL");
                            accUnit.add("AF");
                            resolution.add("1");
                            resolution.add("10");
                            resolution.add("100");
                            resolution.add("1000");
                            resolution.add("10000");
                            break;
                        case "CFS":
                            flowUnitSpinner.setSelection(3);
                            accUnit.add("AF");
                            accUnit.add("CFT");
                            resolution.add("0.1");
                            resolution.add("1");
                            resolution.add("10");
                            resolution.add("100");
                            resolution.add("1000");
                            resolution.add("10000");
                            break;
                    }
                    accUnitArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checked, accUnit);
                    accUnitArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    accUnitSpinner.setAdapter(accUnitArrayAdapter);

                    resolutionArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.checked, resolution);
                    resolutionArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    resolutionSpinner.setAdapter(resolutionArrayAdapter);
                    break;
                case "Accumulation unit":
                    switch (flowUnitSpinner.getSelectedItem().toString())
                    {
                        case "GPM":
                            if(line[1].equals("GAL"))
                                accUnitSpinner.setSelection(0);
                            else
                                accUnitSpinner.setSelection(1);
                            break;
                        case "CFS":
                            if(line[1].equals("AF"))
                                accUnitSpinner.setSelection(0);
                            else
                                accUnitSpinner.setSelection(1);
                            break;
                        default:
                            accUnitSpinner.setSelection(0);
                    }

                    break;
                case "Resolution":
                    res = Double.valueOf(line[1]);
                    if(res<1) {
                        acc = acc / res;
                        neg = neg / res;
                        pos = acc + neg;
                        positive.setText(String.valueOf(pos*res));
                    }
                    if (accUnitSpinner.getSelectedItem().toString().equals("M3"))
                    {
                        if (res == (double) 0.001) {
                            resolutionSpinner.setSelection(0);
                        } else if (res == (double) 0.01) {
                            resolutionSpinner.setSelection(1);
                        } else if (res == (double) 0.1) {
                            resolutionSpinner.setSelection(2);
                        } else if (res == (double) 1){
                            resolutionSpinner.setSelection(3);
                        } else if (res == (double) 10) {
                            resolutionSpinner.setSelection(4);
                        } else if (res == (double) 100)
                            resolutionSpinner.setSelection(5);
                    }
                    break;
                case "Pulse Width":
                    switch (line[1])
                    {
                        case "off":
                            pulseWidthSpinner.setSelection(0);
                            break;
                        case "50":
                            pulseWidthSpinner.setSelection(1);
                            break;
                        case "100":
                            pulseWidthSpinner.setSelection(2);
                            break;
                        case "200":
                            pulseWidthSpinner.setSelection(3);
                            break;
                        case "300":
                            pulseWidthSpinner.setSelection(4);
                            break;
                        case "400":
                            pulseWidthSpinner.setSelection(5);
                            break;
                        case "500":
                            pulseWidthSpinner.setSelection(6);
                            break;
                    }
                    break;
                case "Factor Q4":
                    factor.setText(line[1]);
                    break;
                case "Q3":
                    Q3.setText(line[1]);
                    break;
                case "Q03":
                    Q03.setText(line[1]);
                    break;
                case "Q2":
                    Q2.setText(line[1]);
                    break;
                case "Q1":
                    Q1.setText(line[1]);
                    break;
            }
        }
    }
}