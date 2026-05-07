package org.example.editions;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.example.Server;
import java.util.Arrays;

public class PlayedRenderer {
    private final int spawnRandom = (int)(Math.random() * 2365);
    private int cameraX = spawnRandom, cameraY = spawnRandom, speed = 12;
    private static int[][] grid = new int[32][32];
    private final int playerZ = 64, length = 20;
    static {
        // Created map
        for (int[] ints : grid) {
            Arrays.fill(ints, 1);
        }
    }
    public void _1111212_map_(ChannelHandlerContext ctx) {
        int w = grid.length;
        int h = grid[0].length;
        int size = 150;
        int payloadSize = 16 + (w * h * 4);
        ByteBuf buffer = ctx.alloc().buffer(payloadSize + 4);
        buffer.writeInt(payloadSize);
        buffer.writeInt(12);
        buffer.writeInt(w);
        buffer.writeInt(h);
        buffer.writeInt(size); // Размер карты
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                buffer.writeInt(grid[i][j]);
            }
        }
        ctx.writeAndFlush(buffer);
    }
    public void spawnPlayer(ChannelHandlerContext ctx, int id) {
        ByteBuf buffer = ctx.alloc().buffer(length + 4);
        buffer.writeInt(length);
        buffer.writeInt(15);
        buffer.writeInt(playerZ);
        buffer.writeInt(cameraX);
        buffer.writeInt(cameraY);
        buffer.writeInt(id);
        Server.group.writeAndFlush(buffer.retainedDuplicate(), ch -> ch != ctx.channel());
        ctx.writeAndFlush(buffer);

    }
    public void cameraMap(ChannelHandlerContext ctx, ByteBuf buf, int id) {
        while (buf.isReadable()) {
            byte keys = buf.readByte();
            switch (keys) {
                case 2 -> {
                    cameraY -= speed;
                    if (cameraY <= -2365) cameraY = -2365;
                    cameraY(ctx, cameraY, id);
                }
                case 5 -> {
                    cameraY += speed;
                    if (cameraY >= 2365) cameraY = 2365;
                    cameraY(ctx, cameraY, id);
                }
                case 7 -> {
                    cameraX -= speed;
                    if (cameraX <= -2365) cameraX = -2365;
                    cameraX(ctx, cameraX, id);
                }
                case 9 -> {
                    cameraX += speed;
                    if (cameraX >= 2365) cameraX = 2365;
                    cameraX(ctx, cameraX, id);
                }
            }
        }
    }
    public void cameraX(ChannelHandlerContext ctx, int cameraX, int id) {
        int length = 12;
        ByteBuf out_buf = ctx.alloc().buffer(length + 4);
        out_buf.writeInt(length);
        out_buf.writeInt(8); // ID - X
        out_buf.writeInt(id);
        out_buf.writeInt(cameraX);
        Server.group.writeAndFlush(out_buf.retainedDuplicate(), channel -> channel != ctx.channel());
        ctx.writeAndFlush(out_buf);
    }
    public void cameraY(ChannelHandlerContext ctx, int cameraY, int id) {
        int length = 12;
        ByteBuf out_buf = ctx.alloc().buffer(length + 4);
        out_buf.writeInt(length);
        out_buf.writeInt(9); // ID - Y
        out_buf.writeInt(id);
        out_buf.writeInt(cameraY);
        Server.group.writeAndFlush(out_buf.retainedDuplicate(), channel -> channel != ctx.channel());
        ctx.writeAndFlush(out_buf);
    }
}