package com.orion.object;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

/**
 * @author li.lc
 */
public class ObjClient {
    public static void main(String[] args) {
        EventLoopGroup client = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(client)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("encoder", new MyEncoder())
                                .addLast("decoder", new MyDecoder())
                                .addLast("myClientHandler", new MyClientHandler());
                    }
                });
        System.out.println("客户端准备启动...");
        try {
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            future.addListener(future1 -> {
                if (future.isSuccess()) {
                    System.out.println("客户端启动中...");
                }
                if (future.isDone()) {
                    System.out.println("客户端启动成功...OK！");
                }
            });

            System.out.println("#################################################");
            System.out.println("客户端信息：" + future.channel().localAddress().toString());
            System.out.println("#################################################");

            Channel channel = future.channel();
            User user = new User();
            user.setId(11L);
            user.setAge(23);
            user.setBirthDay(LocalDateTime.now());
            user.setName("小红");
            channel.writeAndFlush(user);

            channel.closeFuture().sync();
            future.addListener(future1 -> {
                if (future.isCancelled()) {
                    System.out.println("客户端正在关闭..");
                }
                if (future.isCancellable()) {
                    System.out.println("客户端已经关闭..");
                }
            });
        } catch (InterruptedException e) {
            System.out.println("客户端启动失败" + e.getMessage());
        } finally {
            client.shutdownGracefully();
        }
    }
}
