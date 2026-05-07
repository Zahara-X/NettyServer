package org.example.connected;

import io.netty.buffer.ByteBuf;
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
        _0100101001_spawn_(ctx, this.renderer);
        logger.info("Player connected - id: {}", id);
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
        logger.info("Player disconnected - id: {}", id);
    }
    public void _0100101001_spawn_(ChannelHandlerContext ctx, PlayedRenderer renderer) {
        id = (int) (1 + Math.random() * 200); // ид начинается от 1 до 200
        // Добавляем игрока в список map, у каждого уникален ид, желательно использовать UUID, но для практики временно сойдет.
        renderer.spawnPlayer(ctx, id);
        // Добавляем в список!
        Server.players.put(id, renderer);
        for(Map.Entry<Integer, PlayedRenderer> entry : Server.players.entrySet()) {
            if(entry.getKey() != id) { // Предотвращаем создавать старого игрока
                renderer.spawnPlayer(ctx, id);
            }
        }
        /// Добавляем игроков в группу
        Server.group.add(ctx.channel());
        /// Добавляем карту, карта общая
        renderer._1111212_map_(ctx);
    }
    public void _00020120_remove_(ChannelHandlerContext ctx, int drop) {
        Server.players.remove(drop); // Дропаем игрока из списка
    }
}