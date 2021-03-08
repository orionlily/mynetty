package com.orion.taskqueue.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author li.lc
 */
public class TqDiscardServer {private int port;

    public TqDiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        //每个boss线程需要手动bind一个不同的监听端口，所以我们一般绑定一个端口的话，bossGroup 设置为1个，这样可以避免不浪费内存。
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // (1)

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    //.localAddress(new InetSocketAddress(999))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("encoder",new StringEncoder())
                                    .addLast("decoder",new StringDecoder())
                                    //.addLast("aggregator", new HttpObjectAggregator(512 * 1024))    //
                                    .addLast(new TqDiscardServerHandler());

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            System.out.println("准备启动服务器...");

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)
            f.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        System.out.println("服务器正在启动....");
                    }
                    if (future.isDone()){
                        System.out.println("服务器启动完成....");
                    }
                }
            });
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
            f.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isCancelled()){
                        System.out.println("服务器正在关闭..");
                    }
                    if(future.isCancellable()){
                        System.out.println("服务器已经关闭..OK");
                    }
                }
            });
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new TqDiscardServer(8080).run();
    }
}
