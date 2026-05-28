/*
package cn.zswltech.mithras.service.config;


import cn.zswltech.mithras.service.auth.websocket.HttpHandShakeIntecepter;
import cn.zswltech.mithras.service.auth.websocket.SocketChannelIntecepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/zswl/message/ws/*")
                //配置握手拦截器
                .addInterceptors(new HttpHandShakeIntecepter())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    */
/**
     * 配置消息代理(中介)
     * enableSimpleBroker 服务端推送给客户端的路径前缀
     * setApplicationDestinationPrefixes  客户端发送数据给服务器端的一个前缀
     *//*

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        registry.enableSimpleBroker("/topic", "/chat");
        registry.setApplicationDestinationPrefixes("/app");

    }

    */
/**
     * 配置 频道拦截器
     *//*

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelIntecepter());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new SocketChannelIntecepter());
    }
}
*/
