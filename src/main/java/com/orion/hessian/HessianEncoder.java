package com.orion.hessian;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author li.lc
 */
public class HessianEncoder extends MessageToByteEncoder<Miracle> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Miracle msg, ByteBuf out) throws Exception {
        byte[] bytes = HessianSerializeUtil.serialize(msg);
        out.writeBytes(bytes);
    }
}
