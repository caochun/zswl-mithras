/*
package cn.zswltech.mithras.service.service.message.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.mapper.message.MessageModel;
import cn.zswltech.mithras.service.others.MithrasException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket/{userId}/message")
@Component
@Slf4j
public class WebSocketServer {
    //静态变量，用来记录当前在线连接数。
    private static int onlineCount = 0;

    //用来存放每个客户端id对应的WebSocket对象。
    private static Map<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //客户标识
    private String userId;

    private static final String PING = "ping";

    private static final String PANG = "pang";

    */
/**
     * 连接建立成功调用的方法
     *//*

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if(!webSocketMap.containsKey(userId)){
            //不存在该用户连接，重复登录或断消息重连只更新，不计入连接数
            addOnlineCount();           //在线数加1
        }
        webSocketMap.put(userId,this);     //加入map中
        log.info("A new connection has been added! The current number of online people is " + getOnlineCount());
        sendMessage("连接建立成功");

    }

    */
/**
     * 连接关闭调用的方法
     *//*

    @OnClose
    public void onClose() {
        webSocketMap.remove(userId);  //从set中删除
        subOnlineCount();           //在线数减1
       log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    */
/**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     *//*

    @OnMessage
    public void onMessage(String message, Session session) {
        //心跳机制，维持连接 不做任何处理
        if(PING.equals(message)){
            sendMessage(PANG);
        }
        //目前只推送消息，接收消息暂不处理
        log.info("websocket user message :" + userId + ",报文:" + message);

    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.warn("websocket has error ", error);
    }

    public void sendAsyncMessage(String message)  {
        try {
            this.session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            log.warn("websocket sendMessage has error ", e);
            throw new MithrasException("消息通道异常");
        }
    }

    public void sendMessage(String message)  {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.warn("websocket sendMessage has error ", e);
            throw new MithrasException("消息通道异常");
        }
    }

    */
/**
     * 客户在线则发送通知到前端,异步发送消息
     * @author: jackerhe
     * @date: 2022/7/27 9:37 上午
     **//*

    public static Boolean sendAsyncInfo(MessageModel messageModel) {
        WebSocketServer webSocket = webSocketMap.get(messageModel.getTo());
        if(ObjectUtil.isEmpty(webSocket)){
            //未找到该用户连接
            log.info("user {} not online", messageModel.getTo());
            return Boolean.FALSE;
        }
        webSocket.sendAsyncMessage(JSONObject.toJSONString(messageModel));
        return Boolean.TRUE;
    }

    */
/**
     * 客户在线则发送通知到前端,同步发送消息
     * @author: jackerhe
     * @date: 2022/7/27 9:37 上午
     **//*

    public static Boolean sendInfo(MessageModel messageModel) {
        WebSocketServer webSocket = webSocketMap.get(messageModel.getTo());
        if(ObjectUtil.isEmpty(webSocket)){
            //未找到该用户连接
            return Boolean.FALSE;
        }
        webSocket.sendMessage(JSONObject.toJSONString(messageModel));
        return Boolean.TRUE;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}*/
