package com.orion.object;

import cn.hutool.json.JSONUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author li.lc
 */
public class MyEncoder extends MessageToByteEncoder<User> {

    @Override
    protected void encode(ChannelHandlerContext ctx, User msg, ByteBuf out) throws Exception {
        String jsonStr = JSONUtil.toJsonStr(msg);
        out.writeBytes(jsonStr.getBytes(StandardCharsets.UTF_8));
    }
}
