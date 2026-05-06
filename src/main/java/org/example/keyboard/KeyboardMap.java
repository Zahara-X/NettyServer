package org.example.keyboard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.Server;
import org.example.editions.PlayedRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class KeyboardMap extends SimpleChannelInboundHandler<ByteBuf> {
    private static Logger logger = LoggerFactory.getLogger(KeyboardMap.class);
    private PlayedRenderer renderer;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        renderer.cameraMap(ctx, msg);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.renderer = new PlayedRenderer();
        renderer.spawnPlayer(ctx);
        renderer._00000021230032130_(ctx);
        Server.group.add(ctx.channel());
        logger.info("Player connected: {}", ctx.channel().hashCode());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Server.group.remove(ctx.channel());
        logger.info("Player disconnected: {}", ctx.channel().hashCode());
    }

}