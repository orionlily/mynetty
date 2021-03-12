package com.orion.webSocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author li.lc
 */
public class WebSocketServer {

    private static final int port = 8882;

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("httpRequestDecoder", new HttpRequestDecoder())
                                .addLast("httpResponseDecoder",new HttpResponseEncoder())
                                //将多个消息转换为单一的request或者response对象
                                .addLast("HttpObjectAggregator",new HttpObjectAggregator(10*1024*1024))
                                //支持异步大文件传输
                                .addLast("ChunkedWriteHandler",new ChunkedWriteHandler())
                                .addLast("myWebSocketServerHandler",new MyWebSocketServerHandler());
                    }
                });
        System.out.println("服务器正在启动...");
        try {
            ChannelFuture cf = serverBootstrap.bind(port).sync().addListener(future -> {
                if (future.isSuccess() && future.isDone()){
                    System.out.println("服务器成功启动");
                }
            });
            cf.channel().closeFuture().sync();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }



    }
}
