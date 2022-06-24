package org.venus.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

public class NettyCodec extends ByteToMessageCodec<FullHttpRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, FullHttpRequest msg, ByteBuf out) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}
