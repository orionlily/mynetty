package com.orion.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author li.lc
 */
public class DiscardServerHandler2 extends ChannelInboundHandlerAdapter { // (1)

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        try {
            // Do something with msg

            String sender = ctx.channel().remoteAddress().toString();
            // Discard the received data silently.
            System.out.println("2号handler收到来自【" + sender + "】的消息 :" + msg.toString());

        } finally {
            //如果有多个handler，需要这样触发下一个handler的执行
            ctx.fireChannelRead(msg);
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        System.out.println("2号handler连接发生异常：" + cause.getMessage());
        ctx.close();
    }
}
