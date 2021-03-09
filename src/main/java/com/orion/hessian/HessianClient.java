package com.orion.hessian;

import com.orion.object.MyDecoder;
import com.orion.object.MyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author li.lc
 */
public class HessianClient {
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
                        ch.pipeline()
                                .addLast("hessianEncoder", new HessianEncoder())
                                //.addLast("hessianDecoder", new HessianDecoder())
                                .addLast("myHessianClientHandler", new HessianClientHandler());
                    }
                });
        System.out.println("客户端准备启动...");
        try {
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9680).sync();
            channelFuture.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("客户端启动成功...");
                }
                if (future.isDone()) {
                    System.out.println("客户端启动完成...OK！");
                }
            });

            System.out.println("#################################################");
            System.out.println("客户端信息：" + channelFuture.channel().localAddress().toString());
            System.out.println("#################################################");

            Channel channel = channelFuture.channel();
            Miracle miracle = new Miracle();
            miracle.setId(11L);
            miracle.setAge(23);
            miracle.setBirthDay(new Date());
            miracle.setName("小红");
            channel.writeAndFlush(miracle);


            channel.closeFuture().sync();
            channelFuture.addListener(future1 -> {
                if (channelFuture.isCancelled()) {
                    System.out.println("客户端正在关闭..");
                }
            });
        } catch (InterruptedException e) {
            System.out.println("客户端启动失败" + e.getMessage());
        } finally {
            client.shutdownGracefully();
        }
    }
}
