package com.example.demo.client;


import com.example.demo.message.GDM;
import io.netty.channel.ChannelHandlerContext;

public class NettyClientHandler extends io.netty.channel.SimpleChannelInboundHandler<GDM.Message>{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当连接建立时，发送一个Protobuf消息
        System.out.println("---------客户端连接成功出发："+ctx);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GDM.Message msg) throws Exception {
        // 处理从服务器接收到的消息
        System.out.println("客户端收到: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 处理异常
        cause.printStackTrace();
        ctx.close();
    }

}
