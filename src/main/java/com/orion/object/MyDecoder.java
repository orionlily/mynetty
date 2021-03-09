package com.orion.object;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author li.lc
 */
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
       /*
       使用java自带字节流
       Object obj = ByteUtil.byteToObject(ByteUtil.read(in));
        out.add(obj);*/

       int len = in.readableBytes();
       byte[] bytes = new byte[len];
       in.readBytes(bytes);

       String msg = new String(bytes,StandardCharsets.UTF_8);

       System.out.println(msg);
        User user = JSONUtil.parse(msg).toBean(User.class);
        out.add(user);
    }
}
