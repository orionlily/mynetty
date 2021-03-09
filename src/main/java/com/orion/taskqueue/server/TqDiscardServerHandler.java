package com.orion.taskqueue.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author li.lc
 */
public class TqDiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress().toString() + " 上线了 ");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress().toString() + " 离线了 ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        try {
            // Do something with msg

            String sender = ctx.channel().remoteAddress().toString();
            // Discard the received data silently.
            System.out.println("收到来自【" + sender + "】的消息 :" + msg.toString());

            Channel channel = ctx.channel();

            channel.eventLoop().execute(()->{
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx.writeAndFlush(Unpooled.copiedBuffer("服务器的异步消息".getBytes()));
            });

            channel.eventLoop().execute(()->{
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx.writeAndFlush(Unpooled.copiedBuffer("服务器的异步消息2".getBytes()));
            });

            channel.eventLoop().schedule(()->{
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx.writeAndFlush(Unpooled.copiedBuffer("服务器的schedule异步消息".getBytes()));},2, TimeUnit.SECONDS);


            ctx.writeAndFlush(Unpooled.copiedBuffer("服务器的同步消息".getBytes()));

        } finally {
            //如果有多个handler，需要这样触发下一个handler的执行
            ctx.fireChannelRead(msg);
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        System.out.println("连接发生异常：" + cause.getMessage());
        ctx.close();
    }
}
