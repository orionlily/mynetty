package com.orion.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author li.lc
 */
public class DiscardClientHandler extends ChannelInboundHandlerAdapter {
    public DiscardClientHandler() {
        super();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("客户端【" + ctx.channel() + "】发生了异常：" + cause.getMessage());
    }
}
