package com.orion.server;

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
public class DiscardServerHandler extends ChannelInboundHandlerAdapter { // (1)

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        System.out.println(ctx.channel().remoteAddress().toString() + " 上线了 ");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        System.out.println(ctx.channel().remoteAddress().toString() + " 离线了 ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        try {
            // Do something with msg

            String sender = ctx.channel().remoteAddress().toString();
            // Discard the received data silently.
            System.out.println("收到来自【" + sender + "】的消息 :" + msg.toString());

            Channel c = ctx.channel();

            for (Channel channel : channels) {
                String s;
                if (channel.equals(c)) {
                    s = "【自己】" + sender + "说：" + msg.toString();
                } else {
                    s = "【用户】" + sender + "说：" + msg.toString();
                }
                ctx.writeAndFlush(s);
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        System.out.println("连接发生异常" + cause.getMessage());
        ctx.close();
    }

    /**
     * 断开连接会触发该消息
     * 同时当前channel 也会自动从ChannelGroup中被移除
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String addr = channel.remoteAddress().toString();
        /**
         * 这里 ChannelGroup 底层封装会遍历给所有的channel发送消息
         *
         */
        channels.writeAndFlush("【用户】 " + addr + " 离开了 ");
        //打印 ChannelGroup中的人数
        System.out.println("当前在线人数是:" + channels.size());
    }
}
