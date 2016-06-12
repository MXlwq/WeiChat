package liwenquan.top.weichat;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

/**
 * Created by LWQ on 2016/5/1.
 */
public class ClientThread implements Runnable {
    public Handler revHanlder;
    BufferedReader br = null;
    InputStreamReader is=null;
    OutputStream os = null;
    FileInputStream reader = null;
    private Socket s;
    private Handler handler;
    String ip;
    String path;

    public ClientThread(Handler handler,String ip) {
        this.handler = handler;
        this.ip=ip;
    }

    @Override
    public void run() {
        try {
            //Socket socket = new Socket("192.168.253.1",7878);
            s = new Socket(ip, 9000);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            os = s.getOutputStream();
            new Thread() {
                @Override
                public void run() {
                    String content = null;
                    try {
                        content = br.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if("Voice".equals(content)){
                        Message msg = new Message();
                        msg.what = 0x1235;
                        handler.sendMessage(msg);
                    }
                    else{
                        try {
                            while ((content = br.readLine()) != null) {
                                Message msg = new Message();
                                msg.what = 0x1234;
                                msg.obj = content;
                                handler.sendMessage(msg);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            Looper.prepare();
            revHanlder = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                    if (msg.what == 0x2345) {
                        try {
                            os.write(("Plain\n"+msg.obj.toString() + "\n").getBytes("UTF-8"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (msg.what == 0x2346) {
                        try {
                            //uploadbar.setMax((int)uploadFile.length());
                            //String souceid = logService.getBindId(uploadFile);
                            path=msg.obj.toString();
                            File uploadFile = new File(path);
                            String head = "Content-Length="+ uploadFile.length() + ";filename="+ uploadFile.getName() +
                                    "\r\n";
                            s = new Socket(ip, 9000);
                            OutputStream outStream = s.getOutputStream();
                            outStream.write(head.getBytes());

                            PushbackInputStream inStream = new PushbackInputStream(s.getInputStream());
                            String response = StreamTool.readLine(inStream);
                            String[] items = response.split(";");
                            String responseid = items[0].substring(items[0].indexOf("=")+1);
                            String position = items[1].substring(items[1].indexOf("=")+1);

                            RandomAccessFile fileOutStream = new RandomAccessFile(uploadFile, "r");
                            fileOutStream.seek(Integer.valueOf(position));
                            byte[] buffer = new byte[1024];
                            int len = -1;
                            int length = Integer.valueOf(position);
                            while((len = fileOutStream.read(buffer)) != -1){
                                outStream.write(buffer, 0, len);
                            }
                            fileOutStream.close();
                            outStream.close();
                            inStream.close();
                            s.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            Looper.loop();
        } catch (IOException e) {
            Log.d("ClientThread", "网络连接超时");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
