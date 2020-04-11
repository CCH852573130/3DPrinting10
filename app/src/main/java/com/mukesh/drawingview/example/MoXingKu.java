package com.mukesh.drawingview.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//显示在本地sd卡上储存在某个目录下的图片，每个图片有自己的复选框，选中之后点打印按钮 打印按钮含读取与图片对应的gcode文件功能

public class MoXingKu extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moxingku);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.imageView2).setOnClickListener(this);
        findViewById(R.id.up_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageView2:
                Intent intent7 = new Intent();
                intent7.setClass(getApplicationContext(), DaYinJieMian.class);
                startActivity(intent7);
                break;
            case R.id.up_button:
                Intent intent8 = new Intent();
                intent8.setClass(getApplicationContext(), UPicture.class);
                startActivity(intent8);
                break;
        }
    }
}