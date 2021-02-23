package com.orion;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * @author li.lc
 */
public class TestByteBuf {
    public static void main(String[] args) {
        //构造
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello world",
                CharsetUtil.UTF_8);
        System.out.println("byteBuf的容量为：" + byteBuf.capacity());
        System.out.println("byteBuf的可读容量为：" + byteBuf.readableBytes());
        System.out.println("byteBuf的可写容量为：" + byteBuf.writableBytes());
        while (byteBuf.isReadable()){ //⽅法⼀：内部通过移动readerIndex进⾏读取
            System.out.println((char)byteBuf.readByte());
        }
        //⽅法⼆：通过下标直接读取
        for (int i = 0; i < byteBuf.readableBytes(); i++) {
            System.out.println((char)byteBuf.getByte(i));
        }
        //⽅法三：转化为byte[]进⾏读取
        byte[] bytes = byteBuf.array();
        for (byte b : bytes) {
            System.out.println((char)b);
        }
    }
}
