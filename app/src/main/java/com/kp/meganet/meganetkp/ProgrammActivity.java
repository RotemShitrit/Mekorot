package com.kp.meganet.meganetkp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProgrammActivity extends AppCompatActivity implements iCallback{

    private String _lastPromptType;
    private boolean _isLocked;
    private boolean _pairDialogIsON;
    private Spinner promptTypeSpin;
    private Spinner paramSpiner;
    private Button inputBtn;
    private Button promptButton;
    private Button promptTempButton;
    private Button powerOffButton;
    private Button programmButton;
    private Button sleepButton;
    private ListView paramsListView;
    private EditText paramEditText;
    private CheckBox paramCheckBox;
    private TextView deviceTextView;
    private CheckBox unlockCheckBox;
    private TextView txtView;
    private String _selectedItem;
    Map<String, QryParams> _currentReadData;
    private String _toastMessageToDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programm);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _selectedItem = "";
        _pairDialogIsON = false;
        _isLocked = true;

        inputBtn = (Button) findViewById(R.id.buttonInput);
        txtView = (TextView)findViewById(R.id.textView11);
        promptButton  = (Button) findViewById(R.id.buttonPrompt);
        promptTempButton = (Button) findViewById(R.id.button_read_temp);
        powerOffButton = (Button) findViewById(R.id.buttonPowerOff);
        programmButton = (Button) findViewById(R.id.buttonProgramm);
        sleepButton = (Button) findViewById(R.id.buttonSleep);
        paramsListView  = (ListView) findViewById(R.id.listViewParams);
        paramEditText = (EditText) findViewById(R.id.editTextParam);
        paramCheckBox = (CheckBox) findViewById(R.id.checkBoxParam);
        unlockCheckBox = (CheckBox) findViewById(R.id.checkBoxLock);
        deviceTextView = (TextView) findViewById(R.id.textViewDevice);
        promptTypeSpin = (Spinner) findViewById(R.id.spinnerPromptTypes);
        paramSpiner = (Spinner) findViewById(R.id.spinnerParam);
        paramEditText.setEnabled(false);
        MeganetInstances.getInstance().GetMeganetEngine().SetReadMetersRSNT(true);
        MeganetInstances.getInstance().GetMeganetEngine().InitProgramming(this, MeganetInstances.getInstance().GetMeganetDb().getSetting(1).GetKeyValue());

        // Prompt list
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.types_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter1 to the spinner
        promptTypeSpin.setAdapter(adapter1);

        _lastPromptType = "";
        //_lastPromptType = MeganetInstances.getInstance().GetMeganetDb().getSetting(6).GetKeyValue();
        //if(_lastPromptType.length() > 0)
        //    promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert(_lastPromptType)));
        String dev_name_txt = "";
        if (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 1)
        {
            dev_name_txt = "MTPIT";
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MTPIT")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MTPIT")));

        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 2)
        {
            dev_name_txt = "MT2PIT (8 dig)";
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT2W\\MT2PIT\\MC")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT2W\\MT2PIT\\MC")));
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 22)
        {
            dev_name_txt = "MT2PIT (10 dig)";

            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("E")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("E")));
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 3)
        {
            dev_name_txt = "MT1W";

            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT1W")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT1W")));
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 4)
        {
            dev_name_txt = "MT2W (8 dig)";

            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT1W")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT1W")));
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 44)
        {
            dev_name_txt = "MT2W (10 dig)";

            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("E")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("E")));
        }
        else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 5)
        {
            dev_name_txt = "MTWP1";
            //

            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT1W")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MT1W")));
        }
        else if (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 6)
        {
            dev_name_txt = "MTWP";

            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MTWP")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("MTWP")));
        }
        else if (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 7)
        {
            dev_name_txt = "Registers";

            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("E")));
            promptTypeSpin.setSelection(((ArrayAdapter<String>) promptTypeSpin.getAdapter()).getPosition(PromptConvert("E")));

        }



            // Replace "Please magnet swipe node"
        // to "Please Click on READ button and Swipe a magnet on the Node"
        // on 21.11.2019.
        dev_name_txt += "\n\nPlease Click on READ button \nand Swipe a magnet on the MTU";
        txtView.setText(dev_name_txt);
        promptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_currentReadData != null)
                    _currentReadData.clear();
                paramsListView.setAdapter(null);
                _selectedItem = "";
                paramEditText.setText("");
                inputBtn.setVisibility(View.INVISIBLE);
                sleepButton.setVisibility(View.INVISIBLE);
                programmButton.setVisibility(View.INVISIBLE);
                promptTempButton.setVisibility(View.VISIBLE);
                powerOffButton.setVisibility(View.INVISIBLE);
                txtView.setVisibility(View.INVISIBLE);
                paramCheckBox.setVisibility(View.INVISIBLE);
                paramSpiner.setVisibility(View.INVISIBLE);
                paramEditText.setVisibility(View.INVISIBLE);
                unlockCheckBox.setVisibility(View.INVISIBLE);


                //If pulses then Prompt REGULAR
                if((promptTypeSpin.getSelectedItem().toString().equals("MTWE")) || (promptTypeSpin.getSelectedItem().toString().equals("MTWP")))
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.REGULAR, PromptConvert(promptTypeSpin.getSelectedItem().toString()));
                else if (promptTypeSpin.getSelectedItem().toString().equals("E"))
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert(promptTypeSpin.getSelectedItem().toString()));
                else
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert(promptTypeSpin.getSelectedItem().toString()));

                programmButton.setEnabled(false);

                CommonSettingsData data = new CommonSettingsData(6, "last_programm_prompt_type", PromptConvert(promptTypeSpin.getSelectedItem().toString()));

                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);
            }
        });

        promptTempButton.setOnClickListener(new View.OnClickListener() { // Click on Read Button
            @Override
            public void onClick(View v) {
                if (_currentReadData != null)
                    _currentReadData.clear();
                paramsListView.setAdapter(null);
                _selectedItem = "";
                paramEditText.setText("");
                inputBtn.setVisibility(View.INVISIBLE);
                sleepButton.setVisibility(View.INVISIBLE);
                programmButton.setVisibility(View.INVISIBLE);
                promptTempButton.setVisibility(View.VISIBLE);
                powerOffButton.setVisibility(View.INVISIBLE);
                txtView.setVisibility(View.INVISIBLE);
                paramCheckBox.setVisibility(View.INVISIBLE);
                paramSpiner.setVisibility(View.INVISIBLE);
                paramEditText.setVisibility(View.INVISIBLE);
                unlockCheckBox.setVisibility(View.INVISIBLE);


                if (MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 1)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert("MTPIT"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 2)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT2W\\MT2PIT\\MC"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 22)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 3)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 4)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT2W\\MT2PIT\\MC"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 44)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 5)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.PAIRING, PromptConvert("MT1W"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 6)
                {
                   MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.REGULAR, PromptConvert("MTWP"));
                }
                else if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 7)
                {
                    MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"));
                }


                programmButton.setEnabled(false);

                CommonSettingsData data = new CommonSettingsData(6, "last_programm_prompt_type", PromptConvert(promptTypeSpin.getSelectedItem().toString()));

                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);
            }
        });

        inputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProgrammActivity.this, PulseActivity.class);
                startActivity(intent);
                finish();
            }
        });

        powerOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MeganetInstances.getInstance().GetMeganetEngine().MeterPowerOff())
                {
                    deviceTextView.setText("");
                    sleepButton.setVisibility(View.INVISIBLE);
                    programmButton.setVisibility(View.INVISIBLE);
                    promptTempButton.setVisibility(View.VISIBLE);
                    txtView.setVisibility(View.VISIBLE);
                    promptButton.setVisibility(View.INVISIBLE);
                    powerOffButton.setVisibility(View.INVISIBLE);
                    paramCheckBox.setVisibility(View.INVISIBLE);
                    paramSpiner.setVisibility(View.INVISIBLE);
                    paramEditText.setVisibility(View.INVISIBLE);
                    unlockCheckBox.setVisibility(View.INVISIBLE);
                    inputBtn.setVisibility(View.INVISIBLE);
                    paramsListView.setAdapter(null);
                }

            }
        });

        unlockCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(unlockCheckBox.isChecked()){
                    InputUnlockPasswordDialog();
                }else{
                    _isLocked = true;
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        programmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateParamValue();
                if(!MeganetInstances.getInstance().GetMeganetEngine().Programm(_currentReadData))
                    return; // Error. Programm fail. Data is not correct.
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        sleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(MeganetInstances.getInstance().GetMeganetEngine().MeterSleep())
                {
                    deviceTextView.setText("");
                    sleepButton.setVisibility(View.INVISIBLE);
                    programmButton.setVisibility(View.INVISIBLE);
                    promptButton.setVisibility(View.INVISIBLE);
                    promptTempButton.setVisibility(View.VISIBLE);
                    powerOffButton.setVisibility(View.INVISIBLE);
                    paramCheckBox.setVisibility(View.INVISIBLE);
                    paramSpiner.setVisibility(View.INVISIBLE);
                    paramEditText.setVisibility(View.INVISIBLE);
                    unlockCheckBox.setVisibility(View.INVISIBLE);
                    inputBtn.setVisibility(View.INVISIBLE);
                    paramsListView.setAdapter(null);
                }
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        paramsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                // TODO Auto-generated method stub

                UpdateParamValue();

                String s1 = adapter.getItemAtPosition(position).toString();
                _selectedItem = s1;
                QryParams p = _currentReadData.get(s1);
                if(p.ReadOnly.equals("1") && _isLocked)
                {
                    paramCheckBox.setEnabled(false);
                    paramSpiner.setEnabled(false);
                    paramEditText.setEnabled(false);
                }

                else
                {
                    paramCheckBox.setEnabled(true);
                    paramSpiner.setEnabled(true);
                    paramEditText.setEnabled(true);
                }
                if(p.Control.equals("TextBox"))
                {
                    paramCheckBox.setVisibility(View.INVISIBLE);
                    paramSpiner.setVisibility(View.INVISIBLE);
                    paramEditText.setVisibility(View.VISIBLE);


                    if( p.ParameterName.equals("Frequency T"))
                    {
                        //deviceTextView.setText(p.TabName + "  ---  " + p.StepOpt);
                        paramEditText.setText(FrequencyFormat(p.TabName, false));
                    }
                    else if(p.ParameterType.contains("FREQUENCY"))
                    {
                        //deviceTextView.setText(p.TabName + "  ---  " + p.StepOpt);
                        paramEditText.setText(FreqConvert2(p.TabName, p.StepOpt));
                    }
                    else
                    {
                        paramEditText.setText(p.TabName);
                    }

                }
                else if(p.Control.equals("ComboBox"))
                {
                    paramCheckBox.setVisibility(View.INVISIBLE);
                    paramSpiner.setVisibility(View.VISIBLE);
                    paramEditText.setVisibility(View.INVISIBLE);
                    //MeterProtocolConverter
                    if(p.ParameterName.equals("Meter Protocol"))
                        InitSpinnParam(p.MinValue, p.MaxValue, MeterProtocolConverter(p.TabName));
                    else if(p.ParameterName.equals("Power") && p.NDevice == 249)
                        InitSpinnParam(p.MinValue ,p.MaxValue,PowerConvert(Double.valueOf(p.TabName)));
                    else
                        InitSpinnParam(p.MinValue, p.MaxValue, p.TabName);

                    if(p.ParameterName.equals("Divider") && (p.NDevice == 173 || p.NDevice == 168))
                    {
                        if(p.TabName.equals("0"))
                            paramSpiner.setSelection(0);
                        else if(p.TabName.equals("1"))
                            paramSpiner.setSelection(1);
                    }
                    else if(p.ParameterName.equals("Protocol For The Next Inputs") && p.NDevice == 45)
                    {
                        if(p.TabName.equals("1"))
                            paramSpiner.setSelection(0);
                        else if(p.TabName.equals("2"))
                            paramSpiner.setSelection(1);
                    }
                }
                else
                {
                    paramCheckBox.setVisibility(View.VISIBLE);
                    paramSpiner.setVisibility(View.INVISIBLE);
                    paramEditText.setVisibility(View.INVISIBLE);

                    paramCheckBox.setText(s1);
                    if(p.ParameterName.equals("Shabat mode"))
                    {
                        if (p.TabName.equals("170"))
                            paramCheckBox.setChecked(false);
                        else if(p.TabName.equals("204"))
                            paramCheckBox.setChecked(true);
                    }
                    else {
                        if (p.TabName.equals("0"))
                            paramCheckBox.setChecked(false);
                        else
                            paramCheckBox.setChecked(true);
                    }
                }
            }
        });
    }



    private void UpdateParamValue()
    {
        if(_selectedItem.length() > 0)
        {
            if(_currentReadData.get(_selectedItem).Control.equals("TextBox"))
            {
                if( _currentReadData.get(_selectedItem).ParameterName.equals("Frequency T"))
                {
                    if(IsInRange(_currentReadData.get(_selectedItem).ParameterName, _currentReadData.get(_selectedItem).MinValue, _currentReadData.get(_selectedItem).MaxValue, FrequencyFormat(paramEditText.getText().toString(), true)))
                        _currentReadData.get(_selectedItem).TabName = FrequencyFormat(paramEditText.getText().toString(), true);
                    else
                        Toast.makeText(getApplicationContext(), " Update value not in range. /nRange between " + _currentReadData.get(_selectedItem).MinValue + " - " + _currentReadData.get(_selectedItem).MaxValue + ". Update fail !",
                                Toast.LENGTH_LONG).show();
                }
                else if(_currentReadData.get(_selectedItem).ParameterType.contains("FREQUENCY"))
                {
                    if(paramEditText.getText().toString().length() > 0) {
                        if (IsInRange(_currentReadData.get(_selectedItem).ParameterName, _currentReadData.get(_selectedItem).MinValue, _currentReadData.get(_selectedItem).MaxValue, paramEditText.getText().toString())) {
                            _currentReadData.get(_selectedItem).TabName = FreqConvert1(paramEditText.getText().toString(), _currentReadData.get(_selectedItem).StepOpt);
                        } else
                            Toast.makeText(getApplicationContext(), " Update value not in range. /nRange between " + _currentReadData.get(_selectedItem).MinValue + " - " + _currentReadData.get(_selectedItem).MaxValue + ". Update fail !",
                                    Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(IsInRange(_currentReadData.get(_selectedItem).ParameterName, _currentReadData.get(_selectedItem).MinValue, _currentReadData.get(_selectedItem).MaxValue, paramEditText.getText().toString()))
                        _currentReadData.get(_selectedItem).TabName = paramEditText.getText().toString();
                    else
                        Toast.makeText(getApplicationContext(), " Update value not in range. /nRange between " + _currentReadData.get(_selectedItem).MinValue + " - " + _currentReadData.get(_selectedItem).MaxValue + ". Update fail !",
                                Toast.LENGTH_LONG).show();
                }

            }
            else if(_currentReadData.get(_selectedItem).Control.equals("ComboBox"))
            {
                if( _currentReadData.get(_selectedItem).ParameterName.equals("Meter Protocol"))
                    _currentReadData.get(_selectedItem).TabName = MeterProtocolConverter(paramSpiner.getSelectedItem().toString());
                else if(_currentReadData.get(_selectedItem).ParameterName.equals("Power") && _currentReadData.get(_selectedItem).NDevice==249)
                    _currentReadData.get(_selectedItem).TabName = PowerConvert(Double.valueOf(paramSpiner.getSelectedItem().toString()));
                else if(_currentReadData.get(_selectedItem).ParameterName.equals("Divider") &&
                        (_currentReadData.get(_selectedItem).NDevice== 173) || _currentReadData.get(_selectedItem).NDevice== 168)
                {
                    if(paramSpiner.getSelectedItem().toString().equals("No division"))
                        _currentReadData.get(_selectedItem).TabName = "0";
                    else if(paramSpiner.getSelectedItem().toString().equals("Division by 10"))
                        _currentReadData.get(_selectedItem).TabName = "1";
                }
                else if(_currentReadData.get(_selectedItem).ParameterName.equals("Protocol For The Next Inputs") &&
                        _currentReadData.get(_selectedItem).NDevice== 45)
                {
                    if(paramSpiner.getSelectedItem().toString().equals("MBUS"))
                        _currentReadData.get(_selectedItem).TabName = "1";
                    else if(paramSpiner.getSelectedItem().toString().equals("MODBUS"))
                        _currentReadData.get(_selectedItem).TabName = "2";
                }
                else
                    _currentReadData.get(_selectedItem).TabName = paramSpiner.getSelectedItem().toString();
            }
            else
            {
                if(paramCheckBox.isChecked()) {
                    _currentReadData.get(_selectedItem).TabName =
                            String.valueOf((int)_currentReadData.get(_selectedItem).MaxValue);
                }
                else {
                    _currentReadData.get(_selectedItem).TabName =
                            String.valueOf((int)_currentReadData.get(_selectedItem).MinValue);
                }
            }
        }
    }

    private boolean IsInRange(String paramName_prm, Double min_prm, Double max_prm, String value_prm)
    {
        try
        {
            double val = Double.valueOf(value_prm);

            if(val >= min_prm && val <= max_prm)
                return true;
        }
        catch(NumberFormatException nfe)
        {

        }
        return false;
    }

    private void InitSpinnParam(Double minValue, Double maxValue, String value)
    {
        Integer range = maxValue.intValue() - minValue.intValue();
        String[] params = new String [range+1];
        Integer arrVal;
        int ndevice = Integer.decode("0x" +  MeganetInstances.getInstance().GetMeganetEngine().GetNdevice())-1;

        for(Integer i =0; i < range+1; i++)
        {
            arrVal = minValue.intValue()+i;

            if(_selectedItem.equals("Pulses per revolution") && (ndevice == 105 || ndevice == 173 || ndevice == 168) &&
                    arrVal == maxValue.intValue())
            {
                    params[i] = "10";
            }
            else if(_selectedItem.equals("Divider") && (ndevice == 173 || ndevice == 168))
            {
                if (arrVal == 0)
                    params[i] = "No division";
                else if(arrVal == 1)
                    params[i] = "Division by 10";
            }
            else if(_selectedItem.equals("Protocol For The Next Inputs") && (ndevice == 45))
            {
                if(arrVal == 1)
                    params[i] = "MBUS";
                else if(arrVal == 2)
                    params[i] = "MODBUS";
            }
            else
                params[i] = arrVal.toString();
        }

        // Create a List from String Array elements
        final List<String> fruits_list = new ArrayList<String>(Arrays.asList(params));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);

        paramSpiner.setAdapter(arrayAdapter);

        paramSpiner.setSelection(((ArrayAdapter<String>) paramSpiner.getAdapter()).getPosition(value));
    }

    public void SetReadData(Map<String, QryParams> data_prm)
    {
        _currentReadData = data_prm; // check here!!

        // Initializing a new String Array
        int i = 0;

        // Calc params size
        int paramsArrSize = 0;
        for(Map.Entry<String, QryParams> item : data_prm.entrySet())
        {
            if(item.getValue().AndrOption.equals("1"))
                paramsArrSize++;

        }
        String[] params = new String [paramsArrSize];
        for(Map.Entry<String, QryParams> item : data_prm.entrySet())
        {
            if(item.getValue().AndrOption.equals("1"))
                params[i++] = item.getKey();
        }
        // Create a List from String Array elements
        final List<String> fruits_list = new ArrayList<String>(Arrays.asList(params));

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);

        // DataBind ListView with items from ArrayAdapter
        paramsListView.setAdapter(arrayAdapter);
        paramEditText.setEnabled(true);
        promptButton.setEnabled(true);
        programmButton.setEnabled(true);
        sleepButton.setVisibility(View.VISIBLE);
        unlockCheckBox.setVisibility(View.VISIBLE);
        programmButton.setVisibility(View.VISIBLE);
        promptTempButton.setVisibility(View.INVISIBLE);
        powerOffButton.setVisibility(View.VISIBLE);

        if(MeganetInstances.getInstance().GetMeganetEngine().GetCurrentProgrammType() == 6)
            inputBtn.setVisibility(View.VISIBLE);
        else
            inputBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    public void ReadData(byte[] dataArr_prm) {

    }

    @Override
    public void GetTime(byte[] dataArr_prm) {

    }

    public boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly)
    {
        deviceTextView.setText(deviceName_prm.substring(0, deviceName_prm.length()-3));

        if (titleOnly)
            return true;

        if(!_pairDialogIsON)
        {
            _pairDialogIsON = true;
            PairingDialot();
        }

        return true;
    }

    public void OnParameters(String deviceName_prm, List<QryParams> parameters)
    {

    }

    public void OnRead(String deviceName_prm, String ndevice_pam)
    {

    }

    public void OnPowerOff (boolean result_prm, String err_prm)
    {
        if(result_prm)
        {
            if (_currentReadData != null)
                _currentReadData.clear();
            paramsListView.setAdapter(null);

            sleepButton.setVisibility(View.INVISIBLE);
            paramCheckBox.setVisibility(View.INVISIBLE);
            paramSpiner.setVisibility(View.INVISIBLE);
            paramEditText.setVisibility(View.INVISIBLE);
            unlockCheckBox.setVisibility(View.INVISIBLE);

            _toastMessageToDisplay = "Disconnect successfully ";
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            _toastMessageToDisplay = "Disconnect fail ";
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void OnSleep (boolean result_prm, String err_prm)
    {
        if(result_prm)
        {
            if (_currentReadData != null)
                _currentReadData.clear();
            paramsListView.setAdapter(null);

            sleepButton.setVisibility(View.INVISIBLE);
            paramCheckBox.setVisibility(View.INVISIBLE);
            paramSpiner.setVisibility(View.INVISIBLE);
            paramEditText.setVisibility(View.INVISIBLE);
            unlockCheckBox.setVisibility(View.INVISIBLE);

            _toastMessageToDisplay = "Sleep successfully ";
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            _toastMessageToDisplay = "Sleep fail ";
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void OnProgramm(boolean result_prm, String err_prm)
    {
        if(result_prm)
        {
            _toastMessageToDisplay = "MTU programmed successfully ";
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            _toastMessageToDisplay = err_prm;
            this.runOnUiThread(new Runnable() {
                public void run() {
                    // Access/update UI here
                    Toast.makeText(getApplicationContext(), _toastMessageToDisplay,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void OnErrorCb(String error_prm)
    {

    }

    public void OnMessageCb(String message_prm)
    {
        if(message_prm.length() > 0)
        {
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

    private void PairingDialot()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Connect to MTU ID: " + MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + " ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Unit Paired", Toast.LENGTH_SHORT).show();
                        MeganetInstances.getInstance().GetMeganetEngine().PairingDevice(true, false); // Check here!!!!
                        dialog.dismiss();
                        _pairDialogIsON = false;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // some code if you want
                        Toast.makeText(getApplicationContext(),"UNPAIR FROM UNIT",
                                Toast.LENGTH_SHORT).show();
                        MeganetInstances.getInstance().GetMeganetEngine().PairingDevice(false,false);
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
        //bq.setBackgroundColor(Color.BLUE);
    }

    private String FrequencyFormat(String freq_prm, boolean fromDouble)
    {
        String val;
        Double dVal;
        Integer iVal;

        if(fromDouble)
        {
            dVal = Double.valueOf(freq_prm);
            dVal = dVal * 1000000.0;
            iVal = dVal.intValue();
            val = iVal.toString();
        }
        else
        {
            iVal = Integer.valueOf(freq_prm);
            dVal = iVal.doubleValue() / 1000000.0;
            val = dVal.toString();
        }

        return val;
    }

    private String PowerConvert(Double power)
    {
        String ret = "";
        switch (power.toString()) {
            case "93.0":
                ret = "0";
                break;
            case "95.0":
                ret = "1";
                break;
            case "98.0":
                ret = "2";
                break;
            case "100.0":
                ret = "3";
                break;
            case "102.0":
                ret = "4";
                break;
            case "105.0":
                ret = "5";
                break;
            case "107.0":
                ret = "6";
                break;
            case "109.0":
                ret = "7";
                break;
            case "111.0":
                ret = "8";
                break;
            case "114.0":
                ret = "9";
                break;
            case "116.0":
                ret = "10";
                break;
            case "119.0":
                ret = "11";
                break;
            case "121.0":
                ret = "12";
                break;
            case "123.0":
                ret = "13";
                break;
            case "125.0":
                ret = "14`";
                break;
            case "127.0":
                ret = "15";
                break;

            case "0.0":
                ret =  "93";
                break;
            case "1.0":
                ret =  "95";
                break;
            case "2.0":
                ret =  "98";
                break;
            case "3.0":
                ret =  "100";
                break;
            case "4.0":
                ret =  "102";
                break;
            case "5.0":
                ret =  "105";
                break;
            case "6.0":
                ret =  "107";
                break;
            case "7.0":
                ret =  "109";
                break;
            case "8.0":
                ret =  "111";
                break;
            case "9.0":
                ret =  "114";
                break;
            case "10.0":
                ret =  "116";
                break;
            case "11.0":
                ret =  "119";
                break;
            case "12.0":
                ret =  "121";
                break;
            case "13.0":
                ret =  "123";
                break;
            case "14.0":
                ret =  "125";
                break;
            case "15.0":
                ret =  "127";
                break;
        }
        return ret;
    }


    private String MeterProtocolConverter(String type_prm)
    {
        if(type_prm.equals("1"))
            return "4";

        if(type_prm.equals("2"))
            return "3";

        if(type_prm.equals("3"))
            return "2";

        if(type_prm.equals("4"))
            return "1";

        if(type_prm.equals("5"))
            return "5";

        if(type_prm.equals("6"))
            return "6";

        if(type_prm.equals("7"))
            return "7";

        if(type_prm.equals("8"))
            return "8";

        if(type_prm.equals("9"))
            return "9";

        return type_prm;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_programm, menu);

        return true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.menu_meter_protocol:
                Toast.makeText(getApplicationContext(), R.string.meter_protocol_value,
                        Toast.LENGTH_LONG).show();
                break;
/*
            case R.id.menu_program_field_verif:
                super.onBackPressed();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
                intent = new Intent(ProgrammActivity.this, ReadsActivity.class);
                startActivity(intent);
                break;
*/
            case R.id.menu_program_ranman:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();
                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                break;

            case R.id.menu_program_read_meter:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
                intent = new Intent(ProgrammActivity.this, ReadsActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_program_rdm:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "RDM Control", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ProgrammActivity.this, RDM_Controll.class);
                startActivity(intent);
                break;

            case R.id.menu_program_settings:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ProgrammActivity.this, SettingsActivity.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_program_ftp:
                super.onBackPressed();
                Toast.makeText(getApplicationContext(), "FTP", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ProgrammActivity.this, FTP_Controll.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_program_getlog:
                Toast.makeText(getApplicationContext(), "History Log", Toast.LENGTH_LONG).show();
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ProgrammActivity.this, History_Log_1.class);
                startActivity(intent);
                // TODO Something
                break;

            case R.id.menu_program_consumption:
                Toast.makeText(getApplicationContext(), "Flow Rate", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ProgrammActivity.this, ConsumptionActivity.class);
                startActivity(intent);
                // TODO Something
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private String FreqConvert1(String freq_prm, String step_prm)
    {
        Double stepVal;
        String val;

        if(step_prm.equals("0"))
            stepVal = 5.0;
        else
            stepVal = 6.25;
        Double v = Double.valueOf(freq_prm) * (1000.0 / stepVal);

        Integer v1, v2, v3;
        v1 = v.intValue() % 64;
        v2 = (v.intValue() / 64) / 256;
        v3 = (v.intValue() / 64) % 256;

        val = v1.toString() + "," + v2.toString() + "," + v3.toString();

        return  val;

    }

    private String FreqConvert2(String arrData_prm, String step_prm)
    {
        String[] separated = arrData_prm.split(",");
        if(separated.length != 3)
            return "";
        Double stepVal;

        Integer i1, i2, i3;
        i1 = Integer.valueOf(separated[0]);
        i2 = Integer.valueOf(separated[1]);
        i3 = Integer.valueOf(separated[2]);

        Integer v = (((i2 * 256) + i3) * 64) + i1;

        if(step_prm.equals("0"))
            stepVal = 5.0;
        else
            stepVal = 6.25;

        Double value = v.doubleValue() / (1000.0 / stepVal);

        return  value.toString();
    }

    private String PromptConvert(String displayPrompt)
    {
        String convertedPrompt = "";

        if(displayPrompt.equals("MTWE"))
            return "KPMTWEN";

        if(displayPrompt.equals("MTWP"))
            return "KPMTWPN";

        if(displayPrompt.equals("MT1W"))
            return "W";

        if(displayPrompt.equals("MTPIT"))
            return "P";

        if(displayPrompt.equals("MT2W\\MT2PIT\\MC"))
            return "M";
////////////////////////////////////////
        if(displayPrompt.equals( "KPMTWEN"))
            return "MTWE";

        if(displayPrompt.equals("KPMTWPN"))
            return "MTWP";

        if(displayPrompt.equals("W"))
            return "MT1W";

        if(displayPrompt.equals("P"))
            return "MTPIT";

        if(displayPrompt.equals("M"))
            return "MT2W\\MT2PIT\\MC";

        if(displayPrompt.equals( "E"))
            return "E";

        return convertedPrompt;
    }

    private void InputUnlockPasswordDialog()
    {
        // Set an EditText view to get user input
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.programm_password, null);
        final EditText input = promptView.findViewById(R.id.edittext);
        input.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);


        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(promptView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String password = input.getText().toString();
                        if (password.equals("restkpapp")) {
                            _isLocked = false;

                            paramCheckBox.setEnabled(true);
                            paramSpiner.setEnabled(true);
                            paramEditText.setEnabled(true);

                            Toast.makeText(getApplicationContext(), "Unlocked",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            _isLocked = true;

                            paramCheckBox.setEnabled(false);
                            paramSpiner.setEnabled(false);
                            paramEditText.setEnabled(false);

                            Toast.makeText(getApplicationContext(), "Unlock Failed",
                                    Toast.LENGTH_SHORT).show();
                            unlockCheckBox.setChecked(false);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        _isLocked = true;
                        unlockCheckBox.setChecked(false);
                    }
                }).show();
        Button bq = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);

        bq = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setBackgroundColor(Color.WHITE);
        bq.setTextColor(Color.BLUE);
    }
}
