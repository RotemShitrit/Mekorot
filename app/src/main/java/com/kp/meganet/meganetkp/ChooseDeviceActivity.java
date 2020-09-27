package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class ChooseDeviceActivity extends AppCompatActivity {
    private Button pitBtn;
    private Button wallBtn;
    private Button werBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pitBtn = (Button) findViewById(R.id.pit_device);
        wallBtn = (Button) findViewById(R.id.wall_device);
        werBtn = (Button) findViewById(R.id.registers);

        pitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ChooseDeviceActivity.this, PitActivity.class);
                startActivity(intent);
            }
        });

        wallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ChooseDeviceActivity.this, WallMountActivity.class);
                startActivity(intent);
            }
        });

        werBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                intent = new Intent(ChooseDeviceActivity.this, ChooseRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
