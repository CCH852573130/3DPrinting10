package com.mukesh.drawingview.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//要做读取U盘路径 并实现U盘文件（stl）的显示 选中文件后能点击按钮进行上传 布局界面为activity_upicture
public class UPicture extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upicture);
    }
}
