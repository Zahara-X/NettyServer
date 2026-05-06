package org.example.connected;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.example.Server;
import org.example.editions.PlayedRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConnectedPlayer extends SimpleChannelInboundHandler<ByteBuf> {
    private static Logger logger = LoggerFactory.getLogger(ConnectedPlayer.class);
    private PlayedRenderer renderer;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        renderer.cameraMap(ctx, msg);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.renderer = new PlayedRenderer();
        for(Map.Entry<Integer, PlayedRenderer> players : Server.players.entrySet()) {
            if(players.getKey() != ctx.channel().hashCode()) {
                players.getValue().spawnPlayer(ctx);
            }
        }
        Server.players.put(ctx.channel().hashCode(), this.renderer);
        Server.group.add(ctx.channel());
        this.renderer.spawnPlayer(ctx);
        renderer._00000021230032130_(ctx);
        logger.info("Player connected: {}", ctx.channel().hashCode());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Server.players.remove(ctx.channel().hashCode());
        Server.group.remove(ctx.channel());
        logger.info("Player disconnected: {}", ctx.channel().hashCode());
    }

}