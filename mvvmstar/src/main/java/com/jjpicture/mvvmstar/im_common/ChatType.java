package com.jjpicture.mvvmstar.im_common;

public enum ChatType {
    CHAT_SINGLE("单聊", 0),
    CHAT_GROUP("群聊", 1),
    CHAT_VOICECALL("语音通话", 2);

    private String type;
    private int index;

    ChatType(String type, int index){
        this.type = type;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
