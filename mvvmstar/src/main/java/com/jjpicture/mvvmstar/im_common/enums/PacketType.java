package com.jjpicture.mvvmstar.im_common.enums;

public enum PacketType {
    LOGIN_REQ("登陆请求报文",1),
    LOGOUT_REQ("登出请求报文",2),
    P2PMSG_REQ("点对点通信报文",3),
    OFFICIAL_MSG("官方消息报文",4),
    HEARTBEAT("心跳",5)
    ;
    private String name;
    private int index;

    private PacketType(String name,int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for(PacketType pac : PacketType.values()) {
            if(pac.getIndex() == index) {
                return pac.name;
            }
        }
        return null;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
