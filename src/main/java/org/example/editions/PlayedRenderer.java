package org.example.editions;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.example.Server;
import org.example.enums.State;

import java.util.Arrays;

public class PlayedRenderer {
    private final int spawnRandom = (int)(Math.random() * 2365);
    private static ByteBuf buffer;
    private int cameraX = spawnRandom, cameraY = spawnRandom, speed = 12;
    private static final int[][] grid = new int[32][32];
    private static final int playerZ = 64;
    static {
        // Created map
        for (int[] ints : grid) {
            Arrays.fill(ints, 1);
        }
    }
    public void _1111212_map_(ChannelHandlerContext ctx) {
        int w = grid.length, h = grid[0].length;
        int size = 150, idMap = 12;
        int payloadSize = 8 + (w * h * 4);
        ByteBuf buffer = ctx.alloc().buffer(payloadSize + 4);
        int PACKET_MAP = ((w & 0xFF) << 8) | ((h & 0xFF) << 16) | (size & 0xFF);
        buffer.writeInt(payloadSize);
        buffer.writeInt(idMap);
        buffer.writeInt(PACKET_MAP); // Размер карты
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                buffer.writeInt(grid[i][j]);
            }
        }
        ctx.writeAndFlush(buffer);
    }
    public void spawnPlayer(ChannelHandlerContext ctx, int id) {
        int length = 12, idPlayer = 15;
        buffer = ctx.alloc().buffer(length + 4);
        long PACKET_SPAWN = ((playerZ & 0xFFF) << 12) |
                ((long)(cameraX & 0xFFF) << 24) | ((long)(cameraY & 0xFFF) << 36) | (id & 0xFFF); // Выделено 12bit
        buffer.writeInt(length);
        buffer.writeInt(idPlayer); // отправляем ид карты - length
        buffer.writeLong(PACKET_SPAWN);
        Server.group.writeAndFlush(buffer.retainedDuplicate(), ch -> ch != ctx.channel());
        ctx.writeAndFlush(buffer);

    }
    public void cameraMap(ChannelHandlerContext ctx, ByteBuf buf, int id) {
        while (buf.isReadable()) {
            byte keys = buf.readByte();
            // Поменял условие на if правда удобно читать, а чо бы и нет :-)
            if(keys == State.W.getState()) {
                cameraY -= speed;
                if (cameraY <= -2365) cameraY = -2365;
                cameraY(ctx, cameraY, id);
            } else if(keys == State.S.getState()) {
                cameraY += speed;
                if (cameraY >= 2365) cameraY = 2365;
                cameraY(ctx, cameraY, id);
            } else if (keys == State.A.getState()) {
                cameraX -= speed;
                if (cameraX <= -2365) cameraX = -2365;
                cameraX(ctx, cameraX, id);
            } else if (keys == State.D.getState()) {
                cameraX += speed;
                if (cameraX >= 2365) cameraX = 2365;
                cameraX(ctx, cameraX, id);
            }
        }
    }
    public void cameraX(ChannelHandlerContext ctx, int cameraX, int id) {
        int length = 12;
        buffer = ctx.alloc().buffer(length + 4);
        buffer.writeInt(length);
        buffer.writeInt(8); // ID - X
        buffer.writeInt(id);
        buffer.writeInt(cameraX);
        Server.group.writeAndFlush(buffer);
    }
    public void cameraY(ChannelHandlerContext ctx, int cameraY, int id) {
        int length = 12;
        buffer = ctx.alloc().buffer(length + 4);
        buffer.writeInt(length);
        buffer.writeInt(9); // ID - Y
        buffer.writeInt(id);
        buffer.writeInt(cameraY);
        Server.group.writeAndFlush(buffer);
    }
    public void _00011100325_clear(ChannelHandlerContext ctx, int id) {
          int length = 8, idClear = 16;
          buffer = ctx.alloc().buffer(length + 4);
          buffer.writeInt(length);
          buffer.writeInt(idClear);
          buffer.writeInt(id);
          Server.group.writeAndFlush(buffer);
    }
}