package com.mukesh.drawingview.example;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import static com.mukesh.drawingview.example.ZhuJieMian.open;

//机器设置以及切片设置（温度去除） WiFi去除

public class SheZhi extends AppCompatActivity {
private Spinner mspinner1,mspinner2,mspinner3,mspinner4;
private Button mbtn1;
private String nozzle_save,temperature_save,height_save,speed_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shezhi);
        mspinner1 = findViewById(R.id.spinner1);
        mspinner2 = findViewById(R.id.spinner2);
        mspinner3 = findViewById(R.id.spinner3);
        mspinner4 = findViewById(R.id.spinner4);
        mbtn1 = findViewById(R.id.btn_save);
        mbtn1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMyPermissions();//sd卡读写动态请求
                String result = getJson("fdmprinter.def.json");//读取SD卡中的json
                try {//解析json并修改，然后把json转换为字符串并写成文件

                    JSONObject json = new JSONObject(result);
                    String nozzle = nozzle_save;
                    String temperature = temperature_save;
                    String height = height_save;
                    String speed = speed_save;
                    JSONObject user = json.getJSONObject("settings");
                    JSONObject obj_nozzle = user.getJSONObject("machine_settings")
                            .getJSONObject("children").getJSONObject("machine_nozzle_size");
                    JSONObject obj_temperature = user.getJSONObject("material")
                            .getJSONObject("children").getJSONObject("material_print_temperature");
                    JSONObject obj_height = user.getJSONObject("resolution")
                            .getJSONObject("children").getJSONObject("layer_height");
                    JSONObject obj_speed = user.getJSONObject("speed")
                            .getJSONObject("children").getJSONObject("speed_print");
                    obj_nozzle.put("default_value",nozzle );
                    obj_temperature.put("default_value",temperature );
                    obj_height.put("default_value",height );
                    obj_speed.put("default_value",speed );
//                    json.put("name",obj1 );
//                    String name = json.getString("name");
//                    Toast.makeText( getApplicationContext(), name,Toast.LENGTH_LONG ).show();
                    String data = json.toString();
                    File file = new File(Environment.getExternalStorageDirectory() + "/test", "fdmprinter.def.json");
                    OutputStream out = new FileOutputStream(file);
                    out.write(data.getBytes());//字符串写入
                    out.close();
                    Toast.makeText( getApplicationContext(),"修改成功",Toast.LENGTH_LONG ).show();
//            for(int i=0;i<json.length();i++)
//            {
//                JSONObject jb=json.getJSONObject(i);
//                Log.d("AAA", jb.getString("name"));
//                Log.d("AAA",String.valueOf(json.length()));
//            }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        } );
        mspinner1.setOnItemSelectedListener(  new AdapterView.OnItemSelectedListener() {
            @Override
            public void  onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String[] nozzle = getResources().getStringArray(R.array.nozzle);
                nozzle_save = nozzle[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );
        mspinner2.setOnItemSelectedListener(  new AdapterView.OnItemSelectedListener() {
            @Override
            public void  onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String[] temperature = getResources().getStringArray(R.array.temperature);
                temperature_save = temperature[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );
        mspinner3.setOnItemSelectedListener(  new AdapterView.OnItemSelectedListener() {
            @Override
            public void  onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String[] height = getResources().getStringArray(R.array.height);
                height_save = height[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );
        mspinner4.setOnItemSelectedListener(  new AdapterView.OnItemSelectedListener() {
            @Override
            public void  onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String[] speed = getResources().getStringArray(R.array.speed);
                speed_save = speed[pos];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        } );

    }
    public String getJson(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileReader myfile = new FileReader( Environment.getExternalStorageDirectory() + "/test/"+fileName);
//            AssetManager assetManager = SaveActivity.this.getAssets();
            BufferedReader bf = new BufferedReader(myfile);
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
                Log.d("AAA", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(SheZhi.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("bbb", "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(SheZhi.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("bbb", "requestMyPermissions: 有读SD权限");
        }
    }
}
