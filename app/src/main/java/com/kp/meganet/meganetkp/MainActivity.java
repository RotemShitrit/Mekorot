package com.kp.meganet.meganetkp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int READ_WRITE_PERMISSION = 1 ;
    private BluetoothAdapter myBluetoothAdapter;

    private Map<String, String> pairsList;
    private String _lastPairDevice;

    private TextView statusTextView;
    private Spinner pairsSpinner;
    private Switch bluetoothSwitch;

    private Button pairButtorn;
    private Button exitButton;

    private boolean btSupport;
    private boolean isPaired;

    private Toast toast;
    private long lastBackPressTime = 0;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_title);

        btSupport = false;
        isPaired = false;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, READ_WRITE_PERMISSION);
            //toast.makeText(getApplicationContext(), "לא ניתן להמשיך בפעולה! \n יש להכנס להגדרות האפליקציה ולאפשר גישה לקבצים.", Toast.LENGTH_SHORT).show();
        }


        exitButton = (Button) findViewById(R.id.buttonCloseApp);
        pairButtorn = (Button) findViewById(R.id.buttonPair);

        statusTextView = (TextView) findViewById(R.id.textViewStatus);
        bluetoothSwitch = (Switch) findViewById(R.id.switch1);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(myBluetoothAdapter == null)
        {
            statusTextView.setText("סטטוס: בלוטוס אינו נתמך");

            Toast.makeText(getApplicationContext(), "המכשיר שלך אינו תומך בבלוטוס",
                    Toast.LENGTH_LONG).show();

            bluetoothSwitch.setEnabled(false);
            pairButtorn.setEnabled(false);
        }
        else
        {
            btSupport = true;
            MeganetInstances.getInstance().SetMeganetDb(new MeganetDB(this));

            MeganetInstances.getInstance().SetMeganetEngine(new MeganetEngine(myBluetoothAdapter));



            InitStartApp();

            bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        MeganetInstances.getInstance().GetMeganetEngine().On();
                    } else {
                        MeganetInstances.getInstance().GetMeganetEngine().Off();
                        isPaired = false;
                    }

                    InitStartApp();
                }

            });

            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExitApplication();

                }
            });

            ///////////////////////////////////////////////////////////////////////////////////////////////////
            pairButtorn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String macAddr;

                    if (MeganetInstances.getInstance().GetMeganetEngine().GetStatus() == BTengine.btMode.ON) {
                        macAddr = pairsList.get(pairsSpinner.getSelectedItem().toString());
                        if (Connect(v, macAddr)) {

                            Toast.makeText(getApplicationContext(), "מחובר ל: " + pairsSpinner.getSelectedItem().toString(),
                                    Toast.LENGTH_SHORT).show();
                            statusTextView.setText("מחובר ל: " + pairsSpinner.getSelectedItem().toString());

                            CommonSettingsData data = new CommonSettingsData(5, "last_bluetooth_pair", pairsSpinner.getSelectedItem().toString());

                            MeganetInstances.getInstance().GetMeganetDb().updateProperty(data);
                            isPaired = true;
                            pairButtorn.setEnabled(false);
                        } else {

                            Toast.makeText(getApplicationContext(), "חיבור נכשל ל: " + pairsSpinner.getSelectedItem().toString(),
                                    Toast.LENGTH_SHORT).show();
                            statusTextView.setText("חיבור נכשל ל: " + pairsSpinner.getSelectedItem().toString());
                            isPaired = false;

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "בלוטוס אינו מופעל",
                                Toast.LENGTH_SHORT).show();
                        statusTextView.setText("בלוטוס כבוי");
                        isPaired = false;
                    }
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_WRITE_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                    dlgAlert.setMessage("לא ניתן להמשיך בפעולה ללא גישה לאחסון המכשיר!");
                    dlgAlert.setTitle("שגיאה!");
                    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
                toast = Toast.makeText(this, "לחץ שוב על מנת לצאת מהאפליקציה", Toast.LENGTH_LONG);
                toast.show();
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement - getResources().getString(R.string.title_activity)
        if (id == R.id.action_about) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.app_name) + " גירסא " + getResources().getString(R.string.app_version),
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        int a = 0;

        if (id == R.id.nav_rssi) {
            OpenNewActivity(4);
        } else if(id == R.id.nav_ftp) {
            OpenNewActivity(9);
        } else {
            if(!btSupport)
            {
                Toast.makeText(getApplicationContext(), "המכשיר שלך אינו תומך בבלוטוס",
                        Toast.LENGTH_LONG).show();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return false;
            }
            if (!isPaired)
            {
                Toast.makeText(getApplicationContext(), "יש להתחבר ליחידת RSINT כדי שתוכל להמשיך",
                        Toast.LENGTH_LONG).show();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return false;
            }

            Intent intent;
/*
            if (id == R.id.nav_field_verif_1) {
                OpenNewActivity(1);
            } else if (id == R.id.nav_read_meter) {
                OpenNewActivity(3);
            } else if (id == R.id.nav_programm) {
                OpenNewActivity(5);
            } else*/
            if (id == R.id.nav_settings) {
                OpenNewActivity(6);
            }
/*
            else if (id == R.id.nav_rdm) {
                OpenNewActivity(7);
            }
*/
            else if (id == R.id.nav_log) {
                OpenNewActivity(10);
            } else if (id == R.id.nav_consumption) {
                OpenNewActivity(11);
            }
            //else if (id == R.id.nav_orders) {
            //  OpenNewActivity(8);}*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void InitStartApp()
    {
        MeganetInstances.getInstance().SetMeganetEngine(new MeganetEngine(myBluetoothAdapter));

        bluetoothSwitch.setEnabled(true);
        MeganetInstances.getInstance().GetMeganetEngine().MeganetInit();
        MeganetInstances.getInstance().GetMeganetEngine().InitFrequency(MeganetInstances.getInstance().GetMeganetDb().getSetting(2).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(3).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(4).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(9).GetKeyValue(), MeganetInstances.getInstance().GetMeganetDb().getSetting(8).GetKeyValue());

        if(MeganetInstances.getInstance().GetMeganetEngine().GetStatus() == BTengine.btMode.ON)
        {
            statusTextView.setText("סטטוס: הבלוטוס דולק");

            pairsList = MeganetInstances.getInstance().GetMeganetEngine().GetDeviceList();

            // Pairs list
            String[] array_spinner = pairsList.keySet().toArray(new String[0]);
            pairsSpinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(this,
                    android.R.layout.simple_spinner_item, array_spinner);
            pairsSpinner.setAdapter(adapter);

            _lastPairDevice = "";
            _lastPairDevice = MeganetInstances.getInstance().GetMeganetDb().getSetting(5).GetKeyValue();
            if(_lastPairDevice.length() > 0)
                pairsSpinner.setSelection(((ArrayAdapter<String>) pairsSpinner.getAdapter()).getPosition(_lastPairDevice));
            pairButtorn.setEnabled(true);
            bluetoothSwitch.setChecked(true);
        }
        else
        {
            pairButtorn.setEnabled(false);

            statusTextView.setText("סטטוס: Bluetooth כבוי, נא להפעיל!");
            if(pairsSpinner != null)
                pairsSpinner.setAdapter(null);
            bluetoothSwitch.setChecked(false);
        }
    }

    public boolean Connect(View v, String address_prm) {
        Toast.makeText(getApplicationContext(), "מתחבר...",
                Toast.LENGTH_SHORT).show();

        boolean result = MeganetInstances.getInstance().GetMeganetEngine().ConnectTo(address_prm);
        if(result)
            MeganetInstances.getInstance().GetMeganetEngine().RSNTVersionRequest();

        return  result;

    }

    public void OpenNewActivity(int activityID_prm)
    {
        Intent intent;
        switch(activityID_prm)
        {

            case 1:
                Toast.makeText(getApplicationContext(), "Field Verification", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_1);
                intent = new Intent(MainActivity.this, ReadsActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "Field Verification 2", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.FIELD_VERIF_2);
                intent = new Intent(MainActivity.this, ReadsActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 3:
                Toast.makeText(getApplicationContext(), "Read Meter", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.READ_METER);
                intent = new Intent(MainActivity.this, ReadsActivity.class);
                startActivity(intent);
                // TODO Something
                break;

            case 4:
                Toast.makeText(getApplicationContext(), "RANMAN RSSI", Toast.LENGTH_LONG).show();

                String url = MeganetInstances.getInstance().GetMeganetDb().getSetting(7).GetKeyValue();//"http://www.google.com";
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.RDM);
                //intent = new Intent(MainActivity.this, ReadsActivity.class);
                //startActivity(intent);
                // TODO Something
                break;

            case 5:
                Toast.makeText(getApplicationContext(), "Programming", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(MainActivity.this, ChooseDeviceActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 6:
                Toast.makeText(getApplicationContext(), "הגדרות", Toast.LENGTH_LONG).show();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                // TODO Something
                break;
            case 7:
                Toast.makeText(getApplicationContext(), "RDM Control", Toast.LENGTH_LONG).show();
                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(MainActivity.this, RDM_Controll.class);
                startActivity(intent);
                // TODO Something
                break;

            case 8: // If there is no internet, open WorkOrderSelect_2 (a message to connect internet)
                ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
                if (cm.getActiveNetworkInfo() != null) {
                    intent = new Intent(MainActivity.this, WorkOrderSelect_1.class);
                }
                else {
                    intent = new Intent(MainActivity.this, WorkOrderSelect_2.class);
                }
                Toast.makeText(getApplicationContext(), "Work Order", Toast.LENGTH_LONG).show();
                //MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                startActivity(intent);
                // TODO Something
                break;

            case 9:
                Toast.makeText(getApplicationContext(), "הגדרות FTP", Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, FTP_Controll.class);
                startActivity(intent);
                // TODO Something
                break;

            case 10:
                Toast.makeText(getApplicationContext(), "יומן היסטוריה", Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, History_Log_1.class);
                startActivity(intent);
                // TODO Something
                break;

            case 11:
                Toast.makeText(getApplicationContext(), "קצב זרימה", Toast.LENGTH_LONG).show();
                intent = new Intent(MainActivity.this, ConsumptionActivity.class);
                startActivity(intent);
                // TODO Something
                break;
        }
    }

    private void ExitApplication()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("לסגור את האפליקציה?")
                .setCancelable(false)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "סוגר אפליקציה",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        moveTaskToBack(true);
						finishAndRemoveTask(); // remove task from recent tasks on phone								
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                    }
                })
                .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // some code if you want
                        dialog.dismiss();
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
}
