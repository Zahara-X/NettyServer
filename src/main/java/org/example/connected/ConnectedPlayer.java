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
    private int id;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        renderer.cameraMap(ctx, msg, id);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.renderer = new PlayedRenderer();
        id = (int)(1 + Math.random() * 200);
        this.renderer.spawnPlayer(ctx, id);
        for(Map.Entry<Integer, PlayedRenderer> players : Server.players.entrySet()) {
            if(players.getKey() != id) {
                players.getValue().spawnPlayer(ctx, id);
            }
        }
        Server.players.put(id, this.renderer);
        Server.group.add(ctx.channel());
        renderer._00000021230032130_(ctx);
        logger.info("Player connected: {}", id);
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Server.players.remove(id);
        Server.group.remove(ctx.channel());
        int length = 8;
        ByteBuf buf = ctx.alloc().buffer(length + 4);
        buf.writeInt(length);
        buf.writeInt(16);
        buf.writeInt(id);
        Server.group.writeAndFlush(buf);
        logger.info("Player disconnected: {}", id);
    }

}