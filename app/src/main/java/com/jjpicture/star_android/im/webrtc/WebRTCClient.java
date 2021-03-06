package com.jjpicture.star_android.im.webrtc;

import com.jjpicture.mvvmstar.im_common.enums.ChatType;
import com.jjpicture.mvvmstar.im_common.enums.MessageType;
import com.jjpicture.mvvmstar.im_common.protocol.ChatMessageProto;
import com.jjpicture.mvvmstar.im_common.util.ChatMessageFactory;
import com.jjpicture.mvvmstar.utils.KLog;
import com.jjpicture.star_android.im.client.IMClient;
import com.jjpicture.star_android.im.config.UserConfig;
import com.jjpicture.star_android.im.service.impl.MessageServiceImpl;

import com.alibaba.fastjson.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.HardwareVideoDecoderFactory;
import org.webrtc.HardwareVideoEncoderFactory;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.voiceengine.WebRtcAudioManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author lyl
 * @Title: WebRTCClient
 * @Description: webrtc
 * @date 2019/3/8 15:43
 */
public class WebRTCClient {
//    private final static int MAX_PEER = 2;
//    private Map<String, Peer> peers = new HashMap<>();

    private PeerConnectionFactory peerConnectionFactory;
    private List<PeerConnection.IceServer> iceServers = new LinkedList<>();
    private MediaConstraints mediaConstraints = new MediaConstraints();
    private MediaStream localMS;
    private WebRTCListener webRTCListener;
    private Peer peer;
    private Map<String, Integer> typeMap = new HashMap<>();

    public static WebRTCClient INSTANCE = new WebRTCClient();


    public interface WebRTCListener {
        void onCallReady(Long callId);

        void onStatusChanged(String newStatus);

        void onLocalStream(MediaStream localStream);

        void onAddRemoteStream(MediaStream remoteStream);

        void onRemoveRemoteStream();
    }

    public WebRTCClient() {
//        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
//        PeerConnectionFactory.InitializationOptions.Builder builder = PeerConnectionFactory.InitializationOptions.builder(context);
//        PeerConnectionFactory.InitializationOptions options = builder.createInitializationOptions();
//        PeerConnectionFactory.initialize(options);
//        peerConnectionFactory = new PeerConnectionFactory(options);
        typeMap.put("init", 4);
        typeMap.put("offer", 5);
        typeMap.put("answer", 6);
        typeMap.put("candidate", 7);

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoDecoderFactory(new HardwareVideoDecoderFactory())
                .setVideoEncoderFactory(new HardwareVideoEncoderFactory(true, true))
                .createPeerConnectionFactory();
        iceServers.add(PeerConnection.IceServer.builder("stun:23.21.150.121").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());

        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));

    }

    public void start() {
        localMS = peerConnectionFactory.createLocalMediaStream("ARDAMS");
        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        AudioTrack audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
        localMS.addTrack(audioTrack);

        webRTCListener.onLocalStream(localMS);
    }

    /**
     * 接受
     */
    public void answerCall() {
//        init();
    }

    /**
     * 发起通话
     */
    public void startCall() {
//        init();
    }

    /**
     * 结束通话
     */
    public void endCall() {

    }

    public void setWebRTCListener(WebRTCListener webRTCListener) {
        this.webRTCListener = webRTCListener;
    }

    public class Peer implements SdpObserver, PeerConnection.Observer {
        PeerConnection peerConnection;
        Long userId;

        public Peer(Long userId) {

            peerConnection = peerConnectionFactory.createPeerConnection(iceServers ,this);
            this.userId = userId;
            peerConnection.addStream(localMS);

            webRTCListener.onStatusChanged("CONNECTING");
        }

        public PeerConnection getPeerConnection() {
            return peerConnection;
        }

        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            KLog.d("创建" + sessionDescription.type.canonicalForm() +"成功");
            sendSDPMessage(sessionDescription.type.canonicalForm(), sessionDescription.description);
            //设置localSDP
            peerConnection.setLocalDescription(this, sessionDescription);
        }

        @Override
        public void onCreateFailure(String s) {
            KLog.d(s);
        }

        @Override
        public void onSetSuccess() {
            KLog.d("设置成功");
        }

        @Override
        public void onSetFailure(String s) {
            KLog.d(s);
        }

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                //TODO
                removePeer();
                webRTCListener.onStatusChanged("DISCONNECTED");
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {

        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            JSONObject payload = new JSONObject();
            payload.put("label", iceCandidate.sdpMLineIndex);
            payload.put("id", iceCandidate.sdpMid);
            payload.put("candidate", iceCandidate.sdp);
            sendSDPMessage("candidate", payload.toString());
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            //TODO 添加远程音频流
            KLog.d(userId);
            webRTCListener.onAddRemoteStream(mediaStream);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            removePeer();
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {

        }

        @Override
        public void onRenegotiationNeeded() {

        }

        @Override
        public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
            KLog.d("添加track");
        }

        private void sendSDPMessage(String type, String body) {
            //包装消息
            ChatMessageProto.ChatMessageProtocol messageProtocol =
                    ChatMessageFactory.buildMessage(
                            UserConfig.getUserId(),
                            userId,
                            ChatType.CHAT_VOICECALL.getIndex(),
                            typeMap.get(type),
                            1,
                            body);
            //发送消息
            MessageServiceImpl.getInstance().sendMessage(messageProtocol, IMClient.getChannel(), 3);
        }
    }

    public Peer getPeer(Long userId) {
        if (peer == null) {
            peer = new Peer(userId);
        }
        return peer;
    }

    public void removePeer() {
        peer = null;
        peer.peerConnection.close();
        //TODO 移除远程音频流
        webRTCListener.onRemoveRemoteStream();
    }

    public MediaConstraints getMediaConstraints() {
        return mediaConstraints;
    }
}
