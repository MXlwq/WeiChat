package liwenquan.top.weichat;

import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    static MsgAdapter adapter;
    static List<Msg> msgList=new ArrayList<Msg>();
    private Button mbutton;
    String line;
    Handler handler;
    static ClientThread clientThread;
    private Button mVoice;
    private RecordButton msendvoice;
    private EditText mInputText;
    String ip;
    private int count;
    private MediaRecorder recorder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.mipmap.back);//设置导航栏图标
        setSupportActionBar(toolbar);
        count=0;
        mVoice= (Button) findViewById(R.id.keyboard);
        msendvoice= (RecordButton) findViewById(R.id.send_voice);
        mInputText= (EditText) findViewById(R.id.input_text);
        send=(Button)findViewById(R.id.send);
        mVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count=(count+1)%2;
                if(count==0){
                    mVoice.setBackground(getDrawable(R.drawable.keyboard));
                    msendvoice.setVisibility(View.GONE);
                    mInputText.setVisibility(View.VISIBLE);
                    send.setVisibility(View.VISIBLE);
                }
                else {
                    mVoice.setBackground(getDrawable(R.drawable.voice));
                    msendvoice.setVisibility(View.VISIBLE);
                    mInputText.setVisibility(View.GONE);
                    mInputText.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        msendvoice.setAudioRecord(new AudioRecorder());
        //initMsgs();
        adapter=new MsgAdapter(MainActivity.this,R.layout.msg_item,msgList);
        inputText=(EditText)findViewById(R.id.input_text);

        msgListView=(ListView)findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);

        preferences=getSharedPreferences("Weichatip",MODE_PRIVATE);

        ip=preferences.getString("ipaddress",null);

        //UI线程中实例化一个Hanlder，用来处理发送给主线程MessageQueue的消息

        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {

                if (message.what == 0x1234) {
                    String content=message.obj.toString();
                    Msg msg=new Msg(content,Msg.TYPE_RECEIVED,Msg.MESSAGE_TYPE_PALIN);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    //更新UI
                    //mTextView.append("\r\n" + message.obj.toString());
                }
                else if (message.what == 0x1235) {

                    Msg msg=new Msg("",Msg.TYPE_RECEIVED,Msg.MESSAGE_TYPE_AUDIO);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    //更新UI
                    //mTextView.append("\r\n" + message.obj.toString());
                }
            }
        };

        clientThread = new ClientThread(handler,ip);
        //Toast.makeText(MainActivity.this,ip,Toast.LENGTH_SHORT).show();
        //启动新的线程
        new Thread(clientThread).start();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //revHanlder发送消息给ClientThread的MessageQueue
                    Message message = new Message();
                    message.what = 0x2345;
                    String content=inputText.getText().toString();
                    message.obj = content;
                    clientThread.revHanlder.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(!"".equals(inputText.getText().toString())){
                    Msg msg=new Msg(inputText.getText().toString(),Msg.TYPE_SENT,Msg.MESSAGE_TYPE_PALIN);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                }
            }
        });

    }


}
