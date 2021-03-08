package com.orion.taskqueue.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Scanner;

/**
 * @author li.lc
 */
public class TqDiscardClient {

    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("encoder",new StringEncoder())
                                    .addLast("decoder",new StringDecoder())
                                    .addLast(new TqDiscardClientHandler());
                        }
                    });

            System.out.println("客户端准备启动...");
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("客户端启动中...");
                    }
                    if (future.isDone()) {
                        System.out.println("客户端启动成功...OK！");
                    }
                }
            });

            System.out.println("#################################################");
            System.out.println("客户端信息：" + future.channel().localAddress().toString());
            System.out.println("#################################################");

            //控制台输入
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                future.channel().writeAndFlush(Unpooled.wrappedBuffer(scanner.nextLine().getBytes()));
            }
            future.channel().closeFuture().sync();
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) {
                    if(future.isCancelled()){
                        System.out.println("客户端正在关闭..");
                    }
                    if(future.isCancellable()){
                        System.out.println("客户端已经关闭..OK");
                    }
                }
            });
        } catch (InterruptedException e) {
            System.out.println("客户端启动失败" + e.getMessage());
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
