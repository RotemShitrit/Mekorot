package com.kp.meganet.meganetkp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ChooseRegisterActivity extends AppCompatActivity {

    private Button wer,ber1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registers);

        wer = (Button) findViewById(R.id.button_werUz1);
        ber1 = (Button) findViewById(R.id.button_ber1);

        ber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(44);
                intent = new Intent(ChooseRegisterActivity.this, Ber1Activity.class);
                startActivity(intent);
            }
        });

        wer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentReadType(MeganetEngine.eReadType.NONE);
                MeganetInstances.getInstance().GetMeganetEngine().SetCurrentProgrammType(7);
                intent = new Intent(ChooseRegisterActivity.this, ProgrammActivity.class);
                startActivity(intent);
            }
        });
    }
}