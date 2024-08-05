package com.example.demo.rpc;

import com.example.demo.message.GDM;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class NettyServer {
    public NettyServer() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    start(8888);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

//    public void start(int port) throws Exception {
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workerGroup)
//                    // 指定Channel
//                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler())
//                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    .childOption(ChannelOption.TCP_NODELAY, true)
//                    //使用指定的端口设置套接字地址
//                    .localAddress(new InetSocketAddress(port))
//                    //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
//                    .option(ChannelOption.SO_BACKLOG, 1024)
//                    //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
//                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    //将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
//                    .childOption(ChannelOption.TCP_NODELAY, true)
//                    .childHandler(new ChannelInitializer<SocketChannel>() {
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ChannelPipeline p = ch.pipeline();
//                            // 添加长度解码器
//                            p.addLast(new ProtobufVarint32FrameDecoder());
//                            // 添加ProtoBuf解码器
//                            p.addLast(new ProtobufDecoder(GDM.Message.getDefaultInstance()));
//                            p.addLast(new ProtobufVarint32LengthFieldPrepender());
//                            // 添加ProtoBuf编码器
//                            p.addLast(new ProtobufEncoder());
//                            // 添加自定义的业务处理器
//                            p.addLast(new ServerHandler());
//                        }
//                    });
//            System.out.println("=====服务器启动成功1=====");
//            ChannelFuture f = b.bind().sync();
//            System.out.println("=====服务器启动成功2=====");
//            f.channel().closeFuture().sync();
//            System.out.println("=====服务器启动成功3=====");
//        } finally {
//            workerGroup.shutdownGracefully();
//            bossGroup.shutdownGracefully();
//        }
//    }

    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // 指定Channel
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler())
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    //服务端可连接队列数,对应TCP/IP协议listen函数中backlog参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //将小的数据包包装成更大的帧进行传送，提高网络的负载,即TCP延迟传输
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // 添加长度解码器
                            p.addLast(new ProtobufVarint32FrameDecoder());
                            // 添加ProtoBuf解码器
                            p.addLast(new ProtobufDecoder(GDM.Message.getDefaultInstance()));
                            p.addLast(new ProtobufVarint32LengthFieldPrepender());
                            // 添加ProtoBuf编码器
                            p.addLast(new ProtobufEncoder());
                            // 添加自定义的业务处理器
                            p.addLast(new MyServerHandler());
                        }
                    });
            ChannelFuture f = b.bind().sync();
            System.out.println("=====服务器启动成功=====");
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }




}