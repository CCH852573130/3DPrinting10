package com.mukesh.drawingview.example;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import usbcontroler.USBDiskState;


//要做读取U盘路径 并实现U盘文件（stl）的显示 选中文件后能点击按钮进行上传 布局界面为activity_upicture
public class UPicture extends AppCompatActivity {
    private List<HashMap<String, Object>> myBeanList; //用来存放数据的数组
    public ListView listView;
    public List<String> listStr;
    private CheckBoxAdapter cbAdapter;
    private TextView tvSelected;
    public String path_picture;
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upicture);
        boolean b = new USBDiskState().isMounted();
        Log.e("test", "onCreate: ------U盘是否存在-------" + b);
        if (b) {
            Toast.makeText(this, "USB设备存在", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "USB设备不存在", Toast.LENGTH_SHORT).show();
        }
        listView = (ListView) findViewById(R.id.listview);//在视图中找到ListView
        myBeanList = new ArrayList<HashMap<String, Object>>();
        listStr = new ArrayList<String>();
        tvSelected = (TextView) findViewById(R.id.tvselected);
        File path = new File("/storage/usbhost1/");//外置U盘路径
        File[] files = path.listFiles();// 读取
        getFileName(files);
//        SimpleAdapter adapter =new SimpleAdapter(this,myBeanList,R.layout.newusb,new String[]{"Name"},new int[] {R.id.headtext});
//        listView.setAdapter(adapter);
        for (int p = 0; p < myBeanList.size(); p++) {
            Log.e("test", "list.name" + myBeanList.get(p));
        }
        cbAdapter = new CheckBoxAdapter(this, myBeanList);
        listView.setAdapter(cbAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CheckBoxAdapter.ViewCache viewCache = (CheckBoxAdapter.ViewCache) view.getTag();
                viewCache.cb.toggle();
                myBeanList.get(position).put("boolean", viewCache.cb.isChecked());
                cbAdapter.notifyDataSetChanged();
                if (viewCache.cb.isChecked()) {//被选中状态
                    listStr.add(myBeanList.get(position).get("filepath").toString());
                } else//从选中状态转化为未选中
                {
                    listStr.remove(myBeanList.get(position).get("filepath").toString());
                }
                tvSelected.setText("已选择了:" + listStr.size() + "项");
            }
        });
    }

    class CheckBoxAdapter extends BaseAdapter {

        private Context context;
        private List<HashMap<String, Object>> list;
        private LayoutInflater layoutInflater;
        private TextView tv;
        private CheckBox cb;

        public CheckBoxAdapter(Context context, List<HashMap<String, Object>> list) {
            this.context = context;
            this.list = list;//list中checkbox状态为false
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.newusb, null);

                ViewCache viewCache = new ViewCache();
                tv = (TextView) convertView.findViewById(R.id.headtext);
                cb = (CheckBox) convertView.findViewById(R.id.cb);

                viewCache.tv = tv;
                viewCache.cb = cb;
                convertView.setTag(viewCache);
            } else {
                ViewCache viewCache = (ViewCache) convertView.getTag();
                tv = viewCache.tv;
                cb = viewCache.cb;
            }

            tv.setText(list.get(position).get("Name") + "");
            cb.setChecked((Boolean) list.get(position).get("boolean"));
            return convertView;
        }

        public class ViewCache {
            TextView tv;
            CheckBox cb;
        }
    }

    public void printing(View view) {
        if (listStr.size() > 1) {
            Toast.makeText(this, "您选择的stl文件过多", Toast.LENGTH_SHORT).show();
        } else if (listStr.size() == 0) {
            Toast.makeText(this, "请选择且只选择一个stl文件", Toast.LENGTH_SHORT).show();
        } else {
            String File_Path_stl = listStr.toString().substring(1,listStr.toString().lastIndexOf("]"));//这里可以获取到文件的路径
            File file = new File(Environment.getExternalStorageDirectory() +"/usbtest.STL");
            String abc = Environment.getExternalStorageDirectory() +"/usbtest.STL";
            copy(abc,File_Path_stl);
            String File_Path_gcode = abc.substring(0, abc.lastIndexOf(".")) + ".gcode";
            String fileName =abc.substring(abc.lastIndexOf("/")+1);
            stringFromJNI3(abc,File_Path_gcode);
            Toast.makeText(this, fileName+"切片成功", Toast.LENGTH_SHORT).show();
            Intent intent8 = new Intent();
            intent8.setClass(getApplicationContext(), DaYinJieMian.class);
            Bundle mBundle = new Bundle();
            mBundle.putString("Data", File_Path_gcode);//压入数据
            intent8.putExtras(mBundle);
            startActivity(intent8);
            System.exit(0);
        }
    }
    public native String stringFromJNI3(String stl_path,String gcode_path);


    private void getFileName(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            String fileName = null;
            for (File file : files) {
                if (file.isDirectory()) {
                    Log.e("test", "若是文件目录。继续读1" + file.getName().toString() + file.getPath().toString());
                    getFileName(file.listFiles());
                    Log.e("test", "若是文件目录。继续读2" + file.getName().toString() + file.getPath().toString());
                } else {
                    fileName = file.getName();
                    if (fileName.endsWith(".stl") || (fileName.endsWith(".STL"))) {
                        path_picture = file.getAbsolutePath();
                        HashMap<String, Object> map = new HashMap();
//                        String s = fileName.substring(0, fileName.lastIndexOf(".")).toString();
//                        Log.i("test", "文件名stl：：  " + s);
//                        map.put("Name", fileName.substring(0, fileName.lastIndexOf(".")));
                        map.put("Name", fileName);
                        map.put("boolean", false);
                        map.put("filepath",path_picture);
                        myBeanList.add(map);
                    }
                }
            }
        }
    }

    private void copy( String Filepathofcopy,String FileToCopy){
        try {
            FileInputStream fis = new FileInputStream(FileToCopy);
            FileOutputStream fos = new FileOutputStream(Filepathofcopy);
            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            } finally {
                fos.flush();
                fos.close();
                fis.close();
            }
        } catch (IOException e) {
            Log.d("aaa", "Yushengnan tested");
        }
        }
    }
/*    private String getFilePathByName(String seekFileName,File rootFile){
        List<File> files=parseFiles(rootFile);
        for (File file:files){
            if(file.getName().equals(seekFileName)){
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    private List<File> parseFiles(File file){
        List<File> listFiles=new ArrayList<>();
        File[] files = file.listFiles();
        for (File mf:files){
            if(mf.isDirectory()){
                listFiles.addAll(parseFiles(mf));
            }else{
                listFiles.add(mf);
            }

        }
        return listFiles;
    }
    }
*/
