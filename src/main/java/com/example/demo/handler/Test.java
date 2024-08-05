package com.example.demo.handler;

import com.example.demo.annotation.ToMethod;
import com.example.demo.annotation.ToServer;
import com.example.demo.annotation.route.RouteServer;
import com.example.demo.message.GDM;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
@ToServer("Test")
public class Test extends RouteServer {
    @ToMethod("Login_Request")
    public void p( ChannelHandlerContext ctx, GDM.Request request) {
        GDM.LoginRequest login = request.getLogin();
        System.out.println(ctx + "oooooooooooooo" + login);
    }

    @ToMethod("Test_Request")
    public void ff( ChannelHandlerContext ctx, GDM.Request request) {
        GDM.TestRequest testRequest = request.getTestRequest();
        System.out.println(ctx + "oooooooooooooo" + testRequest);

    }

    @ToMethod("Send_Message_Request")
    public void ddd( ChannelHandlerContext ctx, GDM.Request request) {
        GDM.SendMessageRequest sendMessage = request.getSendMessage();
        System.out.println(ctx + "oooooooooooooo" + sendMessage);
    }

}
