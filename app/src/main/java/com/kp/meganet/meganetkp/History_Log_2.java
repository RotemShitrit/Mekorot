package com.kp.meganet.meganetkp;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class History_Log_2 extends AppCompatActivity implements iReadMeterCallBack,iCallback {
    private boolean _pairDialogIsON;
    Map<Double,Double> messages;

    private TextView tv16,tv1;
    private RadioGroup input;
    private Spinner inputSpinner;
    private Button getLogBtn,promptBtn;
    private ProgressBar pb;

    Toast toast;
    boolean send_request = false; // a flag that indicate if we start with requests messages
    FTPClient mFTPClient = null; //for access to FTP
    String new_fileName;// name of file
    String dataFile = ""; //all data that file include

    double curr_index;
    int msgSize;
    int current_length, input_num, length, total, start;
    String[] inputs = {"1","2","3","4","5","6","7","8","9","10"};
    String[] columns = {"ID", "Hour", "Message", "Address"}; //Columns of table that will saved in CSV file in FTP
    Calendar last_read; // date of the last read

    //Toast cannot appear without runnable
    Runnable nullRunnable = new Runnable() {
        @Override
        public void run() {
            MeganetInstances.getInstance().GetMeganetEngine().Disconnect();
            MeganetInstances.getInstance().GetMeganetEngine().reset_timerCount();
            Toast.makeText(getApplicationContext(), "החיבור אבד!", Toast.LENGTH_LONG).show();

            synchronized(this)
            {
                this.notify();
            }

            finish();
        }
    };


    Runnable getLogRunnable = new Runnable() {
        @Override
        public void run() {
            int len1 = length;
            if (start + length < total) //If we didn't get all of the reads
            {
                start += length;
                if (start + length > total)
                    len1 = total - start + 1;
                Toast.makeText(getApplicationContext(), "מקבל...", Toast.LENGTH_SHORT).show();
                MeganetInstances.getInstance().GetMeganetEngine().GetLog(input_num, start, len1, false);
            }
            else
            {
                if (!send_request) { // If we ask all of reads and didn't receive all, we need to send request for the missing reads
                    Toast.makeText(getApplicationContext(), "מסיים תהליך...", Toast.LENGTH_LONG).show();
                    send_request = true;
                }
                send_requests();
            }
        }
    };

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_log_2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);

        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = v.findViewById(R.id.mytext);
        titleTxtView.setText("יומן היסטוריית קריאות");

        toast = new Toast(this);
        //input = (RadioGroup) findViewById(R.id.inputRadio);
        inputSpinner = (Spinner) findViewById(R.id.spinnerInputs);
        tv16 = (TextView) findViewById(R.id.textView16);
        tv1 = (TextView) findViewById(R.id.textViewMain);
        getLogBtn = (Button) findViewById(R.id.getLogBtn);
        promptBtn = (Button) findViewById(R.id.promptbtn);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        pb.getIndeterminateDrawable().setColorFilter(Color.parseColor("#e66807"),android.graphics.PorterDuff.Mode.SRC_ATOP);
        last_read = Calendar.getInstance(); // get the current date

        pb.setVisibility(View.INVISIBLE);
        getLogBtn.setVisibility(View.INVISIBLE);
        tv16.setVisibility(View.INVISIBLE);
        //input.setVisibility(View.INVISIBLE);
        inputSpinner.setVisibility(View.INVISIBLE);

        // Initializing an ArrayAdapter
        ArrayAdapter<String> inputsSpinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.checked, inputs
        );
        inputsSpinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        inputSpinner.setAdapter(inputsSpinnerArrayAdapter);

        inputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String input = (String) inputSpinner.getSelectedItem();
                switch (input)
                {
                    case "1":
                        input_num = 1;
                        break;
                    case "2":
                        input_num = 2;
                        break;
                    case "3":
                        input_num = 3;
                        break;
                    case "4":
                        input_num = 4;
                        break;
                    case "5":
                        input_num = 5;
                        break;
                    case "6":
                        input_num = 6;
                        break;
                    case "7":
                        input_num = 7;
                        break;
                    case "8":
                        input_num = 8;
                        break;
                    case "9":
                        input_num = 9;
                        break;
                    case "10":
                        input_num = 10;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        messages = new HashMap<Double, Double>();

        _pairDialogIsON = false;
        start = 1; // the first read of a package
        current_length = 0; // indicate how much reads were received
        total = 4000; // total of reads
        length = 360; // quantity of reads in one package

        TableRow tr_head = new TableRow(History_Log_2.this);
        for (String col : columns) {
            dataFile += col + ", ";
        }
        dataFile += "\n ";

        MeganetInstances.getInstance().GetMeganetEngine().InitReadMeter(this);
        MeganetInstances.getInstance().GetMeganetEngine().InitProgramming(this, MeganetInstances.getInstance().GetMeganetDb().getSetting(1).GetKeyValue());

        promptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeganetInstances.getInstance().GetMeganetEngine().Prompt(MeganetEngine.ePromptType.TEN_CHR_PAIRING, PromptConvert("E"));
                Toast.makeText(getApplicationContext(), "ממתין לתגובת יחידת הקצה", Toast.LENGTH_LONG).show();
                CommonSettingsData data = new CommonSettingsData(6, "last_programm_prompt_type", PromptConvert("E"));
                MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);

            }
        });
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

    private void PairingDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Alert dialog for connecting to device
        builder.setMessage("האם להתחבר עם יחידת קצה: " + MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + " ?")
                .setCancelable(false)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "היחידה התחברה",
                        Toast.LENGTH_SHORT).show();

                        promptBtn.setVisibility(View.INVISIBLE);
                        tv1.setText("מחובר ליחידת קצה: " + MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress());
                        int ndevice = Integer.decode("0x" +  MeganetInstances.getInstance().GetMeganetEngine().GetNdevice())-1;
                        if(ndevice == 45) {
                            getLogBtn.setVisibility(View.VISIBLE);
                            tv16.setVisibility(View.VISIBLE);
                            //input.setVisibility(View.VISIBLE);
                            inputSpinner.setVisibility(View.VISIBLE);
                            msgSize = 4;
                        }
                        else {
                            getLogBtn.setVisibility(View.INVISIBLE);
                            tv16.setVisibility(View.INVISIBLE);
                            //input.setVisibility(View.INVISIBLE);
                            inputSpinner.setVisibility(View.INVISIBLE);
                            pb.setVisibility(View.VISIBLE);
                            msgSize = 5;
                            input_num = 0;
                            MeganetInstances.getInstance().GetMeganetEngine().TimeRequest(); // send time request
                        }

                        getLogBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getLogBtn.setVisibility(View.INVISIBLE);
                                tv16.setVisibility(View.INVISIBLE);
                                //input.setVisibility(View.INVISIBLE);
                                inputSpinner.setVisibility(View.INVISIBLE);
                                pb.setVisibility(View.VISIBLE);
                                //MeganetInstances.getInstance().GetMeganetEngine().GetMeterSN(input_num);
                                MeganetInstances.getInstance().GetMeganetEngine().TimeRequest(); // send time request
                            }
                        });
                        dialog.dismiss();
                        //_pairDialogIsON = false;
                    }
                })
                .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // some code if you want
                        Toast.makeText(getApplicationContext(), "ההתחברות ליחידה התבטלה", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        _pairDialogIsON = false;
                        MeganetInstances.getInstance().GetMeganetEngine().Disconnect();
                        finish();
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

    public void checkButton(View v) // check which input number of device
    {
        int radioID = input.getCheckedRadioButtonId();
        if(radioID==R.id.radioButton1)
            input_num = 0;
        else
            input_num = 1;
    }

    @Override
    public void SetReadData(Map<String, QryParams> data_prm) {
    }

    @Override
    public void ReadData(byte[] dataArr_prm) { //
        int len, num_of_msgs;
        double first_index, read;
        byte[] msg;

        if(dataArr_prm == null) { // If the device connection was disconnected
            synchronized (nullRunnable){
                this.runOnUiThread(nullRunnable);
                try {
                    nullRunnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            while (dataArr_prm.length > 0) { // If there is data in the package
                len = dataArr_prm[1];
                msg = new byte[len + 2];

                msg = Arrays.copyOfRange(dataArr_prm, 0, len + 2);
                dataArr_prm = Arrays.copyOfRange(dataArr_prm, len + 2, dataArr_prm.length);

                if(len == 6)
                {
                    if(msg[7] == 9) {
                        total = (int) curr_index;
                        break;
                    }
                }
                else {
                    first_index = ConvertByteToNumber(Arrays.copyOfRange(msg, 7, 9)); // first index indicate the number of the first read in a message
                    curr_index = first_index-1;

                    num_of_msgs = (len - 7) / msgSize;

                    for (int i = 0; i < num_of_msgs; i++) {
                        read = ConvertByteToNumber(Arrays.copyOfRange(msg, 9 + msgSize * i, 9 + msgSize * (i+1)));
                        messages.put(first_index + i, read);
                        curr_index++;
                    }
                    msg = null;
                }
            }
            current_length = messages.size();
            System.out.println("Current length is: " + current_length);
            MeganetInstances.getInstance().GetMeganetEngine().reset_timerCount();

            if (current_length == total) { // If we received all reads we need to upload the history log to FTP
                byte[] tamper_msg = {-1, -1, -1, -1, -1};
                double tamper = ConvertByteToNumber(tamper_msg);
                double id, message_prm;
                List<Double> messagesByKey = new ArrayList<Double>(messages.keySet());
                toast.cancel();

                Collections.sort(messagesByKey);

                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String _date;
                int hoursToSubtract;
                Calendar curr_read;

                for (double key : messagesByKey) {
                    curr_read = (Calendar) last_read.clone();
                    id = key;
                    message_prm = messages.get(key);

                    //Insert Id
                    dataFile += id + ", ";

                    //Insert Hour
                    if(msgSize == 5) {
                        hoursToSubtract = (int) (id-1);
                        curr_read.add(Calendar.HOUR, -hoursToSubtract);
                    }
                    else {
                        hoursToSubtract = (int) ((id-1)*15);
                        curr_read.add(Calendar.MINUTE, -hoursToSubtract);
                    }

                    _date = format.format(curr_read.getTime());
                    dataFile += _date + ", ";

                    //Insert Message
                    if (message_prm == tamper) {
                        dataFile += "TAMPER" + ", ";
                    } else {
                        dataFile += message_prm + ", ";
                    }

                    //Insert Address
                    dataFile += MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + ", ";
                    dataFile += "\n ";
                }

                SimpleDateFormat formatter = new SimpleDateFormat("MMddyy_HHmmss");
                Date now_date = new Date(System.currentTimeMillis());
                new_fileName = MeganetInstances.getInstance().GetMeganetEngine().GetUnitAddress() + "_" + formatter.format(now_date) + ".csv";
                MeganetInstances.getInstance().GetMeganetEngine().Disconnect();

                if(isExternalStorageWritable() && checkPermission()) {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/saved_logs");
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    File file = new File(myDir, new_fileName);
                    if (file.exists())
                        file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        out.write(dataFile.getBytes());
                        out.close();
                        Toast.makeText(getApplicationContext(), "הקובץ נשמר בתיקיית saved_logs באחסון החיצוני" , Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "שמירת קובץ נכשלה!",Toast.LENGTH_LONG).show();
                }
                finish();
                /*
                //added history_log_3 layout and activity with button upload to ftp after muller reject
                Intent intent = new Intent(History_Log_2.this, History_Log_3.class);
                intent.putExtra("data file", dataFile);
                startActivity(intent);
                */
            }

            this.runOnUiThread(getLogRunnable);
        }
    }

    public boolean checkPermission(){
        //return(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED);
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(getApplicationContext(), "לא ניתן להמשיך בפעולה! \n יש להכנס להגדרות האפליקציה ולאפשר גישה לקבצים.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Log.i("State", "Yes, it is permitted!");
            return true; // Permission has already been granted
        }
    }

    private boolean isExternalStorageWritable(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Log.i("State", "Yes, it is writable!");
            return true;
        }   else {
            return false;
        }
    }


    public void send_requests() {
        int len;
        List<Double> messagesByKey = new ArrayList<Double>(messages.keySet());
        Collections.sort(messagesByKey);

        if (current_length < total) {
            int index = 1;
            for (Double key : messagesByKey) {
                if (index < key) {
                    len = (int) (key - index);
                    if (len > 5)
                        len = 5;
                    MeganetInstances.getInstance().GetMeganetEngine().GetLog(input_num, index, len, true);
                    break;
                } else if (index == 3995) {
                    len = 5;
                    MeganetInstances.getInstance().GetMeganetEngine().GetLog(input_num, index, len, true);
                    break;
                } else
                    index++;
            }
        }
    }

    @Override
    public void GetTime(byte[] dataArr_prm) { // Function that update the time of the last read that received and then send log request

        if( MeganetInstances.getInstance().GetMeganetEngine().get_timerCount() >= 5)// If we didn't success to receive the last read after 5 attempts, we disconnect.
        {
            MeganetInstances.getInstance().GetMeganetEngine().reset_timerCount();
            Toast.makeText(getApplicationContext(), "החיבור אבד!", Toast.LENGTH_SHORT).show();
            MeganetInstances.getInstance().GetMeganetEngine().Disconnect();
            finish();
        }
        else {
            MeganetInstances.getInstance().GetMeganetEngine().reset_timerCount();
            int seconds =  (int)ConvertByteToNumber(Arrays.copyOfRange(dataArr_prm, 7, 9)); // get the number of second that pass from the last read
            last_read.add(Calendar.SECOND, -seconds); // calculate the date of the last read
            Toast.makeText(getApplicationContext(), "מקבל...", Toast.LENGTH_SHORT).show();
            MeganetInstances.getInstance().GetMeganetEngine().GetLog(input_num, start, length, false); // send get log
        }
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

    public boolean PairData(String deviceName_prm, String ndevice_pam, boolean titleOnly)
    {
        //deviceTextView.setText(deviceName_prm.substring(0, deviceName_prm.length()-3));

        if (titleOnly)
            return true;

        if(!_pairDialogIsON)
        {
            _pairDialogIsON = true;
            PairingDialog();
        }

        return true;
    }

    @Override
    public void OnParameters(String deviceName_prm, List<QryParams> parameters) {

    }

    @Override
    public void OnRead(String serial_num, String reading) { // Function that update the MeterSN and send time request

        if( MeganetInstances.getInstance().GetMeganetEngine().get_timerCount() >= 5) // If we didn't success to update the MeterSN after 5 attempts, we disconnect.
        {
            MeganetInstances.getInstance().GetMeganetEngine().reset_timerCount();
            MeganetInstances.getInstance().GetMeganetEngine().Disconnect();
            Toast.makeText(getApplicationContext(), "החיבור אבד!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            MeganetInstances.getInstance().GetMeganetEngine().reset_timerCount();
            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccSN(serial_num);
            MeganetInstances.getInstance().GetMeganetEngine().SetLastAccRead(reading);
            MeganetInstances.getInstance().GetMeganetEngine().TimeRequest(); // send time request
        }
    }

    @Override
    public void OnProgramm(boolean result_prm, String err_prm) {

    }

    @Override
    public void OnPowerOff(boolean result_prm, String err_prm) {

    }

    @Override
    public void OnSleep(boolean result_prm, String err_prm) {

    }

    @Override
    public void OnErrorCb(String error_prm) {

    }

    @Override
    public void OnMessageCb(String message_prm) {

    }

    @Override
    public void OnReadMeters(byte[] data_prm) {

    }

    @Override
    public void OnFilterSet(boolean status) {

    }

    public class uploadFile extends AsyncTask<Void, Void, Void> {
        Boolean connect = true;

        @Override
        protected Void doInBackground(Void... voids) {

            //getting shared preferences that saved the ftp details
            SharedPreferences sp = getSharedPreferences("ftp", MODE_PRIVATE);

            String host = sp.getString("host", "host");
            String userName = sp.getString("username", "username");
            String password = sp.getString("password", "password");
            int port = sp.getInt("port", (int) 2l);

            try { // try to connect to FTP and open the History Log folder. It not exist - create this folder
                mFTPClient = new FTPClient();
                mFTPClient.connect(host,port);

                if (mFTPClient.login(userName, password)) {
                    mFTPClient.enterLocalPassiveMode(); // important!
                    mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                    mFTPClient.changeWorkingDirectory("/History Log");

                    int returnCode = mFTPClient.getReplyCode();
                    if (returnCode == 550) {
                        mFTPClient.makeDirectory("History Log");
                    }

                    try { // try to open new file and write data in it with output streamer
                        FileOutputStream fos = openFileOutput(new_fileName, MODE_PRIVATE);
                        OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
                        outputWriter.write(dataFile);
                        outputWriter.close();

                        StringBuffer sb = new StringBuffer( "ftp://" );
                        sb.append( userName );
                        sb.append( ':' );
                        sb.append( password );
                        sb.append( '@' );
                        sb.append( host );
                        sb.append( "/History Log/" );
                        sb.append( new_fileName );
                        //sb.append( ";type=i" );

                        BufferedInputStream bis = null;
                        BufferedOutputStream bos = null;

                        try
                        {
                            URL url = new URL( sb.toString() );
                            URLConnection urlc = url.openConnection();
                            urlc.setDoOutput(true);
                            bos = new BufferedOutputStream( urlc.getOutputStream() );
                            bis = new BufferedInputStream( openFileInput(new_fileName) );

                            int i;
                            // read byte by byte until end of stream
                            while ((i = bis.read()) != -1)
                            {
                                if(i==10)
                                {
                                    bos.write('\r');
                                    bos.write('\n');
                                    bos.flush();
                                }
                                else
                                    bos.write( i );
                            }
                        }
                        finally
                        {
                            if (bis != null)
                                try {
                                    bis.close();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                            if (bos != null)
                                try {
                                    bos.close();
                                } catch (IOException ioe) {
                                    ioe.printStackTrace();
                                }
                        }
                        connect = true;
                        mFTPClient.logout();
                        mFTPClient.disconnect();

                    } catch (IOException e) {
                        connect = false;
                        Log.e("Exception", "File write failed: " + e.toString());
                    }
                }
                else
                    connect = false;
            } catch (Exception e) {
                connect = false;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!connect) { // If there is a problem with the writing file to FTP or with the connection to FTP
                AlertDialog.Builder dialog = new AlertDialog.Builder(History_Log_2.this);
                dialog.setTitle("Problem with FTP connection!");
                dialog.setCancelable(true);
                dialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            }
            else {
                Toast.makeText(getApplicationContext(), "Uploaded to FTP!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(History_Log_2.this, History_Log_4.class);
                startActivity(intent);
            }
        }
    }
}