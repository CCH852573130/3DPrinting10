package com.mukesh.drawingview.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

//主界面跳转到四个分界面
public class ZhuJieMian extends AppCompatActivity implements View.OnClickListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_zhujiemian);
    //初始化方法
    initUI();
  }
  private void initUI(){
    findViewById(R.id.button1).setOnClickListener(this);
    findViewById(R.id.button2).setOnClickListener(this);
    findViewById(R.id.button3).setOnClickListener(this);
    findViewById(R.id.button4).setOnClickListener(this);
  }
//四个按钮分别实现四个分界面的跳转
  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.button1:
        Intent intent1 = new Intent();
        intent1.setClass(getApplicationContext(), JianBiHua.class);
        startActivity(intent1);
        break;
      case R.id.button3:
        Intent intent2 = new Intent();
        intent2.setClass(getApplicationContext(), MoXingKu.class);
        startActivity(intent2);
        break;
      case R.id.button2:
        Intent intent3 = new Intent();
        intent3.setClass(getApplicationContext(), ZhuanYeMoShi.class);
        startActivity(intent3);
        break;
      case R.id.button4:
        Intent intent4 = new Intent();
        intent4.setClass(getApplicationContext(), SheZhi.class);
        startActivity(intent4);
        break;
    }
  }
}
