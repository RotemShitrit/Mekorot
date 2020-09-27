package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WallMountActivity extends AppCompatActivity {

    private Button mt1w;
    private Button mt2w8;
    private Button mt2w10;
    private Button mtwp1;
    private Button mtwp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_mount);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mt1w = (Button) findViewById(R.id.button_mt1w);
        mt2w8 = (Button) findViewById(R.id.button_mt2w8);
        mt2w10 = (Button) findViewById(R.id.button_mt2w10);
        mtwp1 = (Button) findViewById(R.id.button_mtwp1);
        mtwp = (Button) findViewById(R.id.button_mtwp);

        mt1w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(3);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        mt2w8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(4);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        mt2w10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        mtwp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(5);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });

        mtwp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                //super.onBackPressed();

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);

                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(6);

                intent = new Intent(WallMountActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });
    }
}
