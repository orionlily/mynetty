package com.orion.taskqueue.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author li.lc
 */
public class TqDiscardClientHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("handler:" +System.currentTimeMillis() +"----"+  msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("客户端【" + ctx.channel() + "】发生了异常：" + cause.getMessage());
        ctx.close();
    }
}
