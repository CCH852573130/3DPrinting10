package com.mukesh.drawingview.example;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

//显示在本地sd卡上储存在某个目录下的图片，每个图片有自己的复选框，选中之后点打印按钮 打印按钮含读取与图片对应的gcode文件功能

public class MoXingKu extends AppCompatActivity  {
    private GridView mGv;

    private static final String FILE_NAME[] = {
            "a.png", "b.png",
            "c.png", "d.png",
            "e.png", "p2.png",
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
    }



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
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
////            case R.id.imageView2:
////                Intent intent7 = new Intent();
////                intent7.setClass(getApplicationContext(), DaYinJieMian.class);
////                startActivity(intent7);
////                break;
//            case R.id.btn1:
//                Intent intent8 = new Intent();
//                intent8.setClass(getApplicationContext(), UPicture.class);
//                startActivity(intent8);
//                break;
//        }
//    }
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