package com.example.demo.rpc;

import com.example.demo.message.GDM;
import io.netty.channel.ChannelHandlerContext;

public interface Process {
    void route(ChannelHandlerContext ctx, GDM.Message request) throws Exception;
}
