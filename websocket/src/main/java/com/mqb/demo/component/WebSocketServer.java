package com.mqb.demo.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mqb
 * @create 2020-08-20 14:50
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{name}")
public class WebSocketServer {

    /**
     * 与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;

    /**
     * 标识当前连接客户端的用户名
     */
    private String name;

    /**
     * 用于存储所有的连接服务的客户端
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketSet = new ConcurrentHashMap<>();

    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String name){
        this.session = session;
        this.name = name;
        // name是用来标识唯一的客户端，如果需要指定发送，需要指定name来区分
        webSocketSet.put(name, this);
        log.info("[WebSocket]" + name + " 连接成功，当前连接人数为：{}",webSocketSet.size());

        // 给自己回显消息
        this.session.getAsyncRemote().sendText("恭喜您成功连接上WebSocket-->当前在线人数为："+webSocketSet.size());

        // 给群里所有人（不包括新人）发送新人加入提示信息，
        GroupSending("欢迎新人：" + name + "加入----------------------------", name);
    }

    @OnClose
    public void OnClose(){
        webSocketSet.remove(this.name);
        log.info("[WebSockcet] " + name + " 退出成功，当前连接人数：{}", webSocketSet.size());

        // 给群里所有人发人断线的消息
        GroupSending(name + "离线啦-------------------------------" );
    }

    @OnMessage
    public void OnMessage(String message, @PathParam(value = "name") String name){
        log.info("[WebSocket] 收到消息：{}", message);
        message = name + ": " + message; // 变成格式 用户名：消息

        // 判断是否需要指定发送，具体规则自定义
        if(message.indexOf("TOUSER") == 0){
            String toName = message.substring(message.indexOf("TOUSER")+6,message.indexOf(";"));
            AppointSending(toName,message.substring(message.indexOf(";")+1,message.length()));
        } else {
            GroupSending(message);
        }
    }

    /**
     * 群发
     * @param message
     */
    public void GroupSending(String message){
        for (String name : webSocketSet.keySet()){
            try {
                webSocketSet.get(name).session.getBasicRemote().sendText(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void GroupSending(String message,String name){
        for (String n : webSocketSet.keySet()){
            if(n.equals(name)){
                continue;
            }
            try {
                webSocketSet.get(n).session.getBasicRemote().sendText(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定发送
     * @param name
     * @param message
     */
    public void AppointSending(String name,String message){
        try {
            webSocketSet.get(name).session.getBasicRemote().sendText(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
