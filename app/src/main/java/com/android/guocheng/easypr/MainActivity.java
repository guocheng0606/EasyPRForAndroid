package com.android.guocheng.easypr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.fosung.libeasypr.view.EasyPRPreSurfaceView;
import com.fosung.libeasypr.view.EasyPRPreView;

public class MainActivity extends AppCompatActivity {

    private EasyPRPreView easyPRPreView;
    private Button btnShutter;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        easyPRPreView = (EasyPRPreView) findViewById(R.id.preSurfaceView);
        btnShutter = (Button) findViewById(R.id.btnShutter);
        text = (TextView) findViewById(R.id.text);
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (easyPRPreView != null) {
            easyPRPreView.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (easyPRPreView != null) {
            easyPRPreView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (easyPRPreView != null) {
            easyPRPreView.onDestroy();
        }
    }

    private void initListener() {
        easyPRPreView.setRecognizedListener(new EasyPRPreSurfaceView.OnRecognizedListener() {
            @Override
            public void onRecognized(String result) {
                if (result == null || result.equals("0")) {
                    Toast.makeText(MainActivity.this, "换个姿势试试!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "识别成功", Toast.LENGTH_SHORT).show();
                    text.setText(result);
                }
            }
        });
        btnShutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                easyPRPreView.recognize();//开始识别
            }
        });
    }
}
