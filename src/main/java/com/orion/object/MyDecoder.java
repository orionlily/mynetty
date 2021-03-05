package com.orion.object;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author li.lc
 */
public class MyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //字节数据长度
        int len = in.readInt();
        byte[] data = new byte[len];
        in.readBytes(data);
        out.add(data);
    }
}
