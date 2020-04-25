package com.mukesh.drawingview.example;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MoXingKu extends AppCompatActivity  {
    private GridView mGv;
    static {
        System.loadLibrary("native-lib");
    }

    private static final String FILE_NAME[] = {
            "a.png", "b.png",
            "c.png", "d.png",
            "e.png", "p2.png",
    };
    private static final String stl_NAME[] = {
            "a.STL", "b.STL",
            "c.STL", "d.STL",
            "e.STL", "p2.STL",


    };
    private static List<String> imagePath=new ArrayList<String>();//图片文件的路径
    private static String[] imageFormatSet=new String[]{"jpg","png","gif"};//合法的图片文件格式
    /*
     * 方法:判断是否为图片文件
     * 参数:String path图片路径
     * 返回:boolean 是否是图片文件，是true，否false
     * */
    private static boolean isImageFile(String path){
        for(String format:imageFormatSet){//遍历数组
            if(path.contains(format)){//判断是否为合法的图片文件
                return true;
            }
        }
        return false;
    }
    /*
     * 方法:用于遍历指定路径
     * 参数:String url遍历路径
     * 无返回值
     * */
    private void getFiles(String url){
        File files=new File(url);//创建文件对象
        File[] file=files.listFiles();
        try {
            for(File f:file){//通过for循环遍历获取到的文件数组
                if(f.isDirectory()){//如果是目录，也就是文件夹
                    getFiles(f.getAbsolutePath());//递归调用有点狠
                }else{
                    if(isImageFile(f.getPath())){//如果是图片文件
                        imagePath.add(f.getPath());//将文件的路径添加到List集合中
                    }
                }
            }
            for (int i = 0;i<imagePath.size();i++){
                int j = imagePath.lastIndexOf( imagePath.get(i) );
                if (i != j){
                    imagePath.remove( j );
                    i--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();//输出异常信息
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_moxingku );
        Log.d( "MoXing","oncreate" );
        Log.d("MoXing","taskid:"+getTaskId()+"  ,hash:"+hashCode());
        logtaskName();
        initUI();
        String sdpath = Environment.getExternalStorageDirectory() + "/picture";//获得SD卡中图片的路径
        getFiles( sdpath );//调用getFiles()方法获取SD卡上的全部图片
        if (imagePath.size() < 1) {//如果不存在文件图片
            return;
        }
        mGv = (GridView) findViewById( R.id.gv );
        mGv.setAdapter( new BaseAdapter() {
            @Override
            public int getCount() {
                return imagePath.size();
            }

            class ViewHolder {
                public ImageView imageView;
                public TextView textView;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                ViewHolder holder = null;
                if (view == null) {
                    LayoutInflater mLayoutInflater = LayoutInflater.from( MoXingKu.this );
                    view = mLayoutInflater.inflate( R.layout.layout_grid_item, null );
                    holder = new ViewHolder();
                    holder.imageView = (ImageView) view.findViewById( R.id.iv_grid );
                    holder.textView = (TextView) view.findViewById( R.id.tv_title );
                    view.setTag( holder );
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                //赋值
                String fName = imagePath.get(position).trim();
                String fileName = fName.substring(fName.lastIndexOf("/")+1);
                holder.textView.setText( fileName );
                //为ImageView设置要显示的图片
                Bitmap bm= BitmapFactory.decodeFile(imagePath.get(position));
                holder.imageView.setImageBitmap(bm);
//        Glide.with(mContext).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503734793030&di=10ea8e49217f7a2054f4febf94a164af&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F15%2F24%2F57%2F34s58PICQEq_1024.jpg").into(holder.imageView);
                return view;
            }
        } );
        mGv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                        String fPath = imagePath.get(position).trim();
                        String fileName2 = fPath.substring(fPath.lastIndexOf("/")+1);
                        String stl_path1 = fPath.replace( "png","STL" );
                        String stl_path2 = stl_path1.replace( "picture","stl_file" );
                        String gcode_path1 = fPath.replace( "png","gcode" );
                        String gcode_path2 = gcode_path1.replace( "picture","gcode_file" );
                        stringFromJNI2(stl_path2,gcode_path2);//进行切片操作，接口需要传入stl和gcode路径
//                        Toast.makeText( getApplicationContext(),fileName2+"切片成功",Toast.LENGTH_LONG ).show();
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), DaYinJieMian.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("Data", gcode_path2);//压入数据
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        System.exit( 0 );
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d( "MoXing","onStart" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d( "MoXing","onresume" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d( "MoXing","onpause" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d( "MoXing","onStop" );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d( "MoXing","onrestart" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d( "MoXing","ondestroy" );
    }

    public native String stringFromJNI2(String stl_path, String gcode_path);



    private void initUI() {


        //将drawable文件夹下的文件加载至SD卡中（路径可以按你们需要改）[有难度，好像只有asserts下的才能用IO流]
        File testFolder = new File( Environment.getExternalStorageDirectory() + "/picture");
        if(testFolder.exists() && testFolder.isDirectory() ) {
        Toast.makeText( getApplicationContext(),"图片已经存在",Toast.LENGTH_LONG ).show();
    } else if(!testFolder.exists()) {
        testFolder.mkdir();
// check whether the model files exist in the phone **/
// if not, copy them to there                       **/
        for (int n =0; n < FILE_NAME.length; n++) {
            File modelFile = new File(testFolder, FILE_NAME[n]);
            if (!modelFile.exists()) {
                copyAssetFilesToSDCard(modelFile, FILE_NAME[n]);
            }
        }
        Toast.makeText( getApplicationContext(),"上传图片成功",Toast.LENGTH_LONG ).show();
    }

        //将drawable文件夹下的文件加载至SD卡中（路径可以按你们需要改）[有难度，好像只有asserts下的才能用IO流]
        File testFolder2 = new File( Environment.getExternalStorageDirectory() + "/stl_file");
        File testFolder3 = new File( Environment.getExternalStorageDirectory() + "/gcode_file");
        if(testFolder2.exists() && testFolder2.isDirectory() ) {
            Toast.makeText( getApplicationContext(),"stl已经存在",Toast.LENGTH_LONG ).show();
        } else if(!testFolder2.exists()) {
            testFolder2.mkdir();
// check whether the model files exist in the phone **/
// if not, copy them to there                       **/
            for (int n =0; n < stl_NAME.length; n++) {
                File modelFile2 = new File(testFolder2, stl_NAME[n]);
                if (!modelFile2.exists()) {
                    copyAssetFilesToSDCard(modelFile2, stl_NAME[n]);
                }
            }
            Toast.makeText( getApplicationContext(),"上传stl成功",Toast.LENGTH_LONG ).show();
        }
        if(testFolder3.exists() && testFolder3.isDirectory() ) {
            Toast.makeText( getApplicationContext(),"gcode已经存在",Toast.LENGTH_LONG ).show();
        } else if(!testFolder3.exists()) {
            testFolder3.mkdir();}
}
    private void logtaskName(){
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            Log.d("MoXing",info.taskAffinity);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void copyAssetFilesToSDCard(final File testFileOnSdCard, final String FileToCopy) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {//lastIndexOf去掉后缀名，说实话有点天才
//                    int id =getResources().getIdentifier(FileToCopy, "drawable", getPackageName()); //name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名

//                    InputStream is = getResources().getDrawable(id);
                    InputStream is = getAssets().open(FileToCopy);
                    FileOutputStream fos = new FileOutputStream(testFileOnSdCard);
                    byte[] buffer = new byte[8192];
                    int read;
                    try {
                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                    } finally {
                        fos.flush();
                        fos.close();
                        is.close();
                    }
                } catch (IOException e) {
                    Log.d("aaa", "Can't copy test file onto SD card");
                }
            }
        }).start();
    }
}