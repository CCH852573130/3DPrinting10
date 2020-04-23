package com.mukesh.drawingview.example;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import utils.SerialPortUtils;

import static com.mukesh.drawingview.example.ZhuJieMian.open;

//蔚晟楠已完成的给打印机发送串口指令的控制 此处集成 布局界面为activity_zhuanyemoshi
public class ZhuanYeMoShi extends AppCompatActivity {
    public double size = 1.0;//倍率选择
    @Override
    /*
    作者：蔚晟楠
    函数作用：初始化函数，handler表示进入页面后每五秒发送一次M105和M114，mRunnable则用来更新UI
    无参数
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuanyemoshi);
        handler.postDelayed(runnable, 5000);//send "M105"and"M114" once every five second
        new Thread(mRunnable).start();// make the latest data visible on UI
    }
    /*
作者：蔚晟楠
函数作用：重写onstop方法，在activity不可见时，关闭runnable对象定时发送任务，防止出现串口堵塞的情况，更新UI不需要听，无所谓
 */
    protected void onStop(){
        super.onStop();
        handler.removeCallbacks(runnable);

    }

//    /*
//    作者：蔚晟楠
//    函数作用：重新onrestart方法，在重新进入activity的时候重新启动定时器，由于程序原因，没有做返回按钮，所以只能onstop
//     */
    protected void onRestart(){
        super.onRestart();
        handler.postDelayed(runnable,5000);
    }
    /*
//    作者：蔚晟楠
//    函数作用：mRunnable对象创建实例，每五秒钟利用handle机制处理消息，如果异常则去处理异常
//     */
    public Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(2000);
                    mHandler.sendMessage(mHandler.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
//
//    /*
//    作者：蔚晟楠
//    函数作用：mHandler对象创建实例，super表示继承
//     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };

//    /*
//    作者：蔚晟楠
//    函数作用：更新UI，首先findViewById方法定位，然后用settext方法更新数值，记得数值在SerialPortUtils里，关键字不要变
//     */
    public void refreshUI(){
        TextView tv1 =  findViewById(R.id.temp_backbed_num);
        TextView tv2 =  findViewById(R.id.temp_extru_num);
        TextView tv3 =  findViewById(R.id.X_posi_num);
        TextView tv4 =  findViewById(R.id.Y_posi_num);
        TextView tv5 =  findViewById(R.id.Z_posi_num);
        tv1.setText(SerialPortUtils.temp_backbed_num1);
        tv2.setText(SerialPortUtils.temp_extrude_num1);
        tv3.setText(SerialPortUtils.X_posi_num1);
        tv4.setText(SerialPortUtils.Y_posi_num1);
        tv5.setText(SerialPortUtils.Z_posi_num1);
    }
//
//    /*
//    作者：蔚晟楠
//    函数作用：发送XY回零指令，前台Onlick对应
//     */
    public void send_XYBACK(View v) { control_command("G28 X0. Y0."); }

//    /*
//    作者：蔚晟楠
//    函数作用：发送Z回零指令，前台Onlick对应
//    */
    public void send_ZBACK(View v) { control_command("G28 Z0."); }
//    /*
//    作者：蔚晟楠
//    函数作用：设置四种倍率size，前台Onlick对应
//    */
    public void setsize1(View v) { size = 0.1; }
//
    public void setsize2(View v) { size = 1.0; }
//
    public void setsize3(View v) { size = 10.0; }
//
    public void setsize4(View v) { size = 50.0; }
//
//    /*
//    作者：蔚晟楠
//    函数作用：发送X增指令，首先判断串口连接状态，前台Onlick对应
//    */
    public void send_Xplus(View v) {
        if (size == 0.1) {
            control_command("G0 X0.1");
        } else if (size == 1.0) {
            control_command("G0 X1.0");
        } else if (size == 10.0) {
            control_command("G0 X10.0");
        } else if (size == 50.0) {
            control_command("G0 X50.0");
        }
    }
//
//    /*
//    作者：蔚晟楠
//    函数作用：发送X减指令，首先判断串口连接状态，前台Onlick对应
//    */
    public void send_Xminus(View v) {
        if (size == 0.1) {
            control_command("G0 X-0.1");
        } else if (size == 1.0) {
            control_command("G0 X-1.0");
        } else if (size == 10.0) {
            control_command("G0 X-10.0");
        } else if (size == 50.0) {
            control_command("G0 X-50.0");
        }
    }

//    /*
//    作者：蔚晟楠
//    函数作用：发送Y增指令，首先判断串口连接状态，前台Onlick对应
//    */
    public void send_Yplus(View v) {
        if (size == 0.1) {
            control_command("G0 Y0.1");
        } else if (size == 1.0) {
            control_command("G0 Y1.0");
        } else if (size == 10.0) {
            control_command("G0 Y10.0");
        } else if (size == 50.0) {
            control_command("G0 Y50.0");
        }
    }

//    /*
//    作者：蔚晟楠
//    函数作用：发送Y减指令，首先判断串口连接状态，前台Onlick对应
//    */
    public void send_Yminus(View v) {
        if (size == 0.1) {
            control_command("G0 Y-0.1");
        } else if (size == 1.0) {
            control_command("G0 Y-1.0");
        } else if (size == 10.0) {
            control_command("G0 Y-10.0");
        } else if (size == 50.0) {
            control_command("G0 Y-50.0");
        }
    }
//
//    /*
//    作者：蔚晟楠
//    函数作用：发送Z增指令，首先判断串口连接状态，前台Onlick对应
//    */
    public void send_Zplus(View v) {
        if (size == 0.1) {
            control_command("G0 Z0.1");
        } else if (size == 1.0) {
            control_command("G0 Z1.0");
        } else if (size == 10.0) {
            control_command("G0 Z10.0");
        } else if (size == 50.0) {
            control_command("G0 Z50.0");
        }
    }
//
//    /*
//    作者：蔚晟楠
//    函数作用：发送Z减指令，首先判断串口连接状态，前台Onlick对应
//    */
    public void send_Zminus(View v) {
        if (size == 0.1) {
            control_command("G0 Z-0.1");
        } else if (size == 1.0) {
            control_command("G0 Z-1.0");
        } else if (size == 10.0) {
            control_command("G0 Z-10.0");
        } else if (size == 50.0) {
            control_command("G0 Z-50.0");
        }
    }

//    /*
//    作者：蔚晟楠
//    函数作用：首先判断是否存在实例，同时设置监听开始收取数据
//    */
//
//
//    /*
//    作者：蔚晟楠
//    函数作用：首先判断是否串口连接正常，并将command转为字节型进行发送
//    参数 command字符串类型的指令
//    */
    public void control_command(String command) {
       byte[] bty = command.getBytes();
        open.sendDataToSerialPort(bty);
        Log.d("Test", "发送成功" + command);
    }

//    /*
//    作者：蔚晟楠
//    函数作用：进入该界面时开始定时发送M105和M114
//    */
    Handler handler =new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 5000);
            control_command("M105");
            control_command("M114");
        }
    };//定时器初始化
}
