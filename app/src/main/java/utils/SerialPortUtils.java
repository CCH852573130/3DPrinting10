package utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android_serialport_api.SerialPort;

public class SerialPortUtils {
    private static SerialPortUtils INSTANCE = null;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    private SCMDataReceiveListener SCMDataReceiveListener;
    public static volatile String temp_backbed_num1 = "0.0";
    public static volatile String temp_extrude_num1 = "0.0";
    public static volatile String X_posi_num1 = "0.0";
    public static volatile String Y_posi_num1 = "0.0";
    public static volatile String Z_posi_num1 = "0.0";
    /*
    作者：蔚晟楠
    函数作用：完成单例模式，如果没有实例化则创建一个实例，如果有则使用
     */
    public static SerialPortUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SerialPortUtils();
        }
        return INSTANCE;
    }
    /*
    作者：蔚晟楠
    函数作用：连接指定串口，若有需要遍历所有可用串口，代码见注释，已调试可用
     */
    public int openSerialPort() {
        final String ports = "/dev/ttyS1";
        final int baudRate = 115200;
        try {
            serialPort = new SerialPort(new File(ports), baudRate, 0);
            outputStream = serialPort.getOutputStream();
            inputStream = serialPort.getInputStream();
            new ReadThread().start();//开启读取线程
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

/*        serialPortFinder = new SerialPortFinder();
        final String[] ports = serialPortFinder.getAllDevicesPath();
        Log.d("Test","接受到的串口地址=========" + ports);
        if(ports ==null) {
            return 0;
        }
        final int baudRate = 115200;
        for (String s:ports){
            try {
                serialPort = new SerialPort(new File(s), baudRate, 0);
                break;
            }catch (IOException e){
                e.printStackTrace();
                continue;
            }
        }
        outputStream = serialPort.getOutputStream();
        inputStream = serialPort.getInputStream();
        new ReadThread().start();
        return 1;
    }
*/
    }

    /*    public void closeSerialPort() {
            try {
                inputStream.close();
                outputStream.close();
                serialPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    */
    /*
    作者：蔚晟楠
    函数作用：发送数据到串口
     */
    public void sendDataToSerialPort(byte[] data) {
        try {
            int dataLength = data.length;
            Log.d("DATASIZE", String.valueOf(dataLength));
            if (dataLength > 0) {
                outputStream.write(data);
                outputStream.write('\n');
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    作者：蔚晟楠
    函数作用：读取数据的线程开启，线程中进行正则匹配
     */
    public class ReadThread extends Thread {
        public void run() {
            super.run();
            while (!isInterrupted()) {//接受线程已开启
                int size;
                try {
                    if (inputStream == null) return;
                    byte[] buffer = new byte[1024];
                    size = inputStream.read(buffer);
                    if (size > 0) {
                        /*监听数据接收*/
                        SCMDataReceiveListener.dataRecevie(buffer, size);
                        // UIcontroler.run();
                        String receive = new String(buffer, 0, size);
                        Log.d("Test", "收到接口信息======" + receive);
                        if (receive.startsWith("ok T:")) {
                            temp_backbed_num1 = Match_num(receive, "B\\:[0-9]+(\\.[0-9]+)?");
                            temp_extrude_num1 = Match_num(receive, "T\\:[0-9]+(\\.[0-9]+)?");
                        } else if (receive.startsWith("ok C:")) {
                            X_posi_num1 = Match_num(receive, "X\\:[0-9]+(\\.[0-9]+)?");
                            Y_posi_num1 = Match_num(receive, "Y\\:[0-9]+(\\.[0-9]+)?");
                            Z_posi_num1 = Match_num(receive, "Z\\:[0-9]+(\\.[0-9]+)?");
                        }
                    }//用串口调试助手测，2字符发送17次，3字符9次，4字符8次，大概都发送到36-39字符左右就会出现乱码？这个是串口调试助手的问题！
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }//BUG描述：输入过长字符串就会出现Process xxx terminated.的报错，故做修改
            }

        }
    }

    /*
    作者：蔚晟楠
    函数作用：进行正则匹配，将数字提取出来
     */
    private String Match_num(String s,String regex){
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        while(m.find()){
            return m.group(0).substring(2);
        }
        return "";
    }

    /*
    作者：蔚晟楠
    函数作用：数据接收监听器
     */
    public void setSCMDataReceiveListener(utils.SCMDataReceiveListener SCMDataReceiveListener) {
        this.SCMDataReceiveListener = SCMDataReceiveListener;
    }
}
