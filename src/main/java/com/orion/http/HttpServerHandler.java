package com.orion.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author li.lc
 */
public class HttpServerHandler extends ChannelInboundHandlerAdapter { // (1)


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress().toString() + " 上线了 ");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress().toString() + " 离线了 ");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws URISyntaxException { // (2)
        try {
            // Do something with msg
            System.out.println(ctx.handler().toString());
            String sender = ctx.channel().remoteAddress().toString();
            // Discard the received data silently.
            //判断 msg 是不是 httprequest请求
            if(msg instanceof HttpRequest) {

                System.out.println("ctx 类型=" + ctx.getClass());

                System.out.println("pipeline hashcode" + ctx.pipeline().hashCode() + " HttpServerHandler hash=" + this.hashCode());

                System.out.println("msg 类型=" + msg.getClass());
                System.out.println("客户端地址" + ctx.channel().remoteAddress());

                //获取到
                HttpRequest httpRequest = (HttpRequest) msg;
                //获取uri, 过滤指定的资源
                URI uri = new URI(httpRequest.uri());
                System.out.println("uri="+ uri);
                /*if("/favicon.ico".equals(uri.getPath())) {
                    System.out.println("请求了 favicon.ico, 不做响应");
                    return;
                }*/
                //回复信息给浏览器 [http协议]

                ByteBuf content = Unpooled.copiedBuffer("hello, 我是服务器", CharsetUtil.UTF_8);

                //构造一个http的相应，即 httpresponse
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

                System.out.println("=====================================================");
                //将构建好 response返回
                ctx.writeAndFlush(response);
            }

        } finally {
            //如果有多个handler，需要这样触发下一个handler的执行
            //ctx.fireChannelRead(msg);
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
