package com.jjpicture.star_android.im.handler;


import com.alibaba.fastjson.JSONObject;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
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

    private Map<String, Handler> handlerMap = new HashMap<>();

    private MediaConstraints pcConstraints = new MediaConstraints();

    private WebRTCClient.Peer peer;

    public SDPMessageHandler() {
        handlerMap.put("init", new InitHandler());
        handlerMap.put("offer", new OfferHandler());
        handlerMap.put("answer", new AnswerHandler());
        handlerMap.put("candidate", new CandidateHandler());
    }

    public void handle(ChatMessageProto.ChatMessageProtocol messageProtocol){
        peer = WebRTCClient.INSTANCE.getPeer(messageProtocol.getFromId());

        String bodyStr = messageProtocol.getBody();
        JSONObject bodyJson = JSONObject.parseObject(bodyStr);
        String type = bodyJson.getString("type");

        Handler handler = handlerMap.get(type);
        if (handler != null) {
            handler.handleMessage(type.equals("init") ? null : bodyJson.getJSONObject("payload"));
        }
    }

    private class InitHandler implements Handler {
        /**
         * 创建offer
         */
        @Override
        public void handleMessage(JSONObject payload) {
            peer.getPeerConnection().createOffer(peer, pcConstraints);
        }
    }

    private class OfferHandler implements Handler {
        /**
         * 设置remoteSDP
         * 创建answer
         * @param payload
         */
        @Override
        public void handleMessage(JSONObject payload) {
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.getPeerConnection().setRemoteDescription(peer, sdp);
            peer.getPeerConnection().createAnswer(peer, pcConstraints);
        }
    }

    private class AnswerHandler implements Handler {
        /**
         * 设置remoteSDP
         * @param payload
         */
        @Override
        public void handleMessage(JSONObject payload) {
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.getPeerConnection().setRemoteDescription(peer, sdp);
        }
    }

    private class CandidateHandler implements Handler {
        @Override
        public void handleMessage(JSONObject payload) {
            PeerConnection pc = peer.getPeerConnection();
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("id"),
                        payload.getInteger("label"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    public interface Handler {
        void handleMessage(JSONObject payload);
    }
}
