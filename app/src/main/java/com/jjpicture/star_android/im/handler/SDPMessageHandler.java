package com.jjpicture.star_android.im.handler;


import com.alibaba.fastjson.JSONObject;
import com.jjpicture.mvvmstar.im_common.enums.MessageType;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.webrtc.WebRTCClient;

import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lyl
 * @Title: SDPMessageHandler
 * @Description: SDP消息
 * @date 2019/3/8 16:56
 */
public class SDPMessageHandler {
    /**
     * {
     *     "type" : "";
     *     "payload" : {
     *         "type" :
     *         "sdp" :
     *     }
     * }
     */
    public static SDPMessageHandler INSTANCE = new SDPMessageHandler();

    private Map<Integer, Handler> handlerMap = new HashMap<>(4);

    private Map<Integer, String> typeMap = new HashMap<>(4);

    private MediaConstraints pcConstraints = new MediaConstraints();

    private WebRTCClient.Peer peer;

    public SDPMessageHandler() {
        handlerMap.put(4, new InitHandler());
        handlerMap.put(5, new OfferHandler());
        handlerMap.put(6, new AnswerHandler());
        handlerMap.put(7, new CandidateHandler());

        typeMap.put(4, "init");
        typeMap.put(5, "offer");
        typeMap.put(6, "answer");
        typeMap.put(7, "candidate");

    }

    public void handle(ChatMessageProto.ChatMessageProtocol messageProtocol){
        if (peer == null) {
            peer = WebRTCClient.INSTANCE.getPeer(messageProtocol.getFromId());
        }

        int type = messageProtocol.getMsgType();

        Handler handler = handlerMap.get(type);
        if (handler != null) {
            handler.handleMessage(typeMap.get(type), messageProtocol.getMsgBody().toStringUtf8());
        }
    }

    private class InitHandler implements Handler {
        /**
         * 创建offer
         * @param type
         * @param payload
         */
        @Override
        public void handleMessage(String type, String payload) {
            KLog.d("创建offer");
            peer.getPeerConnection().createOffer(peer, pcConstraints);
        }
    }

    private class OfferHandler implements Handler {
        /**
         * 设置remoteSDP
         * 创建answer
         * @param type
         * @param payload
         */
        @Override
        public void handleMessage(String type, String payload) {
            KLog.d("设置remoteSDP,创建answer");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(type),
                    payload
            );
            peer.getPeerConnection().setRemoteDescription(peer, sdp);
            peer.getPeerConnection().createAnswer(peer, pcConstraints);
        }
    }

    private class AnswerHandler implements Handler {
        /**
         * 设置remoteSDP
         * @param type
         * @param payload
         */
        @Override
        public void handleMessage(String type, String payload) {
            KLog.d("设置remoteSDP");
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(type),
                    payload
            );
            peer.getPeerConnection().setRemoteDescription(peer, sdp);
        }
    }

    private class CandidateHandler implements Handler {
        @Override
        public void handleMessage(String type, String payload) {
            JSONObject jsonObject = JSONObject.parseObject(payload);
            PeerConnection pc = peer.getPeerConnection();
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        jsonObject.getString("id"),
                        jsonObject.getInteger("label"),
                        jsonObject.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    public interface Handler {
        void handleMessage(String type, String payload);
    }
}
