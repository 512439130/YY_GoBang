package com.yy.wuziqi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button bt_ReStart;
    private Button bt_Return;
    private GobangPanel gobangPanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
    }


    private void initViews() {
        bt_ReStart = (Button) findViewById(R.id.id_bt_restart);
        bt_Return = (Button) findViewById(R.id.id_bt_return);
        gobangPanel = (GobangPanel) findViewById(R.id.id_gobang);

    }
    private void initEvents() {
        bt_ReStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重玩
                gobangPanel.reStart();
            }
        });
        bt_Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //悔棋
                gobangPanel.Return();
            }
        });
    }


}
