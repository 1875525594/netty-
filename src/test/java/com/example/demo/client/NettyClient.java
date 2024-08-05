package com.example.demo.client;

import com.example.demo.message.GDM;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class NettyClient {

    private final String host;
    private final int port;
    private static Channel channel;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        // 创建客户端的 EventLoopGroup
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建 Bootstrap 实例
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class) // 使用 NIO 的通道类型
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new ProtobufVarint32FrameDecoder());
                            // 添加ProtoBuf解码器
                            p.addLast(new ProtobufDecoder(GDM.Message.getDefaultInstance()));
                            p.addLast(new ProtobufVarint32LengthFieldPrepender());
                            // 添加ProtoBuf编码器
                            p.addLast(new ProtobufEncoder());
                            p.addLast(new NettyClientHandler());
                        }
                    });

            // 连接到服务器并等待连接完成
            ChannelFuture f = b.connect(host, port).sync();
            NettyClient.channel = f.channel();
            // 等待直到连接被关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅地关闭
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new NettyClient("127.0.0.1", 8888).start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        Thread.sleep(3000);

        for (int i = 0; i < 10; i++) {

            GDM.Message.Builder builder = GDM.Message.newBuilder();
            builder.setMsgType(GDM.MSG.Login_Request);
            builder.setSequence(i);
            builder.setSessionId(i);

            //Request
            GDM.Request.Builder builderRequest = GDM.Request.newBuilder();
            GDM.LoginRequest.Builder loginRequest = GDM.LoginRequest.newBuilder();
            loginRequest.setUsername("dahuang");
            loginRequest.setPassword("123456");
            builderRequest.setLogin(loginRequest);
            GDM.Request requestInstance = builderRequest.buildPartial();
            builder.setRequest(requestInstance);

            //Response

            //Notification

            //message
            GDM.Message message = builder.buildPartial();
            NettyClient.sendDataToServer(channel, message);
            System.out.println("**********************************************");
        }

    }
//    public void builderMessage(GeneratedMessage generatedMessage) {
//        GDM.Request.Builder builderRequest = GDM.Request.newBuilder();
//        Message.Builder oneClass = generatedMessage.toBuilder();
////        oneClass.setField();
//
//
//        builderRequest.setTestRequest(oneClass);
//        GDM.Request requestInstance = builderRequest.buildPartial();
//        builder.setRequest(requestInstance);
//    }
    public static void sendDataToServer(Channel channel, GDM.Message data) {
        // 确保Channel是可写的
        if (channel.isActive()) {
            // 发送数据到服务器
            channel.writeAndFlush(data);
        }
    }
}
