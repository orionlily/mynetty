package com.orion.hessian;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author li.lc
 */
public class HessianServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        try {
            serverBootstrap.channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)

                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    //.addLast("hessianEncoder", new HessianEncoder())
                                    .addLast("hessianDecoder", new HessianDecoder())
                                    .addLast("myHessianServerHandler", new HessianServerHandler());
                        }
                    });
            System.out.println("服务器准备启动...");
            ChannelFuture channelFuture = serverBootstrap.bind(9680).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("服务器启动成功...");
                } else {
                    System.out.println("服务器启动失败");
                }
            });
            channelFuture.channel().closeFuture().sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("服务器关闭成功...");
                } else {
                    System.out.println("服务器关闭失败...");
                }
            });
        } catch (InterruptedException e) {
            System.out.println("产生异常 " + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
