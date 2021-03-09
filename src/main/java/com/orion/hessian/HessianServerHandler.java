package com.orion.hessian;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author li.lc
 */
public class HessianServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有客户端连上：" + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            Miracle miracle = (Miracle) msg;
            System.out.println("收到客户端发来的数据:" + miracle.toString());
            ctx.writeAndFlush(Unpooled.wrappedBuffer("ok,我收到了".getBytes(StandardCharsets.UTF_8)));
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        System.out.println("连接发生异常：" + cause.getMessage());
        ctx.close();
    }
}
