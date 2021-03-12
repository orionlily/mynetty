package com.orion.webSocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author li.lc
 */
public class MyWebSocketServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest fhr = (FullHttpRequest) msg;
            /*
            fhr.headers().entries().forEach(item -> {
                System.out.println(item.getKey() + "---" + item.getValue());
            });
            */

                HttpMethod method = fhr.method();
                HttpResponse httpResponse;
                String uri = fhr.uri();

                final StringBuilder result = new StringBuilder("server time：" + LocalDateTime.now() + "，receive params：");

                if (method.equals(HttpMethod.GET)) {
                    QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
                    queryStringDecoder.parameters().forEach((k,v) ->{
                        result.append(k+"="+ v + ";");
                    });
                }


                if (method.equals(HttpMethod.POST)) {
                    //System.out.println(fhr.content());
                    HttpPostRequestDecoder httpPostRequestDecoder = new HttpPostRequestDecoder(fhr);
                    List<InterfaceHttpData> bodyHttpDatas = httpPostRequestDecoder.getBodyHttpDatas();
                    bodyHttpDatas.forEach(item ->{
                        Attribute attribute = (Attribute) item;
                        try {
                            result.append(attribute.getName()+"="+ attribute.getValue() + ";");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                httpResponse = genHttpResp(result.toString());
                ctx.writeAndFlush(httpResponse);
            }

            if (msg instanceof WebSocketFrame) {

            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("发生异常：" + cause.getMessage());
        ctx.close();
    }

    /**
     * 生成httpResp对象
     *
     * @param result
     * @return
     */
    private HttpResponse genHttpResp(String result) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(result,StandardCharsets.UTF_8);
        HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        HttpHeaders headers = httpResponse.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=utf-8");
        headers.set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        return httpResponse;
    }
}
