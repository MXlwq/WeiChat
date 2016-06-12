package liwenquan.top.weichat;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by LWQ on 2016/5/22.
 */
public class MsgAdapter extends ArrayAdapter<Msg> {
    private MediaPlayer mp;
    private int resourceId;
    View view;
    public MsgAdapter(Context context,  int textViewResourceId, List<Msg> objects) {
        super(context,textViewResourceId, objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Msg msg=getItem(position);
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.msg_item,null);
            viewHolder=new ViewHolder();
            viewHolder.leftLayout=(LinearLayout)view.findViewById(R.id.left_layout);
            viewHolder.rightLayout=(LinearLayout)view.findViewById(R.id.right_layout);
            viewHolder.leftMsg=(TextView)view.findViewById(R.id.left_msg);
            viewHolder.rightMsg=(TextView)view.findViewById(R.id.right_msg);
            viewHolder.leftVoiceMsg=(Button) view.findViewById(R.id.left_voice_msg);
            viewHolder.rightVoiceMsg=(Button)view.findViewById(R.id.right_voice_msg);
            view.setTag(viewHolder);
        }
        else {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        if(msg.getType()==Msg.TYPE_RECEIVED){
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);

            if(msg.getMsgType()==Msg.MESSAGE_TYPE_PALIN) {
                viewHolder.leftMsg.setVisibility(View.VISIBLE);
                viewHolder.leftMsg.setText(msg.getContent());
                viewHolder.leftVoiceMsg.setVisibility(View.GONE);
            }
            else {
                viewHolder.leftMsg.setVisibility(View.GONE);
                viewHolder.leftVoiceMsg.setVisibility(View.VISIBLE);
                viewHolder.leftVoiceMsg.setText(msg.getLength()+"s");
            }
        }
        else if(msg.getType()==Msg.TYPE_SENT){
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);

            if(msg.getMsgType()==Msg.MESSAGE_TYPE_PALIN) {
                viewHolder.rightMsg.setText(msg.getContent());
                viewHolder.rightMsg.setVisibility(View.VISIBLE);
                viewHolder.rightVoiceMsg.setVisibility(View.GONE);
            }
            else {
                viewHolder.rightMsg.setVisibility(View.GONE);
                viewHolder.rightVoiceMsg.setVisibility(View.VISIBLE);
                StringBuffer sb=new StringBuffer("");
                for(int i=0;i<msg.getLength();){
                    sb.append("　");
                    i+=1.5;
                }
                String text=new DecimalFormat(".#").format(msg.getLength())+"s"+sb.toString();
                viewHolder.rightVoiceMsg.setText(text);
            }
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(msg.getMsgType()==Msg.MESSAGE_TYPE_AUDIO){
                    String path=msg.getContent();
                    Intent audioIntent=new Intent(getContext(),AudioService.class);
                    audioIntent.putExtra("audio_name",path);
                    getContext().startService(audioIntent);
                    Toast.makeText(getContext(),path, Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;

    }
    class ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        Button leftVoiceMsg;
        Button rightVoiceMsg;
    }
}
