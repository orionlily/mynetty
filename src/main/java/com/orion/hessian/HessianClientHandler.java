package com.orion.hessian;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author li.lc
 */
public class HessianClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //Miracle miracle = (Miracle) msg;
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("收到服务端发来的数据：" + byteBuf.toString(StandardCharsets.UTF_8));
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
