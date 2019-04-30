package com.jjpicture.mvvmstar.im_common.enums;

public enum MessageType {
    MESSAGE_TEXT("text", 0),
    MESSAGE_FILE("file", 1),
    MESSAGE_PICTURE("picture", 2),
    //3~7为聊天类型为语音通话时的消息类型
    MESSAGE_CALL("call", 3),
    MESSAGE_INIT("init", 4),
    MESSAGE_OFFER("offer", 5),
    MESSAGE_ANSWER("answer", 6),
    MESSAGE_CANDIDATE("candidate", 7);

    private String type;
    private int index;

    MessageType(String type, int index) {
        this.type = type;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getType(int index) {
        return type;
    }
}
