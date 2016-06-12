package liwenquan.top.weichat;

/**
 * Created by LWQ on 2016/5/22.
 */
public class Msg {
    public static final int TYPE_RECEIVED=0;
    public static final int TYPE_SENT=1;
    public static final int MESSAGE_TYPE_PALIN=3;
    public static final int MESSAGE_TYPE_AUDIO=4;
    private String content;
    private int type;
    private int msgType;
    private float length;
    public Msg(String content,int type,int msgType){
        this.content=content;
        this.type=type;
        this.msgType=msgType;
    }
    public Msg(float length,String content,int type,int msgType){
        this.length=length;;
        this.content=content;
        this.type=type;
        this.msgType=msgType;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }


    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }
}
